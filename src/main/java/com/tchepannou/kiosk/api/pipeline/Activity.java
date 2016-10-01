package com.tchepannou.kiosk.api.pipeline;

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

        log(event);
        doHandleEvent(event);
    }

    public void publishEvent(final Event event) {
        publisher.publishEvent(event);
    }

    protected abstract String getTopic();

    protected abstract void doHandleEvent(final Event event);

    protected String getName() {
        return getClass().getSimpleName();
    }

    private void log(final Event event) {
        log.add("Activity", getName());
        log.add("Topic", event.getTopic());
    }

}
