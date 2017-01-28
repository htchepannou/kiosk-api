package io.tchepannou.kiosk.api.controller;

import io.tchepannou.kiosk.api.model.EventModel;
import io.tchepannou.kiosk.api.service.EventService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EventControllerTest {
    @Mock
    EventService service;

    @InjectMocks
    EventController controller;

    @Test
    public void testPush() throws Exception {
        final EventModel event = new EventModel();

        controller.push(event);

        verify(service).push(event);
    }
}
