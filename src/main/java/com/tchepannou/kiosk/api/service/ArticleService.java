package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.domain.Website;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import com.tchepannou.kiosk.api.mapper.ImageMapper;
import com.tchepannou.kiosk.api.mapper.WebsiteMapper;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
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
import com.tchepannou.kiosk.core.service.TransactionIdProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class ArticleService {
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
    TransactionIdProvider transactionIdProvider;

    @Autowired
    LogService logService;


    //-- Public
    public GetArticleResponse get(final String id) {
        /* article */
        final Article article = findArticle(id);
        if (article == null) {
            return createGetArticleResponse(null, null, null, ErrorConstants.ARTICLE_NOT_FOUND);
        }
        final ArticleDto articleDto = articleMapper.toArticleDto(article);

        /* website */
        final Website website = article.getFeed().getWebsite();
        final WebsiteDto websiteDto = websiteMapper.toWebsiteDto(website);

        /* image */
        final Image image = article.getImage();
        final ImageDto imageDto = image != null ? imageMapper.toImageDto(image) : null;

        /* Get the content */
        final String html = fetchContent(article, article.getStatus());
        if (html == null) {
            return createGetArticleResponse(null, null, null, ErrorConstants.CONTENT_NOT_FOUND);
        }
        articleDto.setContent(html);

        /* result */
        return createGetArticleResponse(articleDto, websiteDto, imageDto, null);
    }

    public GetArticleListResponse status(final String status) {
        final List<Article> articles = articleRepository.findByStatusOrderByPublishedDateDesc(Article.Status.valueOf(status.toLowerCase()));
        return createGetArticleListResponse(articles);
    }

    public PublishResponse publish(final PublishRequest request) throws IOException {
        publisher.publishEvent(new Event(PipelineConstants.TOPIC_ARTICLE_SUBMITTED, request));

        final String articleId = Article.generateId(request.getArticle().getUrl());
        return createPublishResponse(articleId);
    }

    //-- Private
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

    private String fetchContent(final Article article, final Article.Status status) {
        try {

            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final String key = article.contentKey(status);
            fileService.get(key, out);
            return out.toString();

        } catch (final Exception e) {
            logService.add("Exception", e.getClass().getName());
            logService.add("ExceptionMessage", e.getMessage());
            return null;
        }
    }

    private PublishResponse createPublishResponse(final String articleId) {
        final PublishResponse response = new PublishResponse();
        response.setTransactionId(transactionIdProvider.get());
        response.setArticleId(articleId);
        return response;
    }

    private GetArticleResponse createGetArticleResponse(
            final ArticleDto article,
            final WebsiteDto website,
            final ImageDto image,
            final String code
    ) {
        final GetArticleResponse response = new GetArticleResponse();
        response.setArticle(article);
        response.setWebsite(website);
        response.setImage(image);
        response.setTransactionId(transactionIdProvider.get());
        if (code != null) {
            response.setError(new ErrorDto(code));
        }
        return response;
    }

    private GetArticleListResponse createGetArticleListResponse(final List<Article> articles) {
        final List<ArticleDto> dtos = articles
                .stream()
                .map(a -> articleMapper.toArticleDto(a))
                .collect(Collectors.toList());

        final GetArticleListResponse response = new GetArticleListResponse();
        response.setTransactionId(transactionIdProvider.get());
        response.setArticles(dtos);
        return response;
    }
}
