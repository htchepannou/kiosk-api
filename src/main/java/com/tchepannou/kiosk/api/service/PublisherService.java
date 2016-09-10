package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.dto.PublishRequestDto;
import com.tchepannou.kiosk.api.dto.PublishResponseDto;
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
    public PublishResponseDto publish(final PublishRequestDto request) throws IOException {
        final String keyhash = Article.generateKeyhash(request.getUrl());
        if (alreadyPublished(keyhash)) {
            return createErrorResponse("already_submitted");
        }

        /* store the meta */
        final Article article = articleMapper.toArticle(request);
        article.setStatus(Article.Status.submitted);
        articleRepository.save(article);

        /* store the content */
        try (final InputStream in = new ByteArrayInputStream(request.getContent().getBytes())) {
            final String key = article.contentKey(article.getStatus());
            contentRepositoryService.write(key, in);
        }

        return createResponse(article);
    }

    private boolean alreadyPublished(final String keyhash) {
        try {
            Article article = articleRepository.findOne(keyhash);
            return article != null;
        } catch (final DataAccessException e) {
            return true;
        }
    }

    private PublishResponseDto createErrorResponse(final String code) {
        final PublishResponseDto response = new PublishResponseDto();
        response.setSuccess(true);
        response.setErrorCode(code);
        return response;
    }

    private PublishResponseDto createResponse(final Article article) {
        final PublishResponseDto response = new PublishResponseDto();
        response.setSuccess(true);
        response.setErrorCode("OK");
        response.setTransactionId(article.getKeyhash());
        return response;
    }
}
