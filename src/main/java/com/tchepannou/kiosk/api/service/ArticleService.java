package com.tchepannou.kiosk.api.service;

import com.google.common.base.Strings;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.api.filter.ArticleFilterSet;
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
import com.tchepannou.kiosk.core.rule.TextRuleSet;
import com.tchepannou.kiosk.core.rule.Validation;
import com.tchepannou.kiosk.core.service.FileService;
import com.tchepannou.kiosk.core.service.LogService;
import com.tchepannou.kiosk.core.service.TransactionIdProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    FileService fileService;

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    TransactionIdProvider transactionIdProvider;

    @Autowired
    LogService logService;

    @Autowired
    TextFilterSet textFilters;

    @Autowired
    ArticleFilterSet articleFilters;

    @Autowired
    TextRuleSet rules;

    @Value("${kiosk.article.slug.maxLength}")
    int slugMaxLength;

    //-- Public
    public GetArticleResponse get(final String id) {
        /* article */
        final Article article = findArticle(id);
        if (article == null) {
            return createGetArticleResponse(null, ErrorConstants.ARTICLE_NOT_FOUND);
        }
        final ArticleDto dto = articleMapper.toArticleDto(article);

        /* Get the content */
        final String html = fetchContent(article, article.getStatus());
        if (html == null) {
            return createGetArticleResponse(null, ErrorConstants.CONTENT_NOT_FOUND);
        }
        dto.setContent(html);

        /* result */
        return createGetArticleResponse(dto, null);
    }

    public GetArticleListResponse status(final String status) {
        final List<Article> articles = articleRepository.findByStatusOrderByPublishedDateDesc(Article.Status.valueOf(status.toLowerCase()));
        return createGetArticleListResponse(articles);
    }

    @Transactional
    public ProcessResponse process(final ProcessRequest request) throws IOException {
        final String articelId = request.getArticleId();
        Article article = null;
        ProcessResponse response = null;
        try {

            // Get data
            article = findArticle(request.getArticleId());
            if (article == null) {
                response = createProcessResponse(articelId, ErrorConstants.ARTICLE_NOT_FOUND);
                return response;

            }

            // Get content
            final String html = fetchContent(article, Article.Status.submitted);
            if (html == null) {
                response = createProcessResponse(articelId, ErrorConstants.CONTENT_NOT_FOUND);
                return response;
            }

            // Process
            final String xhtml = textFilters.filter(html);

            // Rules
            final Validation validation = rules.validate(xhtml);
            final Article.Status status = validation.isSuccess() ? Article.Status.processed : Article.Status.rejected;
            final String reason = validation.isSuccess() ? null : validation.getReason();

            // Save all
            storeContent(article, xhtml, status);
            updateStatus(article, status, reason);

            response = createProcessResponse(article.getId(), null);

        } finally {
            log(article, request, response);
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
            if (feed == null) {
                response = createPublishResponse(article, ErrorConstants.FEED_INVALID);
                return response;
            }

            /* article */
            article = articleMapper.toArticle(request);
            article.setStatus(Article.Status.submitted);
            article.setFeed(feed);
            if (Strings.isNullOrEmpty(article.getSlug())) {
                article.setSlug(defaultSlug(request));
            }
            articleFilters.filter(article);

            /* store */
            articleRepository.save(article);

            /* store the content */
            try (final InputStream in = new ByteArrayInputStream(requestArticle.getContent().getBytes())) {
                final String key = article.contentKey(article.getStatus());
                fileService.put(key, in);
            }

            response = createPublishResponse(article, null);
            return response;

        } finally {

            log(articleId, request, response);

        }
    }

    //-- Private
    private String defaultSlug(final PublishRequest request) {
        final Document doc = Jsoup.parse(request.getArticle().getContent());
        final String text = doc.text();
        return text.length() > slugMaxLength
                ? text.substring(0, slugMaxLength) + "..."
                : text;
    }

    private Article findArticle(final String id) {
        return (Article) findById(id, articleRepository);
    }

    private Feed findFeed(final long id) {
        return (Feed) findById(id, feedRepository);
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

    private void storeContent(final Article article, final String html, final Article.Status status) throws IOException {
        final String key = article.contentKey(status);
        fileService.put(key, new ByteArrayInputStream(html.getBytes()));
    }

    private void updateStatus(final Article article, final Article.Status status, final String reason) {
        article.setStatus(status);
        article.setStatusReason(reason);
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

    private void log(final Article article, final ProcessRequest request, final ProcessResponse response) {
        logService.add("ArticelId", request.getArticleId());

        if (article != null) {
            logService.add("ArticelStatus", article.getStatus());
            logService.add("ArticelStatusReason", article.getStatusReason());
        }

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
