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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(basePath = "/v1/articles", value = "Article API")
@RequestMapping(value = "/v1/articles", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArticleController {

    @Autowired
    ArticleService articleService;

    @ApiOperation("Return a list of articles")
    @RequestMapping(value = "/list/{page}", method = RequestMethod.GET)
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "Success"),
            }
    )
    public ArticleModelList list(
            @PathVariable @ApiParam(required = true, defaultValue = "0", value = "zero based index of the articles")
            final int page
    ) {
        return articleService.list(page);
    }

}
