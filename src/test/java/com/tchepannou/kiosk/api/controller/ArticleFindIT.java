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

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@Sql({"/sql/clean.sql", "/sql/ArticleFind.sql"})
public class ArticleFindIT extends RestAssuredSupport {

    @Autowired
    FileService fileService;

    @Test
    public void shouldReturnAnArticleByUrl() throws Exception {

        System.out.println(Article.generateId("http://feed100/100"));

        // @formatter:off
        given()
                .queryParam("url", "http://feed100/100")
                .queryParam("includeContent", "false")
        .when()
                .get("/kiosk/v1/articles/find")
        .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))

                .body("error", nullValue())

                .body("article.id", is("d0fc08117a071843564f9f8cb0530af7"))
                .body("article.url", is("http://feed100/100"))
                .body("article.title", is("Article #100"))
                .body("article.slug", is("Slug #100"))
                .body("article.countryCode", is("CMR"))
                .body("article.languageCode", is("FR"))
                .body("article.publishedDate", startsWith("2013-11-15"))
                .body("article.content", nullValue())

                .body("article.website.id", is(100))
                .body("article.website.name", is("Mboa Football"))
                .body("article.website.url", is("http://www.mboafootball.com"))
        ;

        // @formatter:on
    }

    @Test
    public void shouldReturn404WhenUrlInvalid() throws Exception {
        // @formatter:off

        given()
                .queryParam("url", "http://feed100/99999")
                .queryParam("includeContent", "false")
        .when()
                .get("/kiosk/v1/articles/find")
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
