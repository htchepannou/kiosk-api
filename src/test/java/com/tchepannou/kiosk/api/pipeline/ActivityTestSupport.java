package com.tchepannou.kiosk.api.pipeline;

import com.tchepannou.kiosk.core.service.LogService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ActivityTestSupport {
    @Mock
    protected ApplicationEventPublisher publisher;

    @Mock
    protected LogService logService;


    protected void assertThatEventPublished(final String topic, final Object payload){
        final ArgumentCaptor<Event> event = ArgumentCaptor.forClass(Event.class);
        verify(publisher).publishEvent(event.capture());

        assertThat(event.getValue().getTopic()).isEqualTo(topic);
        assertThat(event.getValue().getPayload()).isEqualTo(payload);
    }

    protected void assertThatNoEventPublished(){
        verify(publisher, never()).publishEvent(any());
    }
}
