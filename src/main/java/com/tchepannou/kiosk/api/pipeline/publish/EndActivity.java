package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import org.springframework.beans.factory.annotation.Autowired;

public class EndActivity extends Activity{
    @Autowired
    ArticleRepository articleRepository;

    @Override
    protected String getTopic() {
        return PipelineConstants.EVENT_END;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        final Object payload = event.getPayload();
        if (payload instanceof Article){
            articleRepository.save((Article)payload);
        } else if (payload instanceof Iterable){
            articleRepository.save((Iterable)payload);
        }
    }
}
