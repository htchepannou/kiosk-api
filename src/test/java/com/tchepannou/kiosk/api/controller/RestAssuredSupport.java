package com.tchepannou.kiosk.api.controller;

import com.jayway.restassured.RestAssured;
import com.tchepannou.kiosk.api.Fixture;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

public class RestAssuredSupport {
    @Value("${local.server.port}")
    private int serverPort;

    @Value("${kiosk.repository.home}")
    private String home;

    @Before
    public void setUp() {
        RestAssured.port = serverPort;
    }

    @After
    public void tearDorn (){
        Fixture.deleteRecursive(new File(home));
    }
}
