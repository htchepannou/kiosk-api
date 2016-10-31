package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.pipeline.ArticleActivity;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import org.apache.tika.language.LanguageIdentifier;

public class ExtractLanguageActivity extends ArticleActivity {
    @Override
    protected String getTopic() {
        return PipelineConstants.EVENT_EXTRACT_LANGUAGE;
    }

    @Override
    protected String doHandleArticle(final Article article) {
        final String text = article.getTitle() + "\n" + article.getSlug();
        final LanguageIdentifier li = new LanguageIdentifier(text);

        article.setLanguageCode(li.getLanguage());

        return PipelineConstants.EVENT_VALIDATE;
    }
}
