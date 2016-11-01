package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.pipeline.ArticleActivity;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;

import java.io.IOException;

public class ExtractKeywordsActivity extends ArticleActivity {
    @Override
    protected String getTopic() {
        return PipelineConstants.EVENT_EXTRACT_KEYWORDS;
    }

    @Override
    protected String doHandleArticle(final Article article) throws IOException {
        return PipelineConstants.EVENT_SCORE;
    }

}
