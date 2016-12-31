package io.tchepannou.kiosk.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableScheduling
public class Application {
    public static void main(final String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
