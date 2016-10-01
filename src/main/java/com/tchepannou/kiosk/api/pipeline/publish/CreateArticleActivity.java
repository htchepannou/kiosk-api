package com.tchepannou.kiosk.api.pipeline.publish;

import com.google.common.base.Strings;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.api.filter.ArticleFilterSet;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.jpa.FeedRepository;
import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import com.tchepannou.kiosk.core.service.FileService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Transactional
public class CreateArticleActivity extends Activity{
    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    ArticleFilterSet articleFilters;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    FileService fileService;

    @Value("${kiosk.article.slug.maxLength}")
    int slugMaxLength;


    @Override
    protected String getTopic() {
        return PipelineConstants.TOPIC_ARTICLE_ACCEPTED;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        final PublishRequest request = (PublishRequest)event.getPayload();
        final Feed feed = feedRepository.findOne(request.getFeedId());

        Article article = articleMapper.toArticle(request);
        article.setStatus(Article.Status.submitted);
        article.setFeed(feed);

        if (Strings.isNullOrEmpty(article.getSlug())) {
            article.setSlug(defaultSlug(request));
        }
        articleFilters.filter(article);

        /* store the content */
        try (final InputStream in = new ByteArrayInputStream(request.getArticle().getContent().getBytes())) {
            // Save the article
            articleRepository.save(article);

            // Store the content
            final String key = article.contentKey(article.getStatus());
            fileService.put(key, in);

            // next
            log(article, null);
            publishEvent(new Event(PipelineConstants.TOPIC_ARTICLE_CREATED, article));

        } catch(Exception ex){
            log(article, ex);
        }

    }

    private String defaultSlug(final PublishRequest request) {
        final Document doc = Jsoup.parse(request.getArticle().getContent());
        final String text = doc.text();
        return text.length() > slugMaxLength
                ? text.substring(0, slugMaxLength) + "..."
                : text;
    }

    protected void log(final Article article, final Throwable ex) {
        log.add("Title", article.getTitle());
        log.add("Url", article.getUrl());
        log.add("Id", article.getId());

        if (ex != null) {
            log.add("Success", false);
            log.add("Exception", ex.getClass().getName());
            log.add("ExceptionMessage", ex.getMessage());
            log.log(ex);
        } else {
            log.add("Success", true);
            log.log();
        }
    }
}
