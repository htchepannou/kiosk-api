package com.tchepannou.kiosk.api.controller;

import com.tchepannou.kiosk.client.dto.PublishRequest;
import com.tchepannou.kiosk.client.dto.PublishResponse;
import com.tchepannou.kiosk.api.service.PipelineService;
import com.tchepannou.kiosk.api.service.PublisherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Api(basePath = "/kiosk/v1/articles", value = "Article Publisher API")
@RequestMapping(value = "/kiosk/v1/articles", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArticleController {

    @Autowired
    PublisherService publisherService;

    @Autowired
    PipelineService pipelineService;

    @ApiOperation("Publish an article")
    @RequestMapping(value="/publish", method = RequestMethod.POST)
    public ResponseEntity<PublishResponse> publish(
            @RequestBody final PublishRequest request
    ) throws IOException {
        final PublishResponse dto = publisherService.publish(request);
        final HttpStatus status = dto.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT;

        return new ResponseEntity<>(dto, status);
    }

    @ApiOperation("Process a published an article")
    @RequestMapping(value="/process/{keyhash}", method = RequestMethod.POST)
    public void process(
            @PathVariable final String keyhash
    ) throws IOException {
        pipelineService.process(keyhash);
    }
}
