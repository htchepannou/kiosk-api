package com.tchepannou.kiosk.api.service;

import com.codahale.metrics.MetricRegistry;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.domain.Website;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.jpa.FeedRepository;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import com.tchepannou.kiosk.api.mapper.ImageMapper;
import com.tchepannou.kiosk.api.mapper.WebsiteMapper;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.client.dto.ArticleDataDto;
import com.tchepannou.kiosk.client.dto.ArticleDto;
import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.client.dto.ErrorDto;
import com.tchepannou.kiosk.client.dto.GetArticleListResponse;
import com.tchepannou.kiosk.client.dto.GetArticleResponse;
import com.tchepannou.kiosk.client.dto.ImageDto;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import com.tchepannou.kiosk.client.dto.PublishResponse;
import com.tchepannou.kiosk.client.dto.WebsiteDto;
import com.tchepannou.kiosk.core.service.FileService;
import com.tchepannou.kiosk.core.service.LogService;
import com.tchepannou.kiosk.core.service.TimeService;
import com.tchepannou.kiosk.core.service.TransactionIdProvider;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.scheduling.annotation.Scheduled;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ArticleService {
    public static final int NEWS_WINDOW_HOURS = 1*24;

    @Autowired
    protected ApplicationEventPublisher publisher;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    FileService fileService;

    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    ImageMapper imageMapper;

    @Autowired
    WebsiteMapper websiteMapper;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    TransactionIdProvider transactionIdProvider;

    @Autowired
    TimeService timeService;

    @Autowired
    LogService logService;

    @Autowired
    MetricRegistry metricRegistry;

    @Value("${kiosk.article.page.size}")
    int pageSize = 20;

    //-- Public
    public GetArticleResponse get(final String id, final boolean withContent) {
        /* article */
        final Article article = findArticle(id);
        if (article == null) {
            return createGetArticleResponse(null, ErrorConstants.ARTICLE_NOT_FOUND);
        }
        final ArticleDto articleDto = toArticleDto(article);

        /* Get the content */
        if (withContent) {
            final String html = fetchContent(article, article.getStatus());
            if (html == null) {
                return createGetArticleResponse(null, ErrorConstants.CONTENT_NOT_FOUND);
            }
            articleDto.setContent(html);
        }

        /* result */
        return createGetArticleResponse(articleDto, null);
    }

    public GetArticleListResponse findByStatus(final String status, final int page) {
        final PageRequest pagination = new PageRequest(page, pageSize, Sort.Direction.DESC, "score");
        final Date endDate = timeService.now();
        final Date startDate = DateUtils.addHours(endDate, -NEWS_WINDOW_HOURS);
        final List<Article> articles = articleRepository.findByStatusAndPublishedDateBetween(Article.Status.valueOf(status.toLowerCase()), startDate, endDate, pagination);

        return createGetArticleListResponse(articles);
    }

    @Transactional
    public PublishResponse publish(final PublishRequest request) {
        final ArticleDataDto requestArticle = request.getArticle();
        final String articleId = Article.generateId(requestArticle.getUrl());
        final Article article = articleRepository.findOne(articleId);
        if (article != null && !request.isForce()) {
            return createPublishResponse(articleId, ErrorConstants.ALREADY_PUBLISHED);
        }

        final Feed feed = feedRepository.findOne(request.getFeedId());
        if (feed == null) {
            return createPublishResponse(articleId, ErrorConstants.FEED_INVALID);
        }

        // Publish
        publisher.publishEvent(new Event(PipelineConstants.EVENT_CREATE_ARTICLE, request));
        return createPublishResponse(articleId);
    }

    @Transactional
    @Scheduled(cron = "${kiosk.process.cron}")
    public void process(){
        // Get the articles
        final Date endDate = timeService.now();
        final Date startDate = DateUtils.addHours(endDate, -NEWS_WINDOW_HOURS);
        final List<Article> articles = findAllArticles(startDate, endDate, 100, 10);

        // Process
        publisher.publishEvent(new Event(PipelineConstants.EVENT_RANK, articles));
    }

    public String fetchContent(final Article article, final Article.Status status) {
        try {

            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final String key = article.contentKey(status);
            fileService.get(key, out);

            return out.toString("utf-8");

        } catch (final Exception e) {
            logService.add("Exception", e.getClass().getName());
            logService.add("ExceptionMessage", e.getMessage());
            return null;
        }
    }

    //-- Private
    private List<Article> findAllArticles(final Date start, final Date end, final int limit, final int maxIterations){
        final List<Article> articles = new ArrayList<>();
        for (int i=0 ; i<maxIterations ; i++){
            final PageRequest pagination = new PageRequest(i, limit);
            final List<Article> tmp = articleRepository.findByStatusAndPublishedDateBetween(Article.Status.processed, start, end, pagination);
            articles.addAll(tmp);
            if (tmp.size() < limit){
                break;
            }
        }
        return articles;
    }

    private Article findArticle(final String id) {
        return (Article) findById(id, articleRepository);
    }

    private Object findById(final Serializable id, final CrudRepository repository) {
        try {
            return repository.findOne(id);
        } catch (final DataAccessException e) {
            logService.add("Exception", e.getClass().getName());
            logService.add("ExceptionMessage", e.getMessage());
            return null;
        }
    }

    private PublishResponse createPublishResponse(final String articleId) {
        return createPublishResponse(articleId, null);
    }

    private PublishResponse createPublishResponse(final String articleId, final String code) {
        final PublishResponse response = new PublishResponse();
        response.setTransactionId(transactionIdProvider.get());
        response.setArticleId(articleId);
        if (code != null) {
            response.setError(new ErrorDto(code));
        }
        return response;
    }

    private GetArticleResponse createGetArticleResponse(final ArticleDto article, final String code) {
        final GetArticleResponse response = new GetArticleResponse();
        response.setArticle(article);
        response.setTransactionId(transactionIdProvider.get());
        if (code != null) {
            response.setError(new ErrorDto(code));
        }
        return response;
    }

    private GetArticleListResponse createGetArticleListResponse(final List<Article> articles) {
        final List<ArticleDto> articleDtos = articles
                .stream()
                .map(a -> toArticleDto(a))
                .collect(Collectors.toList());

        final GetArticleListResponse response = new GetArticleListResponse();
        response.setTransactionId(transactionIdProvider.get());
        response.setArticles(articleDtos);
        return response;
    }

    private ArticleDto toArticleDto(final Article article) {
        final ArticleDto articleDto = articleMapper.toArticleDto(article);

        final Website website = article.getFeed().getWebsite();
        final WebsiteDto websiteDto = website != null
                ? websiteMapper.toWebsiteDto(website)
                : null;
        articleDto.setWebsite(websiteDto);

        final Image image = article.getImage();
        final ImageDto imageDto = image != null
                ? imageMapper.toImageDto(image)
                : null;
        articleDto.setImage(imageDto);

        return articleDto;
    }
}
