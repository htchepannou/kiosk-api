package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import com.tchepannou.kiosk.client.dto.ArticleDto;
import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.client.dto.ErrorDto;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import com.tchepannou.kiosk.client.dto.PublishResponse;
import com.tchepannou.kiosk.core.service.LogService;
import com.tchepannou.kiosk.core.service.TransactionIdProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PublisherService {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ContentRepositoryService contentRepositoryService;

    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    LogService logService;

    @Autowired
    TransactionIdProvider transactionIdProvider;

    @Transactional
    public PublishResponse publish(final PublishRequest request) throws IOException {
        PublishResponse response = null;
        try {

            final ArticleDto requestArticle = request.getArticle();
            final String keyhash = Article.generateKeyhash(requestArticle.getUrl());
            Article article = findArticle(keyhash);
            if (article != null) {
                response = createResponse(article, ErrorConstants.ALREADY_PUBLISHED);
                return response;
            }

            /* store the meta */
            article = articleMapper.toArticle(request);
            article.setStatus(Article.Status.submitted);
            articleRepository.save(article);

            /* store the content */
            try (final InputStream in = new ByteArrayInputStream(requestArticle.getContent().getBytes())) {
                final String key = article.contentKey(article.getStatus());
                contentRepositoryService.write(key, in);
            }

            response = createResponse(article, null);
            return response;

        } finally {

            log(request, response);

        }
    }

    private void log(final PublishRequest request, final PublishResponse response) {
        logService.add("FeedId", request.getFeedId());

        final ArticleDto article = request.getArticle();
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

    private Article findArticle(final String keyhash) {
        try {
            return articleRepository.findOne(keyhash);
        } catch (final DataAccessException e) {
            return null;
        }
    }

    private PublishResponse createResponse(final Article article, final String code) {
        final PublishResponse response = new PublishResponse();
        response.setKeyhash(article.getKeyhash());
        response.setTransactionId(transactionIdProvider.get());
        if (code != null) {
            response.setError(new ErrorDto(code));
        }
        return response;
    }
}
