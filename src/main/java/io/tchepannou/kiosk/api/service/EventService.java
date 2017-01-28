package io.tchepannou.kiosk.api.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.tchepannou.kiosk.api.model.EventModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class EventService {
    @Autowired
    AmazonSQS sqs;

    @Autowired
    ObjectMapper objectMapper;

    private String queueUrl;

    public void push(EventModel event) throws IOException {
        final String body = objectMapper.writeValueAsString(event);
        sqs.sendMessage(queueUrl, body);
    }

    public String getQueueUrl() {
        return queueUrl;
    }

    public void setQueueUrl(final String queueUrl) {
        this.queueUrl = queueUrl;
    }
}
