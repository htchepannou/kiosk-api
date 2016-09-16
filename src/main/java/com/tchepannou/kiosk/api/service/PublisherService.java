package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.client.dto.ArticleDto;
import com.tchepannou.kiosk.client.dto.ErrorDto;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import com.tchepannou.kiosk.client.dto.PublishResponse;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.mapper.ArticleMapper;
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

    @Transactional
    public PublishResponse publish(final PublishRequest request) throws IOException {
        final ArticleDto requestArticle = request.getArticle();
        final String keyhash = Article.generateKeyhash(requestArticle.getUrl());
        Article article = findArticle(keyhash);
        if (article != null) {
            return createResponse(article, ErrorConstants.ALREADY_PUBLISHED);
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

        return createResponse(article, null);
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
        response.setTransactionId(article.getKeyhash());
        if (code != null){
            response.setError(new ErrorDto(code, code));
        }
        return response;
    }
}
