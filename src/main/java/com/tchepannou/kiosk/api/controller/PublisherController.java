package com.tchepannou.kiosk.api.controller;

import com.tchepannou.kiosk.api.dto.PublishRequestDto;
import com.tchepannou.kiosk.api.dto.PublishResponseDto;
import com.tchepannou.kiosk.api.service.PublisherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Api(basePath = "/kiosk/v1/publish", value = "Publisher API")
@RequestMapping(value = "/kiosk/v1/publish", produces = MediaType.APPLICATION_JSON_VALUE)
public class PublisherController {

    @Autowired
    PublisherService publisherService;

    @ApiOperation("Publish an article")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<PublishResponseDto> publish(
            @RequestBody final PublishRequestDto request
    ) throws IOException {
        final PublishResponseDto dto = publisherService.publish(request);
        final HttpStatus status = dto.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT;

        return new ResponseEntity<>(dto, status);
    }
}
