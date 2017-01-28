package io.tchepannou.kiosk.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.tchepannou.kiosk.api.model.EventModel;
import io.tchepannou.kiosk.api.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Api(basePath = "/v1/event", value = "Event API")
@RequestMapping(value = "/v1/event", produces = MediaType.APPLICATION_JSON_VALUE)
public class EventController {
    @Autowired
    EventService service;

    @ApiOperation("Submit and event")
    @RequestMapping(method = RequestMethod.POST)
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "Success"),
            }
    )
    public void push (@RequestBody EventModel event) throws IOException {
        service.push(event);
    }
}
