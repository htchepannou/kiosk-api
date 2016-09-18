package com.tchepannou.kiosk.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class Starter {
    //-- Main
    public static void main (String [] args){
        SpringApplication.run(Starter.class, args);
    }
}
