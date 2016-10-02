package com.tchepannou.kiosk.api.pipeline;

import com.tchepannou.kiosk.core.service.LogService;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ActivityTestSupport {
    @Mock
    protected ApplicationEventPublisher publisher;

    @Mock
    protected LogService logService;


    protected void assertThatEventPublished(final String topic, final Object payload){
        verify(publisher).publishEvent(new Event(topic, payload));
    }

    protected void assertThatNoEventPublished(){
        verify(publisher, never()).publishEvent(any());
    }
}
