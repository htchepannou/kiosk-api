package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import org.apache.tika.language.LanguageIdentifier;

public class ExtractLanguageActivity extends Activity {
    @Override
    protected String getTopic() {
        return PipelineConstants.EVENT_EXTRACT_LANGUAGE;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        final Article article = (Article)event.getPayload();
        final String text = article.getTitle() + "\n" + article.getSlug();
        final LanguageIdentifier li = new LanguageIdentifier(text);

        article.setLanguageCode(li.getLanguage());

        publishEvent(new Event(PipelineConstants.EVENT_VALIDATE, article));
    }
}
