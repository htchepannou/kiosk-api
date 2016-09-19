package com.tchepannou.kiosk.api.controller;

import com.jayway.restassured.RestAssured;
import com.tchepannou.kiosk.api.Starter;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@Sql({"/sql/clean.sql", "/sql/ArticleGetByStatus.sql"})
public class ArticleGetByStatusIT {
    @Value("${local.server.port}")
    private int serverPort;

    @Before
    public void setUp() {
        RestAssured.port = serverPort;
    }

    @Test
    public void shouldReturnAllProcessedArticles() throws Exception {
        // @formatter:off

        when()
                .get("/kiosk/v1/articles/status/processed")
        .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("size", is(3))
                .body("success", is(true))

                .body("success", is(true))
                .body("error", nullValue())

                .body("articles[0].id", is("200"))
                .body("articles[0].status", is("processed"))
                .body("articles[0].data.url", is("http://feed200/200"))
                .body("articles[0].data.title", is("Article #200"))
                .body("articles[0].data.slug", is("Slug #200"))
                .body("articles[0].data.countryCode", is("CMR"))
                .body("articles[0].data.languageCode", is("FR"))
                .body("articles[0].data.publishedDate", is("2013-11-15 20:30:00 +0000"))

                .body("articles[1].id", is("102"))
                .body("articles[1].status", is("processed"))
                .body("articles[1].data.url", is("http://feed100/102"))
                .body("articles[1].data.title", is("Article #102"))
                .body("articles[1].data.slug", is("Slug #102"))
                .body("articles[1].data.countryCode", is("CMR"))
                .body("articles[1].data.languageCode", is("FR"))
                .body("articles[1].data.publishedDate", is("2013-11-15 19:30:00 +0000"))
        
                .body("articles[2].id", is("101"))
                .body("articles[2].status", is("processed"))
                .body("articles[2].data.url", is("http://feed100/101"))
                .body("articles[2].data.title", is("Article #101"))
                .body("articles[2].data.slug", is("Slug #101"))
                .body("articles[2].data.countryCode", is("CMR"))
                .body("articles[2].data.languageCode", is("FR"))
                .body("articles[2].data.publishedDate", is("2013-11-15 18:30:00 +0000"))
        ;

        // @formatter:on
    }
}
