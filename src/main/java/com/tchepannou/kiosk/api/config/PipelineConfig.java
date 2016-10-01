package com.tchepannou.kiosk.api.config;

import com.tchepannou.kiosk.api.pipeline.publish.CreateArticleActivity;
import com.tchepannou.kiosk.api.pipeline.publish.DownloadImageActivity;
import com.tchepannou.kiosk.api.pipeline.publish.ExtractImagesActivity;
import com.tchepannou.kiosk.api.pipeline.publish.ProcessArticleActivity;
import com.tchepannou.kiosk.api.pipeline.publish.PublishArticleGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PipelineConfig {
    @Bean
    CreateArticleActivity createArticleActivity(){
        return new CreateArticleActivity();
    }

    @Bean
    ProcessArticleActivity processArticleActivity(){
        return new ProcessArticleActivity();
    }

    @Bean
    PublishArticleGateway publishedGateway(){
        return new PublishArticleGateway();
    }

    @Bean
    DownloadImageActivity downloadImageActivity(){
        return new DownloadImageActivity();
    }

    @Bean
    ExtractImagesActivity extractImagesActivity(){
        return new ExtractImagesActivity();
    }
}
