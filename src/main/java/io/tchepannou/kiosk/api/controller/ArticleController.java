package io.tchepannou.kiosk.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.tchepannou.kiosk.api.model.ArticleModelList;
import io.tchepannou.kiosk.api.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(basePath = "/v1/articles", value = "Article API")
@RequestMapping(value = "/v1/articles", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArticleController {

    @Autowired
    ArticleService articleService;

    @ApiOperation("Return a list of articles")
    @RequestMapping(method = RequestMethod.GET)
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "Success"),
            }
    )
    public ArticleModelList list(
            @RequestParam(name = "page", defaultValue = "0")
            @ApiParam(required = false, defaultValue = "0", value = "zero based index of the articles")
            final int page,

            @RequestParam(name = "limit", defaultValue = "30")
            @ApiParam(required = false, defaultValue = "30", value = "Number of article to return. Max=100")
            final int limit

    ) {
        return articleService.list(page, Math.min(limit, 100));
    }

}
