package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.client.dto.ErrorDto;
import com.tchepannou.kiosk.client.dto.ProcessRequest;
import com.tchepannou.kiosk.client.dto.ProcessResponse;
import com.tchepannou.kiosk.core.filter.TextFilterSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PipelineService {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ContentRepositoryService contentRepository;

    @Autowired
    TextFilterSet filters;

    @Transactional
    public ProcessResponse process(final ProcessRequest request) throws IOException {
        final String keyhash = request.getKeyhash();
        try {

            // Get data
            final Article article = articleRepository.findOne(request.getKeyhash());
            if (article == null){
                return createProcessResponse(keyhash, ErrorConstants.ARTICLE_NOT_FOUND);
            }

            // Get content
            final String html = fetchContent(article);

            // Process
            final String xhtml = filters.filter(html);

            // Save all
            storeContent(article, xhtml, Article.Status.processed);
            updateStatus(article, Article.Status.processed);

            return createProcessResponse(article);

        } catch (final FileNotFoundException ex) {

            return createProcessResponse(keyhash, ErrorConstants.CONTENT_NOT_FOUND);

        }
    }

    private String fetchContent(final Article article) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final String key = article.contentKey(Article.Status.submitted);
        contentRepository.read(key, out);
        return out.toString();
    }

    private void storeContent(final Article article, final String html, final Article.Status status) throws IOException {
        final String key = article.contentKey(status);
        contentRepository.write(key, new ByteArrayInputStream(html.getBytes()));
    }

    private void updateStatus(final Article article, final Article.Status status) {
        article.setStatus(status);
        articleRepository.save(article);
    }

    private ProcessResponse createProcessResponse(final Article article) {
        final ProcessResponse response = new ProcessResponse();
        response.setTransactionId(article.getKeyhash());
        return response;
    }

    private ProcessResponse createProcessResponse(final String keyhash, final String code) {
        final ProcessResponse response = new ProcessResponse();
        response.setTransactionId(keyhash);
        if (code != null){
            response.setError(new ErrorDto(code));
        }
        return response;
    }
}
