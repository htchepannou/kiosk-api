package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.core.filter.TextFilterSet;
import com.tchepannou.kiosk.core.rule.TextRuleSet;
import com.tchepannou.kiosk.core.rule.Validation;
import com.tchepannou.kiosk.core.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Transactional
public class ProcessArticleActivity extends Activity {
    @Autowired
    FileService fileService;

    @Autowired
    TextFilterSet textFilters;

    @Autowired
    TextRuleSet rules;

    @Autowired
    ArticleRepository articleRepository;

    @Override
    protected String getTopic() {
        return PipelineConstants.TOPIC_ARTICLE_CREATED;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        final Article article = (Article) event.getPayload();
        try {

            // Process HTML
            final String html = fetchContent(article, Article.Status.submitted);
            final String xhtml = textFilters.filter(html);

            // Rules
            final Validation validation = rules.validate(xhtml);
            final Article.Status status = validation.isSuccess() ? Article.Status.processed : Article.Status.rejected;
            final String reason = validation.isSuccess() ? null : validation.getReason();

            // Save all
            storeContent(article, xhtml, status);
            updateStatus(article, status, reason);

            // Next
            log(article, reason, null);
            if (reason == null) {
                publishEvent(new Event(PipelineConstants.TOPIC_ARTICLE_PROCESSED, article));
            }

        } catch (Exception ex){
            log(article, null, ex);
        }
    }

    protected void log(final Article article, final String validationReason, final Throwable ex) {
        log.add("Title", article.getTitle());
        log.add("Url", article.getUrl());
        log.add("Id", article.getId());

        if (ex != null) {
            log.add("Success", false);
            log.add("Exception", ex.getClass().getName());
            log.add("ExceptionMessage", ex.getMessage());
        } else {
            log.add("Success", validationReason == null);
            log.add("Reason", validationReason);
        }

        log.log();
    }

    private String fetchContent(final Article article, final Article.Status status) throws IOException {

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final String key = article.contentKey(status);
        fileService.get(key, out);
        return out.toString();
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
}
