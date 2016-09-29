package com.tchepannou.kiosk.api.controller;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.service.ArticleService;
import com.tchepannou.kiosk.client.dto.AbstractResponse;
import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.client.dto.GetArticleListResponse;
import com.tchepannou.kiosk.client.dto.GetArticleResponse;
import com.tchepannou.kiosk.client.dto.ProcessRequest;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import com.tchepannou.kiosk.client.dto.PublishResponse;
import com.tchepannou.kiosk.core.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@Api(basePath = "/kiosk/v1/articles", value = "Article Publisher API")
@RequestMapping(value = "/kiosk/v1/articles", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArticleController {

    @Autowired
    ArticleRepository repository;

    @Autowired
    ArticleService service;

    @Autowired
    LogService logService;

    @ApiOperation("Publish an article")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<PublishResponse> publish(
            @RequestBody final PublishRequest request
    ) throws IOException {
        final PublishResponse response = service.publish(request);
        return toResponseEntity(response);
    }

    @Async
    @ApiOperation("Process the content of all the published articles")
    @RequestMapping(value = "/process", method = RequestMethod.GET)
    public void process() {
        final List<Article> articles = repository.findByStatusOrderByPublishedDateDesc(Article.Status.submitted);
        for (final Article article : articles) {
            try {
                final ProcessRequest request = new ProcessRequest();
                request.setArticleId(article.getId());
                service.process(request);
                logService.log();
            } catch (final Exception e) {
                logService.log(e);
            }
        }
    }

    @ApiOperation("Return an API by ID")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "Success"),
                    @ApiResponse(code = 404, message = "Article not found")
            }
    )
    public ResponseEntity<GetArticleResponse> getById(
            @PathVariable final String id
    ) {
        final GetArticleResponse response = service.get(id);
        return toResponseEntity(response);
    }

    @ApiOperation("Return a list of article ready to be read")
    @RequestMapping( method = RequestMethod.GET)
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "Success"),
            }
    )
    public ResponseEntity<GetArticleListResponse> get() {
        return getByStatus(Article.Status.processed.name());
    }

    @ApiOperation("Return a list of article by status")
    @RequestMapping(value = "/status/{status}", method = RequestMethod.GET)
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "Success"),
            }
    )
    public ResponseEntity<GetArticleListResponse> getByStatus(
            @PathVariable final String status
    ) {
        final GetArticleListResponse response = service.status(status);
        return toResponseEntity(response);
    }

    private <T> ResponseEntity<T> toResponseEntity(final AbstractResponse response) {
        HttpStatus status = HttpStatus.OK;
        if (!response.isSuccess()) {
            final String code = response.getError().getCode();

            if (ErrorConstants.ARTICLE_NOT_FOUND.equals(code) || ErrorConstants.CONTENT_NOT_FOUND.equals(code)) {
                status = HttpStatus.NOT_FOUND;
            } else {
                status = HttpStatus.CONFLICT;
            }
        }

        return (ResponseEntity<T>) new ResponseEntity<>(response, status);
    }
}
