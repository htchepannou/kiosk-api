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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@Sql({"/sql/clean.sql", "/sql/controller/FeedController.all.sql"})
public class FeedControllerIT {
    @Value("${local.server.port}")
    private int serverPort;

    @Before
    public void setUp() {
        RestAssured.port = serverPort;
    }

    @Test
    public void shouldReturnAllFeeds() throws Exception {
        // @formatter:off

        when()
                .get("/kiosk/v1/feeds")
        .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("size", is(2))
                
                .body("feeds[0].id", is(1001))
                .body("feeds[0].countryCode", is("CMR"))
                .body("feeds[0].type", is("rss"))
                .body("feeds[0].name", is("Mboa Football"))
                .body("feeds[0].url", is("http://mboafootball.com/rss"))
        
                .body("feeds[1].id", is(1002))
                .body("feeds[1].countryCode", is("CMR"))
                .body("feeds[1].type", is("rss"))
                .body("feeds[1].name", is("Cameroon Post Online"))
                .body("feeds[1].url", is("http://www.cameroonpostline.com/feed/"))
        
        ;

        // @formatter:on
    }
}
