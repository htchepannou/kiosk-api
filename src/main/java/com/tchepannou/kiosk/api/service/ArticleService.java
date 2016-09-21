package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.jpa.FeedRepository;
import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import com.tchepannou.kiosk.client.dto.ArticleDataDto;
import com.tchepannou.kiosk.client.dto.ArticleDto;
import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.client.dto.ErrorDto;
import com.tchepannou.kiosk.client.dto.GetArticleListResponse;
import com.tchepannou.kiosk.client.dto.GetArticleResponse;
import com.tchepannou.kiosk.client.dto.ProcessRequest;
import com.tchepannou.kiosk.client.dto.ProcessResponse;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import com.tchepannou.kiosk.client.dto.PublishResponse;
import com.tchepannou.kiosk.core.filter.TextFilterSet;
import com.tchepannou.kiosk.core.service.ContentRepositoryException;
import com.tchepannou.kiosk.core.service.ContentRepositoryService;
import com.tchepannou.kiosk.core.service.LogService;
import com.tchepannou.kiosk.core.service.TransactionIdProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class ArticleService {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ContentRepositoryService contentRepository;

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    TransactionIdProvider transactionIdProvider;

    @Autowired
    LogService logService;

    @Autowired
    TextFilterSet filters;


    //-- Public
    public GetArticleResponse get (final String id) {
        /* article */
        final Article article = findArticle(id);
        if (article == null) {
            return createGetArticleResponse(null, ErrorConstants.ARTICLE_NOT_FOUND);
        }
        final ArticleDto dto = articleMapper.toArticleDto(article);

        /* Get the content */
        String html = fetchContent(article, article.getStatus());
        if (html == null){
            return createGetArticleResponse(null, ErrorConstants.CONTENT_NOT_FOUND);
        }
        dto.getData().setContent(html);

        /* result */
        return createGetArticleResponse(dto, null);
    }

    public GetArticleListResponse status (final String status) {
        final List<Article> articles = articleRepository.findByStatusOrderByPublishedDateDesc(Article.Status.valueOf(status.toLowerCase()));
        return createGetArticleListResponse(articles);
    }

    @Transactional
    public ProcessResponse process(final ProcessRequest request) {
        final String articelId = request.getArticleId();
        ProcessResponse response = null;
        try {

            // Get data
            final Article article = findArticle(request.getArticleId());
            if (article == null){
                response = createProcessResponse(articelId, ErrorConstants.ARTICLE_NOT_FOUND);
                return response;

            }

            // Get content
            final String html = fetchContent(article, Article.Status.submitted);
            if (html == null){
                response = createProcessResponse(articelId, ErrorConstants.CONTENT_NOT_FOUND);
                return response;
            }

            // Process
            final String xhtml = filters.filter(html);

            // Save all
            storeContent(article, xhtml, Article.Status.processed);
            updateStatus(article, Article.Status.processed);

            response = createProcessResponse(article.getId(), null);


        } finally {
            log(request, response);
        }

        return response;
    }

    @Transactional
    public PublishResponse publish(final PublishRequest request) throws IOException {
        PublishResponse response = null;
        String articleId = null;
        try {

            final ArticleDataDto requestArticle = request.getArticle();
            articleId = Article.generateId(requestArticle.getUrl());
            Article article = findArticle(articleId);
            if (article != null) {
                response = createPublishResponse(article, ErrorConstants.ALREADY_PUBLISHED);
                return response;
            }

            final Feed feed = findFeed(request.getFeedId());
            if (feed == null){
                response = createPublishResponse(article, ErrorConstants.FEED_INVALID);
                return response;
            }

            /* store the meta */
            article = articleMapper.toArticle(request);
            article.setStatus(Article.Status.submitted);
            article.setFeed(feed);
            articleRepository.save(article);

            /* store the content */
            try (final InputStream in = new ByteArrayInputStream(requestArticle.getContent().getBytes())) {
                final String key = article.contentKey(article.getStatus());
                contentRepository.write(key, in);
            }

            response = createPublishResponse(article, null);
            return response;

        } finally {

            log(articleId, request, response);

        }
    }



    //-- Private
    private Article findArticle (final String id){
        return (Article)findById(id, articleRepository);
    }

    private Feed findFeed(final long id){
        return (Feed)findById(id, feedRepository);
    }

    private Object findById (final Serializable id, CrudRepository repository){
        try{
            return repository.findOne(id);
        } catch (DataAccessException e){
            logService.add("Exception", e.getClass().getName());
            logService.add("ExceptionMessage", e.getMessage());
            return null;
        }
    }

    private String fetchContent(final Article article, final Article.Status status) {
        try {

            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final String key = article.contentKey(status);
            contentRepository.read(key, out);
            return out.toString();

        } catch (ContentRepositoryException e){
            logService.add("Exception", e.getClass().getName());
            logService.add("ExceptionMessage", e.getMessage());
            return null;
        }
    }

    private void storeContent(final Article article, final String html, final Article.Status status) {
        final String key = article.contentKey(status);
        contentRepository.write(key, new ByteArrayInputStream(html.getBytes()));
    }

    private void updateStatus(final Article article, final Article.Status status) {
        article.setStatus(status);
        articleRepository.save(article);
    }

    private void log(final String articleId, final PublishRequest request, final PublishResponse response) {
        logService.add("FeedId", request.getFeedId());

        final ArticleDataDto article = request.getArticle();
        logService.add("ArticleId", articleId);
        logService.add("ArticleUrl", article.getUrl());
        logService.add("ArticleTitle", article.getTitle());

        if (response != null) {
            logService.add("Success", response.isSuccess());
            if (!response.isSuccess()) {
                final ErrorDto error = response.getError();
                logService.add("ErrorCode", error.getCode());
                logService.add("ErrorMessage", error.getMessage());
            }
        }
    }

    private PublishResponse createPublishResponse(final Article article, final String code) {
        final PublishResponse response = new PublishResponse();
        response.setTransactionId(transactionIdProvider.get());
        if (article != null) {
            response.setArticleId(article.getId());
        }
        if (code != null) {
            response.setError(new ErrorDto(code));
        }
        return response;
    }

    private void log(final ProcessRequest request, final ProcessResponse response) {
        logService.add("ArticelId", request.getArticleId());

        if (response != null) {
            logService.add("Success", response.isSuccess());

            if (!response.isSuccess()) {
                final ErrorDto error = response.getError();
                logService.add("ErrorCode", error.getCode());
                logService.add("ErrorMessage", error.getMessage());
            }
        }
    }

    private ProcessResponse createProcessResponse(final String articleId, final String code) {
        final ProcessResponse response = new ProcessResponse();
        response.setTransactionId(transactionIdProvider.get());
        response.setArticleId(articleId);
        if (code != null){
            response.setError(new ErrorDto(code));
        }
        return response;
    }

    private GetArticleResponse createGetArticleResponse(final ArticleDto article, final String code){
        final GetArticleResponse response = new GetArticleResponse();
        response.setArticle(article);
        response.setTransactionId(transactionIdProvider.get());
        if (code != null){
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
