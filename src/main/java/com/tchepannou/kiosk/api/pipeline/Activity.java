package com.tchepannou.kiosk.api.pipeline;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.core.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

public abstract class Activity {
    @Autowired
    protected ApplicationEventPublisher publisher;

    @Autowired
    protected LogService log;

    @EventListener
    public void handleEvent(final Event event) {
        if (!event.getTopic().equalsIgnoreCase(getTopic())) {
            return;
        }

        log.add("Step", getName());
        log.add("Topic", event.getTopic());
        try {

            doHandleEvent(event);
            log.add("Success", true);

        } catch (RuntimeException e){
            addToLog(e);
            log.log(e);
        }
    }

    public void publishEvent(final Event event) {
        publisher.publishEvent(event);
    }

    protected abstract String getTopic();

    protected abstract void doHandleEvent(final Event event);

    protected String getName() {
        return getClass().getSimpleName();
    }

    protected void addToLog(final Article article) {
        if (article == null) {
            return;
        }
        log.add("ArticleTitle", article.getTitle());
        log.add("ArticleUrl", article.getUrl());
        log.add("ArticleId", article.getId());
    }

    protected void addToLog(final Image image) {
        if (image == null) {
            return;
        }
        log.add("ImageUrl", image.getUrl());
        log.add("ImageTitle", image.getTitle());
        log.add("ImageWidth", image.getWidth());
        log.add("ImageHeight", image.getHeight());
    }

    protected void addToLog(final Throwable ex) {
        if (ex == null) {
            return;
        }

        log.add("Success", false);
        log.add("Exception", ex.getClass().getName());
        log.add("ExceptionMessage", ex.getMessage());
    }
}
