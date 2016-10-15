package com.tchepannou.kiosk.api.config;

import com.tchepannou.kiosk.api.filter.ArticleFilterSet;
import com.tchepannou.kiosk.api.filter.ArticleLanguageFilter;
import com.tchepannou.kiosk.api.filter.ArticleTitleFilter;
import com.tchepannou.kiosk.api.pipeline.publish.CreateArticleActivity;
import com.tchepannou.kiosk.api.pipeline.publish.ExtractImageActivity;
import com.tchepannou.kiosk.api.pipeline.publish.ProcessArticleActivity;
import com.tchepannou.kiosk.api.service.ImageService;
import com.tchepannou.kiosk.content.ContentExtractor;
import com.tchepannou.kiosk.content.DefaultFilterSetProvider;
import com.tchepannou.kiosk.content.FilterSetProvider;
import com.tchepannou.kiosk.content.TitleSanitizer;
import com.tchepannou.kiosk.image.ImageExtractor;
import com.tchepannou.kiosk.image.support.ImageGrabber;
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
    ContentExtractor contentExtractor() {
        return new ContentExtractor();
    }

    @Bean
    FilterSetProvider filterSetProvider() {
        return new DefaultFilterSetProvider(minTextLength);
    }

    @Bean
    TitleSanitizer titleSanitizer(){
        return new TitleSanitizer();
    }

    @Bean
    ArticleFilterSet articleFilterSet() {
        return new ArticleFilterSet(Arrays.asList(
                new ArticleLanguageFilter(),
                new ArticleTitleFilter(titleMaxLength)
        ));
    }

    @Bean
    ImageExtractor imageExtractor() {
        return new ImageExtractor();
    }

    @Bean
    ImageService imageService () {
        return new ImageService();
    }

    @Bean
    ImageGrabber imageGrabber() {
        return new ImageGrabber();
    }

    //-- Activities
    @Bean
    CreateArticleActivity createArticleActivity() {
        return new CreateArticleActivity();
    }

    @Bean
    ProcessArticleActivity processArticleActivity() {
        return new ProcessArticleActivity();
    }

    @Bean
    ExtractImageActivity extractImageActivity() {
        return new ExtractImageActivity();
    }
}
