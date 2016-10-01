package com.tchepannou.kiosk.api.pipeline;

public class Event {
    private final String topic;
    private final Object payload;

    public Event(final String topic, final Object payload) {
        this.topic = topic;
        this.payload = payload;
    }

    public String getTopic() {
        return topic;
    }

    public Object getPayload() {
        return payload;
    }
}
