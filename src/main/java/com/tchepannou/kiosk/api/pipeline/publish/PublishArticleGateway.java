package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.client.dto.ArticleDataDto;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import org.springframework.beans.factory.annotation.Autowired;

public class PublishArticleGateway extends Activity{
    @Autowired
    ArticleRepository articleRepository;

    @Override
    protected String getTopic() {
        return PipelineConstants.TOPIC_ARTICLE_SUBMITTED;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        final PublishRequest request = (PublishRequest)event.getPayload();
        final ArticleDataDto requestArticle = request.getArticle();
        final String id = Article.generateId(requestArticle.getUrl());

        Article article = articleRepository.findOne(id);
        if (article == null) {

            log(true);
            publishEvent(new Event(PipelineConstants.TOPIC_ARTICLE_ACCEPTED, request));

        } else {
            log(false);
        }
    }

    private void log (boolean success){
        log.add("Success", success);
        log.log();
    }
}
