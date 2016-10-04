package com.tchepannou.kiosk.api.config;

import com.tchepannou.kiosk.api.pipeline.publish.CreateArticleActivity;
import com.tchepannou.kiosk.api.pipeline.publish.DownloadImageActivity;
import com.tchepannou.kiosk.api.pipeline.publish.ExtractImagesActivity;
import com.tchepannou.kiosk.api.pipeline.publish.MainImageActivity;
import com.tchepannou.kiosk.api.pipeline.publish.ProcessArticleActivity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PipelineConfig {
    //-- Pipeline Activities
    @Bean
    CreateArticleActivity createArticleActivity(){
        return new CreateArticleActivity();
    }

    @Bean
    ProcessArticleActivity processArticleActivity(){
        return new ProcessArticleActivity();
    }

    @Bean
    DownloadImageActivity downloadImageActivity(){
        return new DownloadImageActivity();
    }

    @Bean
    ExtractImagesActivity extractImagesActivity(){
        return new ExtractImagesActivity();
    }

    @Bean
    MainImageActivity mainImageActivity() {
        return new MainImageActivity();
    }
}
