package io.tchepannou.kiosk.api.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import io.tchepannou.kiosk.api.service.ArticleMapper;
import io.tchepannou.kiosk.api.service.ArticleService;
import io.tchepannou.kiosk.api.service.EventService;
import io.tchepannou.kiosk.api.service.FeatureFlagService;
import io.tchepannou.kiosk.api.service.PipelineRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.sql.DataSource;
import java.util.TimeZone;

/**
 * Declare your services here!
 */
@Configuration
public class AppConfiguration {

    //-- Spring
    @Bean
    Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .simpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .timeZone(TimeZone.getTimeZone("GMT"))
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .featuresToDisable(
                        DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
                );
    }

    @Bean
    ObjectMapper objectMapper(){
        return jackson2ObjectMapperBuilder().build();
    }

    @Bean(destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource")
    DataSource dataSource() {
        final HikariDataSource source = (HikariDataSource) DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .build();
        return source;
    }


    //-- Beans
    @Bean
    @ConfigurationProperties("kiosk.service.ArticleMapper")
    ArticleMapper articleMapper(){
        return new ArticleMapper();
    }

    @Bean
    @ConfigurationProperties("kiosk.service.ArticleService")
    ArticleService articleService(){
        return new ArticleService();
    }

    @Bean
    @ConfigurationProperties("kiosk.service.PipelineRunner")
    PipelineRunner pipelineRunner(){
        return new PipelineRunner();
    }

    @Bean
    @ConfigurationProperties("kiosk.service.EventService")
    EventService eventService(){
        return new EventService();
    }

    @Bean
    @ConfigurationProperties("kiosk.service.FeatureFlagService")
    FeatureFlagService featureFlagService(){
        return new FeatureFlagService();
    }

}
