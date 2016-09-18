package com.tchepannou.kiosk.api.controller;

import com.tchepannou.kiosk.api.service.ArticleService;
import com.tchepannou.kiosk.client.dto.AbstractResponse;
import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.client.dto.GetArticleResponse;
import com.tchepannou.kiosk.client.dto.ProcessRequest;
import com.tchepannou.kiosk.client.dto.ProcessResponse;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import com.tchepannou.kiosk.client.dto.PublishResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    ArticleService articleService;

    @ApiOperation("Publish an article")
    @RequestMapping(value="/publish", method = RequestMethod.POST)
    public ResponseEntity<PublishResponse> publish(
            @RequestBody final PublishRequest request
    ) throws IOException {
        final PublishResponse response = articleService.publish(request);
        return toResponseEntity(response);
    }

    @ApiOperation("Process the content of a published article")
    @RequestMapping(value="/process", method = RequestMethod.POST)
    @ApiResponses(
            {
                    @ApiResponse (code = 200, message = "Success"),
                    @ApiResponse (code = 404, message = "Article not found"),
                    @ApiResponse (code = 409, message = "The processing of the article failed")
            }
    )
    public ResponseEntity<ProcessResponse>  process(
            @RequestBody final ProcessRequest request
    ) {
        final ProcessResponse response = articleService.process(request);
        return toResponseEntity(response);
    }

    @ApiOperation("Return an API by ID")
    @RequestMapping(value="/{id}", method = RequestMethod.POST)
    @ApiResponses(
            {
                    @ApiResponse (code = 200, message = "Success"),
                    @ApiResponse (code = 404, message = "Article not found")
            }
    )
    public ResponseEntity<GetArticleResponse> get (
        @PathVariable final String id
    ) {
        final GetArticleResponse response = articleService.get(id);
        return toResponseEntity(response);
    }

    private <T> ResponseEntity<T> toResponseEntity(final AbstractResponse response){
        HttpStatus status = HttpStatus.OK;
        if (!response.isSuccess()){
            final String code = response.getError().getCode();

            if (ErrorConstants.ARTICLE_NOT_FOUND.equals(code) || ErrorConstants.CONTENT_NOT_FOUND.equals(code)){
                status = HttpStatus.NOT_FOUND;
            } else {
                status = HttpStatus.CONFLICT;
            }
        }

        return (ResponseEntity<T>)new ResponseEntity<>(response, status);
    }
}
