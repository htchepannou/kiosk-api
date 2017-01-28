package io.tchepannou.kiosk.api.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.tchepannou.kiosk.api.model.EventModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {
    @Mock
    AmazonSQS sqs;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    EventService service;

    @Before
    public void setUp (){
        service.setQueueUrl("queue");
    }

    @Test
    public void shouldPushMessageToSQS() throws Exception{
        // Given
        final String body = "{\"name\":\"yo\"}";
        when (objectMapper.writeValueAsString(any())).thenReturn(body);

        // When
        service.push(new EventModel());

        // Then
        verify(sqs).sendMessage("queue", body);
    }
}
