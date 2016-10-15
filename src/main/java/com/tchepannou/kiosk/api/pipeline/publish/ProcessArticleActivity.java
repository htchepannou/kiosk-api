package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.PipelineException;
import com.tchepannou.kiosk.api.service.ArticleService;
import com.tchepannou.kiosk.content.ContentExtractor;
import com.tchepannou.kiosk.content.ExtractorContext;
import com.tchepannou.kiosk.content.FilterSetProvider;
import com.tchepannou.kiosk.core.service.FileService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ProcessArticleActivity extends Activity implements ExtractorContext {
    @Autowired
    FileService fileService;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleService articleService;

    @Autowired
    ContentExtractor extractor;

    @Autowired
    FilterSetProvider filterSetProvider;


    //-- Activity overrides
    @Override
    protected String getTopic() {
        return PipelineConstants.TOPIC_ARTICLE_CREATED;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        final Article article = (Article) event.getPayload();
        try {

            final String html = articleService.fetchContent(article, Article.Status.submitted);
            final String xhtml = extractor.extract(html, this);

            store(article, xhtml);

            log(article);
            publishEvent(new Event(PipelineConstants.TOPIC_ARTICLE_PROCESSED, article));

        } catch (final IOException ex) {
            throw new PipelineException(ex);
        }
    }


    //-- ExtractorContext overrides
    @Override
    public FilterSetProvider getFilterSetProvider() {
        return filterSetProvider;
    }

    //-- Private
    private void store (final Article article, final String xhtml) throws IOException {
        // Store content
        final Article.Status status = Article.Status.processed;
        final String key = article.contentKey(status);
        fileService.put(key, new ByteArrayInputStream(xhtml.getBytes("utf-8")));

        // Update article
        article.setStatus(status);
        article.setContentLength(length(xhtml));
        article.setContentCssId(findContentCssId(xhtml));
        articleRepository.save(article);

    }

    private void log(final Article article) {
        addToLog(article);
        log.log();
    }

    private int length(final String xhtml) {
        return xhtml != null ? Jsoup.parse(xhtml).text().trim().length() : 0;
    }

    private String findContentCssId(final String html) {
        final Element xelt = Jsoup.parse(html).body();
        final Elements children = xelt.children();
        if (children.isEmpty()) {
            return null;
        }
        return children.get(0).id();
    }
}
