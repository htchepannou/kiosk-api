package com.tchepannou.kiosk.api.controller;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.service.ArticleService;
import com.tchepannou.kiosk.api.service.RankerService;
import com.tchepannou.kiosk.client.dto.AbstractResponse;
import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.client.dto.GetArticleListResponse;
import com.tchepannou.kiosk.client.dto.GetArticleResponse;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import com.tchepannou.kiosk.client.dto.PublishResponse;
import com.tchepannou.kiosk.core.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(basePath = "/kiosk/v1/articles", value = "Article API")
@RequestMapping(value = "/kiosk/v1/articles", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArticleController {

    @Autowired
    ArticleRepository repository;

    @Autowired
    ArticleService service;

    @Autowired
    LogService logService;

    @Autowired
    RankerService rankerService;

    @ApiOperation("Publish an article")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<PublishResponse> publish(
            @RequestBody final PublishRequest request
    ) throws IOException {
        final PublishResponse response = service.publish(request);
        return toResponseEntity(response);
    }

    @ApiOperation("Return an article by its ID")
    @RequestMapping(value = "/{articleId}", method = RequestMethod.GET)
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "Success"),
                    @ApiResponse(code = 404, message = "Article not found")
            }
    )
    public ResponseEntity<GetArticleResponse> getById(
            @PathVariable final String articleId
    ) {
        final GetArticleResponse response = service.get(articleId, true);
        return toResponseEntity(response);
    }

    @ApiOperation("Return an article by its URL")
    @RequestMapping(value = "/find", method = RequestMethod.GET, params = {"url", "includeContent"})
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "Success"),
                    @ApiResponse(code = 404, message = "Article not found")
            }
    )
    public ResponseEntity<GetArticleResponse> find(
            @ApiParam(required = true) final String url,
            @ApiParam(defaultValue = "false", allowableValues = "true,false") final String includeContent
    ) {
        final String articleId = Article.generateId(url);
        final GetArticleResponse response = service.get(articleId, "true".equalsIgnoreCase(includeContent));
        return toResponseEntity(response);
    }

    @ApiOperation("Return a list of articles")
    @RequestMapping(value = "/list/{page}", method = RequestMethod.GET)
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "Success"),
            }
    )
    public ResponseEntity<GetArticleListResponse> list(
            @PathVariable @ApiParam(required = true, defaultValue = "0", value = "zero based index of the articles") final int page
    ) {
        final GetArticleListResponse response = service.findByStatus(Article.Status.processed.name(), page);
        return toResponseEntity(response);
    }

    @ApiOperation("Sort the articles")
    @RequestMapping(value = "/rank", method = RequestMethod.GET)
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "Success"),
            }
    )
    public void rank() {
        rankerService.rank();
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

        return (ResponseEntity<T>) ResponseEntity.status(status)
                .body(response);
    }
}
