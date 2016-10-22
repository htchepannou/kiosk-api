package com.tchepannou.kiosk.api.controller;

import com.tchepannou.kiosk.api.service.WebsiteService;
import com.tchepannou.kiosk.client.dto.GetWebsiteListResponse;
import com.tchepannou.kiosk.client.dto.GetWebsiteResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(basePath = "/kiosk/v1/websites", value = "Content Website API")
@RequestMapping(value = "/kiosk/v1/websites", produces = MediaType.APPLICATION_JSON_VALUE)
public class WebsiteController {
    @Autowired
    WebsiteService service;

    @ApiOperation("Return the website list")
    @RequestMapping(method = RequestMethod.GET)
    public GetWebsiteListResponse all() {
        return service.all();
    }

    @ApiOperation("Return a website")
    @RequestMapping(value="/{websiteId}", method = RequestMethod.GET)
    public GetWebsiteResponse get(@PathVariable("websiteId") long websiteId) {
        return service.get(websiteId);
    }
}
