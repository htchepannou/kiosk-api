package com.tchepannou.kiosk.api.config;

import com.tchepannou.kiosk.api.filter.ArticleFilterSet;
import com.tchepannou.kiosk.api.filter.ArticleTitleFilter;
import com.tchepannou.kiosk.api.pipeline.publish.CreateArticleActivity;
import com.tchepannou.kiosk.api.pipeline.publish.DownloadImageActivity;
import com.tchepannou.kiosk.api.pipeline.publish.ExtractImagesActivity;
import com.tchepannou.kiosk.api.pipeline.publish.MainImageActivity;
import com.tchepannou.kiosk.api.pipeline.publish.ProcessArticleActivity;
import com.tchepannou.kiosk.core.filter.ContentFilter;
import com.tchepannou.kiosk.core.filter.SanitizeFilter;
import com.tchepannou.kiosk.core.filter.TextFilterSet;
import com.tchepannou.kiosk.core.filter.TrimFilter;
import com.tchepannou.kiosk.core.rule.TextLengthRule;
import com.tchepannou.kiosk.core.rule.TextRuleSet;
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
    @Bean(name=BeanConstants.BEAN_ARTICLE_PROCESSOR_FILTER_SET)
    TextFilterSet processFilterSet() {
        return new TextFilterSet(Arrays.asList(
                new SanitizeFilter(),
                new ContentFilter(minBlocLength),
                new TrimFilter()
        ));
    }

    @Bean
    TextRuleSet textRuleSet() {
        return new TextRuleSet(Arrays.asList(
                new TextLengthRule(minTextLength)
        ));
    }

    @Bean
    ArticleFilterSet articleFilterSet() {
        return new ArticleFilterSet(Arrays.asList(
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
