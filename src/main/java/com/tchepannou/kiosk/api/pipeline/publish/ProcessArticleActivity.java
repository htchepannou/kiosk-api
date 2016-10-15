package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Website;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.PipelineException;
import com.tchepannou.kiosk.api.service.ArticleService;
import com.tchepannou.kiosk.content.ContentExtractor;
import com.tchepannou.kiosk.content.ExtractorContext;
import com.tchepannou.kiosk.content.FilterSetProvider;
import com.tchepannou.kiosk.content.TitleSanitizer;
import com.tchepannou.kiosk.core.service.FileService;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.regex.Pattern;

public class ProcessArticleActivity extends Activity {
    @Autowired
    FileService fileService;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleService articleService;

    @Autowired
    ContentExtractor extractor;

    @Autowired
    TitleSanitizer titleSanitizer;

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

            final Website website = article.getFeed().getWebsite();
            final ExtractorContext ctx = createExtractorContext(website);
            final String html = articleService.fetchContent(article, Article.Status.submitted);
            final String xhtml = extractor.extract(html, ctx);

            store(article, xhtml, ctx);

            log(article);
            publishEvent(new Event(PipelineConstants.TOPIC_ARTICLE_PROCESSED, article));

        } catch (final IOException ex) {
            throw new PipelineException(ex);
        }
    }


    //-- Private
    private ExtractorContext createExtractorContext(final Website website){
        return new ExtractorContext() {
            @Override
            public FilterSetProvider getFilterSetProvider() {
                return filterSetProvider;
            }

            @Override
            public Pattern getTitlePattern() {
                final String regex = website.getTitleSanitizeRegex();
                return StringUtil.isBlank(regex) ? null : Pattern.compile(regex);
            }
        };
    }

    private void store (final Article article, final String xhtml, final ExtractorContext ctx) throws IOException {
        // Store content
        final Article.Status status = Article.Status.processed;
        final String key = article.contentKey(status);
        fileService.put(key, new ByteArrayInputStream(xhtml.getBytes("utf-8")));

        // Update article
        final String title = titleSanitizer.sanitize(article.getTitle(), ctx);
        article.setTitle(title);
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
