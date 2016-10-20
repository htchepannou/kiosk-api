package com.tchepannou.kiosk.api.pipeline.publish;

import com.google.common.base.Strings;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.api.jpa.FeedRepository;
import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.PipelineException;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import com.tchepannou.kiosk.core.service.FileService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CreateArticleActivity extends Activity{
    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    FileService fileService;

    @Value("${kiosk.article.slug.maxLength}")
    int slugMaxLength;


    @Override
    protected String getTopic() {
        return PipelineConstants.TOPIC_ARTICLE_SUBMITTED;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        try {

            final PublishRequest request = (PublishRequest) event.getPayload();

            final Article article = toArticle(request);
            final String html = request.getArticle().getContent();
            store(article, html);

            log(article);
            publishEvent(new Event(PipelineConstants.TOPIC_ARTICLE_CREATED, article));

        } catch (Exception e){

            throw new PipelineException(e);

        }
    }

    private Article toArticle (final PublishRequest request){
        final Feed feed = feedRepository.findOne(request.getFeedId());
        final Article article = articleMapper.toArticle(request);
        article.setStatus(Article.Status.submitted);
        article.setFeed(feed);

        if (Strings.isNullOrEmpty(article.getSlug())) {
            article.setSlug(defaultSlug(request));
        }

        return article;
    }

    private void store(final Article article, final String html) throws IOException {
        final String key = article.contentKey(article.getStatus());
        fileService.put(key, new ByteArrayInputStream(html.getBytes("utf-8")));

    }

    private String defaultSlug(final PublishRequest request) {
        final Document doc = Jsoup.parse(request.getArticle().getContent());
        final String text = doc.text();
        return text.length() > slugMaxLength
                ? text.substring(0, slugMaxLength) + "..."
                : text;
    }

    protected void log(final Article article) {
        addToLog(article);
        log.log();
    }
}
