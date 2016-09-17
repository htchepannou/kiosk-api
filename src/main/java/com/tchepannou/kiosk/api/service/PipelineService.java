package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.client.dto.ErrorDto;
import com.tchepannou.kiosk.client.dto.ProcessRequest;
import com.tchepannou.kiosk.client.dto.ProcessResponse;
import com.tchepannou.kiosk.core.filter.TextFilterSet;
import com.tchepannou.kiosk.core.service.LogService;
import com.tchepannou.kiosk.core.service.TransactionIdProvider;
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
    LogService logService;

    @Autowired
    TransactionIdProvider transactionIdProvider;

    @Autowired
    TextFilterSet filters;

    @Transactional
    public ProcessResponse process(final ProcessRequest request) throws IOException {
        final String articelId = request.getArticleId();
        ProcessResponse response = null;
        try {

            // Get data
            final Article article = articleRepository.findOne(request.getArticleId());
            if (article == null){
                response = createProcessResponse(articelId, ErrorConstants.ARTICLE_NOT_FOUND);
            } else {

                // Get content
                final String html = fetchContent(article);

                // Process
                final String xhtml = filters.filter(html);

                // Save all
                storeContent(article, xhtml, Article.Status.processed);
                updateStatus(article, Article.Status.processed);

                response = createProcessResponse(article);
            }

        } catch (final FileNotFoundException ex) {

            response = createProcessResponse(articelId, ErrorConstants.CONTENT_NOT_FOUND);

        } finally {
            log(request, response);
        }

        return response;
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

    private ProcessResponse createProcessResponse(final Article article) {
        return createProcessResponse(article.getId(), null);
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
}
