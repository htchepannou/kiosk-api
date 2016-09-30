package com.tchepannou.kiosk.api.controller;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.service.ImageService;
import com.tchepannou.kiosk.core.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(basePath = "/kiosk/v1/images", value = "Image API")
@RequestMapping(value = "/kiosk/v1/images", produces = MediaType.APPLICATION_JSON_VALUE)
public class ImageController {

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ImageRepository repository;

    @Autowired
    ImageService service;

    @Autowired
    LogService logService;

    @Async
    @ApiOperation("Fetch all images from submitted articles")
    @RequestMapping(value = "/fetch", method = RequestMethod.GET)
    public void fetch() {
        final List<Article> articles = articleRepository.findByStatusOrderByPublishedDateDesc(Article.Status.submitted);
        for (final Article article : articles) {
            try {
                service.process(article);
                logService.log();
            } catch (final Exception e) {
                logService.log(e);
            }
        }
    }
}
