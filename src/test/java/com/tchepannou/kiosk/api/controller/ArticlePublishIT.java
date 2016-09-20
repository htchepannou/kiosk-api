package com.tchepannou.kiosk.api.controller;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.tchepannou.kiosk.api.Starter;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.service.ContentRepositoryService;
import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static com.jayway.restassured.RestAssured.given;
import static com.tchepannou.kiosk.api.Fixture.createArticleDataDto;
import static com.tchepannou.kiosk.api.Fixture.createPublishRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@Sql({"/sql/clean.sql", "/sql/ArticlePublish.sql"})
public class ArticlePublishIT extends RestAssuredSupport {
    @Autowired
    ContentRepositoryService contentRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Test
    public void shouldPublishAnArticle() throws Exception {
        final PublishRequest request = createPublishRequest(100, createArticleDataDto());

        // @formatter:off
        final String id = given ()
                .contentType(ContentType.JSON)
                .body(request, ObjectMapperType.JACKSON_2)
        .when()
                .post("/kiosk/v1/articles")
        .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))

                .body("error", nullValue())
        .extract()
                .path("articleId")
        ;

        // @formatter:on

        final Article article = articleRepository.findOne(id);
        assertThat(article).isNotNull();
        assertThat(article.getCountryCode()).isEqualTo(request.getArticle().getCountryCode());
        assertThat(article.getFeedId()).isEqualTo(request.getFeedId());
        assertThat(article.getLanguageCode()).isEqualTo(request.getArticle().getLanguageCode());
        assertThat(article.getSlug()).isEqualTo(request.getArticle().getSlug());
        assertThat(article.getStatus()).isEqualTo(Article.Status.submitted);
        assertThat(article.getTitle()).isEqualTo(request.getArticle().getTitle());
        assertThat(article.getUrl()).isEqualTo(request.getArticle().getUrl());

        final OutputStream out = new ByteArrayOutputStream();
        contentRepository.read(article.contentKey(article.getStatus()), out);
        assertThat(out.toString()).isEqualTo(request.getArticle().getContent());
    }

    @Test
    public void shouldNotRePublishAnArticle() throws Exception {
        final PublishRequest request = createPublishRequest(100, createArticleDataDto());

        // @formatter:off
        final String id = given ()
                .contentType(ContentType.JSON)
                .body(request, ObjectMapperType.JACKSON_2)
        .when()
                .post("/kiosk/v1/articles")
        .then()
        .extract()
                .path("articleId")
        ;

        given ()
                .contentType(ContentType.JSON)
                .body(request, ObjectMapperType.JACKSON_2)
        .when()
                .post("/kiosk/v1/articles")
        .then()
                .log().all()
                .statusCode(HttpStatus.SC_CONFLICT)
                .body("success", is(false))

                .body("error", notNullValue())
                .body("error.code", is(ErrorConstants.ALREADY_PUBLISHED))

                .body("articleId", is(id))
        ;

        // @formatter:on
    }
}
