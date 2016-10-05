package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.config.BeanConstants;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.core.filter.TextFilterSet;
import com.tchepannou.kiosk.core.rule.TextRuleSet;
import com.tchepannou.kiosk.core.rule.Validation;
import com.tchepannou.kiosk.core.service.FileService;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProcessArticleActivity extends Activity {
    @Autowired
    FileService fileService;

    @Autowired
    @Qualifier(BeanConstants.BEAN_ARTICLE_PROCESSOR_FILTER_SET)
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

            // Process content
            final Validation validation = rules.validate(xhtml);
            final Article.Status status = validation.isSuccess() ? Article.Status.processed : Article.Status.rejected;
            final String reason = validation.isSuccess() ? null : validation.getReason();
            storeContent(article, xhtml, status);

            // Update article
            article.setStatus(status);
            article.setStatusReason(reason);
            article.setContentLength(length(xhtml));
            articleRepository.save(article);

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
        log.add("Reason", validationReason);
        addToLog(article);
        addToLog(ex);
        log.log(ex);
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

    private int length(final String xhtml){
        return xhtml != null ? Jsoup.parse(xhtml).text().length() : 0;
    }
}
