package com.tchepannou.kiosk.api.config;

import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import com.tchepannou.kiosk.api.mapper.FeedMapper;
import com.tchepannou.kiosk.api.service.ContentRepositoryService;
import com.tchepannou.kiosk.api.service.FeedService;
import com.tchepannou.kiosk.api.service.LocalContentRepositoryService;
import com.tchepannou.kiosk.api.service.PublisherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * Declare your services here!
 */
@Configuration
public class AppConfig {
    @Bean
    FeedMapper feedMapper(){
        return new FeedMapper();
    }

    @Bean
    FeedService feedService(){
        return new FeedService();
    }

    @Bean
    PublisherService publisherService () {
        return new PublisherService();
    }

    @Bean
    ArticleMapper articleMapper() {
        return new ArticleMapper();
    }

    @Bean
    ContentRepositoryService contentRepositoryService (
            @Value("${kiosk.repository.home}") String repositoryHome
    ){
        return new LocalContentRepositoryService(new File(repositoryHome));
    }

}
