package com.tchepannou.kiosk.api.pipeline;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.io.IOException;

public abstract class ArticleActivity extends Activity {
    @Autowired
    ArticleRepository articleRepository;

    protected abstract String doHandleArticle(final Article article) throws IOException;

    @Override
    @Transactional
    public final void doHandleEvent(final Event event) {
        // Handle the event
        final Article article = (Article) event.getPayload();
        String next = null;
        try {
            next = doHandleArticle(article);
            articleRepository.save(article);
        } catch (Exception e){
            addToLog(e);
        } finally {
            addToLog(article);
            log.log();
        }

        // Next event
        if (next != null) {
            publishEvent(new Event(next, article));
        }
    }
}
