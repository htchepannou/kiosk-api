package com.tchepannou.kiosk.api.pipeline;

import com.codahale.metrics.MetricRegistry;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.core.service.LogService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

public abstract class Activity {
    @Autowired
    protected ApplicationEventPublisher publisher;

    @Autowired
    protected LogService log;

    @Autowired
    private MetricRegistry metricRegistry;

    @EventListener
    public void handleEvent(final Event event) {
        if (!event.getTopic().equalsIgnoreCase(getTopic())) {
            return;
        }

        log.add("Step", getName());
        log.add("Topic", event.getTopic());
        log.add("Success", true);
        try {

            doHandleEvent(event);
            markMeter("Article");

        } catch (final RuntimeException e) {
            addToLog(e);
            markMeter("Error");
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

    protected void addToLog(final Throwable ex) {
        if (ex == null) {
            return;
        }

        log.add("Success", false);
        log.add("Exception", ex.getClass().getName());
        log.add("ExceptionMessage", ex.getMessage());

        LoggerFactory.getLogger(getClass()).error("Unexpected error", ex);
    }

    protected void markMeter(final String name) {
        metricRegistry.meter(MetricRegistry.name("Kiosk.Pippeline.Publish", name)).mark();
    }

    protected void markMeter(final String name, final int value) {
        metricRegistry.meter(MetricRegistry.name("Kiosk.Pippeline.Publish", name)).mark(value);
    }
}
