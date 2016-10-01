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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@Sql({"/sql/clean.sql", "/sql/WebsiteGetAll.sql"})
public class WebsiteGetAllIT extends RestAssuredSupport {

    @Test
    public void shouldReturnAllWebsites() throws Exception {
        // @formatter:off

        when()
                .get("/kiosk/v1/websites")
        .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("size", is(2))

                .body("success", is(true))
                .body("error", nullValue())

                .body("websites[0].id", is(1101))
                .body("websites[0].name", is("cameroon-info.net"))
                .body("websites[0].url", is("http://cameroon-info.net"))
                .body("websites[0].articleUrlPrefix", is("/article"))
                .body("websites[0].articleUrlSuffix", is(".html"))
                .body("websites[0].titleCssSelector", is(".cp-post-content h3"))
                .body("websites[0].slugCssSelector", is(".cp-post-content .slug"))

                .body("websites[1].id", is(1102))
                .body("websites[1].name", is("Camfoot"))
                .body("websites[1].url", is("http://camfoot.com"))
                .body("websites[1].articleUrlPrefix", is("/p"))
                .body("websites[1].articleUrlSuffix", is(".html"))
                .body("websites[1].titleCssSelector", is(".content .title"))
                .body("websites[1].slugCssSelector", is(".content .slug"))

        ;

        // @formatter:on
    }
}
