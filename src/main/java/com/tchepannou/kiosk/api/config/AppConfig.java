package com.tchepannou.kiosk.api.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import com.tchepannou.kiosk.api.mapper.FeedMapper;
import com.tchepannou.kiosk.api.mapper.WebsiteMapper;
import com.tchepannou.kiosk.api.service.ArticleService;
import com.tchepannou.kiosk.api.service.FeedService;
import com.tchepannou.kiosk.core.service.FileService;
import com.tchepannou.kiosk.api.service.WebsiteService;
import com.tchepannou.kiosk.core.filter.ContentFilter;
import com.tchepannou.kiosk.core.filter.SanitizeFilter;
import com.tchepannou.kiosk.core.filter.TextFilterSet;
import com.tchepannou.kiosk.core.service.LogService;
import com.tchepannou.kiosk.core.service.TimeService;
import com.tchepannou.kiosk.core.service.TransactionIdProvider;
import com.tchepannou.kiosk.core.servlet.LogFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.servlet.Filter;
import java.io.File;
import java.util.Arrays;
import java.util.TimeZone;

/**
 * Declare your services here!
 */
@Configuration
public class AppConfig {
    @Value("${kiosk.filters.content.blocMinLength}")
    int minBlocLength;

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .simpleDateFormat("yyyy-MM-dd HH:mm:ss Z")
                .timeZone(TimeZone.getTimeZone("GMT"))
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .featuresToDisable(
                        DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
                )
                ;
    }

    @Bean
    @Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    LogService logService() {
        return new LogService(timeService());
    }

    @Bean
    @Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    TransactionIdProvider transactionIdProvider() {
        return new TransactionIdProvider();
    }

    @Bean
    FilterRegistrationBean logFilter() {
        Filter filter = new LogFilter(logService(), transactionIdProvider());
        FilterRegistrationBean bean = new FilterRegistrationBean(filter);
        bean.addUrlPatterns("/kiosk/v1/*");
        return bean;
    }

    @Bean
    FeedMapper feedMapper() {
        return new FeedMapper();
    }

    @Bean
    FeedService feedService() {
        return new FeedService();
    }

    @Bean
    TimeService timeService() {
        return new TimeService();
    }

    @Bean
    WebsiteMapper websiteMapper (){
        return new WebsiteMapper();
    }

    @Bean
    WebsiteService websiteService (){
        return new WebsiteService();
    }

    @Bean
    ArticleService articleService() {
        return new ArticleService();
    }

    @Bean
    ArticleMapper articleMapper() {
        return new ArticleMapper();
    }

    @Bean
    FileService fileService(
            @Value("${kiosk.repository.home}") final String repositoryHome
    ) {
        return new FileService(new File(repositoryHome));
    }

    @Bean
    TextFilterSet textFilterSet() {
        return new TextFilterSet(Arrays.asList(
                new SanitizeFilter(),
                new ContentFilter(minBlocLength)
        ));
    }
}
