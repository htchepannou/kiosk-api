package com.tchepannou.kiosk.api.controller;

import com.tchepannou.kiosk.api.Starter;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@Sql({"/sql/clean.sql", "/sql/ArticleGetByStatus.sql"})
public class ArticleGetByStatusIT extends RestAssuredSupport{

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
                .body("articles[0].url", is("http://feed200/200"))
                .body("articles[0].title", is("Article #200"))
                .body("articles[0].slug", is("Slug #200"))
                .body("articles[0].countryCode", is("CMR"))
                .body("articles[0].languageCode", is("FR"))
                .body("articles[0].publishedDate", startsWith("2013-11-15"))

                .body("articles[1].id", is("102"))
                .body("articles[1].url", is("http://feed100/102"))
                .body("articles[1].title", is("Article #102"))
                .body("articles[1].slug", is("Slug #102"))
                .body("articles[1].countryCode", is("CMR"))
                .body("articles[1].languageCode", is("FR"))
                .body("articles[1].publishedDate", startsWith("2013-11-15"))
        
                .body("articles[2].id", is("101"))
                .body("articles[2].url", is("http://feed100/101"))
                .body("articles[2].title", is("Article #101"))
                .body("articles[2].slug", is("Slug #101"))
                .body("articles[2].countryCode", is("CMR"))
                .body("articles[2].languageCode", is("FR"))
                .body("articles[2].publishedDate", startsWith("2013-11-15"))
        ;

        // @formatter:on
    }
}
