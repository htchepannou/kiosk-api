package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.pipeline.ArticleActivity;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import org.springframework.beans.factory.annotation.Autowired;

public class EndActivity extends ArticleActivity {
    @Autowired
    ArticleRepository articleRepository;

    @Override
    protected String getTopic() {
        return PipelineConstants.EVENT_END;
    }

    @Override
    protected String doHandleArticle(final Article article) {
        return null;
    }
}
