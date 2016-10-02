package com.tchepannou.kiosk.api.controller;

import com.tchepannou.kiosk.api.Starter;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.core.service.FileService;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.ByteArrayInputStream;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@Sql({"/sql/clean.sql", "/sql/ArticleGet.sql"})
public class ArticleGetIT extends RestAssuredSupport{

    @Autowired
    FileService fileService;

    @Test
    public void shouldReturnAnArticle() throws Exception {
        final String content = "<p>Hello <strong>world</strong></p>";
        final Article article = new Article();
        article.setId("100");
        article.setStatus(Article.Status.submitted);
        fileService.put(article.contentKey(article.getStatus()), new ByteArrayInputStream(content.getBytes()));

        // @formatter:off

        when()
                .get("/kiosk/v1/articles/100")
        .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))

                .body("error", nullValue())

                .body("article.id", is("100"))
                .body("article.url", is("http://feed100/100"))
                .body("article.title", is("Article #100"))
                .body("article.slug", is("Slug #100"))
                .body("article.countryCode", is("CMR"))
                .body("article.languageCode", is("FR"))
                .body("article.publishedDate", startsWith("2013-11-15"))
                .body("article.content", is(content))

                .body("website.id", is(100))
                .body("website.name", is("Mboa Football"))
                .body("website.url", is("http://www.mboafootball.com"))

                .body("image.id", is("100"))
                .body("image.title", is("sample image"))
                .body("image.contentType", is("image/png"))
                .body("image.width", is(128))
                .body("image.height", is(256))
                .body("image.publicUrl", is("http://public.x.com/11.png"))
        ;

        // @formatter:on
    }

    @Test
    public void shouldReturn404WhenNotContent() throws Exception {
        // @formatter:off

        when()
                .get("/kiosk/v1/articles/101")
        .then()
                .log().all()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("success", is(false))

                .body("error.code", is(ErrorConstants.CONTENT_NOT_FOUND))

                .body("article", nullValue())
        ;

        // @formatter:on
    }

    @Test
    public void shouldReturn404WhenInvalidId() throws Exception {
        // @formatter:off

        when()
                .get("/kiosk/v1/articles/999999")
        .then()
                .log().all()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("success", is(false))

                .body("error.code", is(ErrorConstants.ARTICLE_NOT_FOUND))

                .body("article", nullValue())
        ;

        // @formatter:on
    }
}
