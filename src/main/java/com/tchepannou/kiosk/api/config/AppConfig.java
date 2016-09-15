package com.tchepannou.kiosk.api.config;

import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import com.tchepannou.kiosk.api.mapper.FeedMapper;
import com.tchepannou.kiosk.api.service.ContentRepositoryService;
import com.tchepannou.kiosk.api.service.FeedService;
import com.tchepannou.kiosk.api.service.LocalContentRepositoryService;
import com.tchepannou.kiosk.api.service.PipelineService;
import com.tchepannou.kiosk.api.service.PublisherService;
import com.tchepannou.kiosk.core.filter.ContentFilter;
import com.tchepannou.kiosk.core.filter.SanitizeFilter;
import com.tchepannou.kiosk.core.filter.TextFilterSet;
import com.tchepannou.kiosk.core.service.TimeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Arrays;

/**
 * Declare your services here!
 */
@Configuration
public class AppConfig {
    @Value("${kiosk.filters.content.blocMinLength}")
    int minBlocLength;

    @Bean
    FeedMapper feedMapper(){
        return new FeedMapper();
    }

    @Bean
    FeedService feedService(){
        return new FeedService();
    }

    @Bean
    TimeService timeService (){
        return new TimeService();
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

    @Bean
    TextFilterSet textFilterSet (){
        return new TextFilterSet(Arrays.asList(
                new SanitizeFilter(),
                new ContentFilter(minBlocLength)
        ));
    }

    @Bean
    PipelineService pipelineService(){
        return new PipelineService();
    }
}
