package com.tchepannou.kiosk.api.config;

import com.tchepannou.kiosk.api.filter.ArticleFilterSet;
import com.tchepannou.kiosk.api.filter.ArticleLanguageFilter;
import com.tchepannou.kiosk.api.filter.ArticleTitleFilter;
import com.tchepannou.kiosk.api.pipeline.publish.CreateArticleActivity;
import com.tchepannou.kiosk.api.pipeline.publish.DownloadImageActivity;
import com.tchepannou.kiosk.api.pipeline.publish.ExtractImagesActivity;
import com.tchepannou.kiosk.api.pipeline.publish.MainImageActivity;
import com.tchepannou.kiosk.api.pipeline.publish.ProcessArticleActivity;
import com.tchepannou.kiosk.content.ContentExtractor;
import com.tchepannou.kiosk.content.DefaultFilterSetProvider;
import com.tchepannou.kiosk.content.FilterSetProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class PipelineConfig {
    @Value("${kiosk.filters.ContentFilter.blocMinLength}")
    int minBlocLength;

    @Value("${kiosk.filters.ArticleTitleFilter.maxLength}")
    int titleMaxLength;

    @Value("${kiosk.rules.TextLengthRule.minLength}")
    int minTextLength;


    //-- Commons
    @Bean
    ContentExtractor contentExtractor(){
        return new ContentExtractor();
    }

    @Bean
    FilterSetProvider filterSetProvider(){
        return new DefaultFilterSetProvider(minTextLength);
    }

    @Bean
    ArticleFilterSet articleFilterSet() {
        return new ArticleFilterSet(Arrays.asList(
                new ArticleLanguageFilter(),
                new ArticleTitleFilter(titleMaxLength)
        ));
    }


    //-- Activities
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
