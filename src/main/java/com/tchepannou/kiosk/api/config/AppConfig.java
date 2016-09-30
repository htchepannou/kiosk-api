package com.tchepannou.kiosk.api.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.tchepannou.kiosk.api.filter.ArticleFilterSet;
import com.tchepannou.kiosk.api.filter.ArticleTitleFilter;
import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import com.tchepannou.kiosk.api.mapper.FeedMapper;
import com.tchepannou.kiosk.api.mapper.WebsiteMapper;
import com.tchepannou.kiosk.api.service.ArticleService;
import com.tchepannou.kiosk.api.service.FeedService;
import com.tchepannou.kiosk.api.service.ImageService;
import com.tchepannou.kiosk.api.service.WebsiteService;
import com.tchepannou.kiosk.core.filter.ContentFilter;
import com.tchepannou.kiosk.core.filter.SanitizeFilter;
import com.tchepannou.kiosk.core.filter.TextFilterSet;
import com.tchepannou.kiosk.core.filter.TrimFilter;
import com.tchepannou.kiosk.core.rule.TextLengthRule;
import com.tchepannou.kiosk.core.rule.TextRuleSet;
import com.tchepannou.kiosk.core.service.FileService;
import com.tchepannou.kiosk.core.service.HttpService;
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
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.TimeZone;

/**
 * Declare your services here!
 */
@Configuration
public class AppConfig {
    @Value("${kiosk.filters.ContentFilter.blocMinLength}")
    int minBlocLength;

    @Value("${kiosk.filters.ArticleTitleFilter.maxLength}")
    int titleMaxLength;

    @Value("${kiosk.rules.TextLengthRule.minLength}")
    int minTextLength;


    @Bean
    public FilterRegistrationBean corsFilterRegistrationBean() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        registrationBean.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(final HttpServletRequest httpServletRequest,
                    final HttpServletResponse httpServletResponse,
                    final FilterChain filterChain)
                    throws ServletException, IOException {

                // For the security reason, CORS should be turned off here. Please change the setting based
                // on your application environment to enable CORS when you fully understand the potential
                // security threat.
                final String requestOrigin = httpServletRequest.getHeader("Origin");
//                if ("https://YOUR_SITE.expedia.com:443".equalsIgnoreCase(requestOrigin)) {
                httpServletResponse.addHeader("Access-Control-Allow-Origin", requestOrigin);
                httpServletResponse.addHeader("Access-Control-Allow-Methods",
                        "GET, POST, PUT, DELETE, OPTIONS");
                httpServletResponse.addHeader("Access-Control-Allow-Headers",
                        "origin, content-type, accept, x-requested-with");
//                }

                filterChain.doFilter(httpServletRequest, httpServletResponse);
            }
        });

        return registrationBean;
    }

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
        final Filter filter = new LogFilter(logService(), transactionIdProvider());
        final FilterRegistrationBean bean = new FilterRegistrationBean(filter);
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
    WebsiteMapper websiteMapper() {
        return new WebsiteMapper();
    }

    @Bean
    WebsiteService websiteService() {
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
    HttpService httpService(){
        return new HttpService();
    }

    @Bean
    TextFilterSet textFilterSet() {
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

    @Bean
    ImageService imageService(){
        return new ImageService();
    }
}
