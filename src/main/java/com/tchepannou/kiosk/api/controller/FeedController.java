package com.tchepannou.kiosk.api.controller;

import com.tchepannou.kiosk.api.dto.FeedListResponseDto;
import com.tchepannou.kiosk.api.service.FeedService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(basePath = "/kiosk/v1/feeds", value = "Content Feed API")
@RequestMapping(value = "/kiosk/v1/feeds", produces = MediaType.APPLICATION_JSON_VALUE)
public class FeedController {
    @Autowired
    FeedService service;

    @ApiOperation("Return the feed list")
    @RequestMapping(method = RequestMethod.GET)
    public FeedListResponseDto all() {
        return service.all();
    }
}
