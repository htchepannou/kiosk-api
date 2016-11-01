package com.tchepannou.kiosk.api.pipeline;

import com.tchepannou.kiosk.api.Starter;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.client.dto.ArticleDataDto;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebAppConfiguration
@Configuration
@IntegrationTest("server.port:0")
@Sql({"/sql/clean.sql", "/pipeline/data.sql"})
public class PublishPipelineIT {
    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private EventHandler handler;

    @Autowired
    private ArticleRepository articleRepository;

    @After
    public void tearDown() {
        handler.getEvents().clear();
    }

    @Bean
    CustomScopeConfigurer customScopeConfigurer() {
        final CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope("request", new SimpleThreadScope());
        return configurer;
    }

    @Bean
    EventHandler eventHandler() {
        return new EventHandler();
    }

    @Test
    public void shouldPublishArticle() throws Exception {
        // Given
        final PublishRequest request = new PublishRequest();
        request.setFeedId(100);
        request.setArticle(sampleArticleFR());

        // When
        publisher.publishEvent(new Event(PipelineConstants.EVENT_CREATE_ARTICLE, request));

        // Then

        // Event
        assertThat(handler.getEvents()).containsExactly(
                PipelineConstants.EVENT_CREATE_ARTICLE,
                PipelineConstants.EVENT_EXTRACT_CONTENT,
                PipelineConstants.EVENT_EXTRACT_IMAGE,
                PipelineConstants.EVENT_EXTRACT_VIDEO,
                PipelineConstants.EVENT_EXTRACT_LANGUAGE,
                PipelineConstants.EVENT_VALIDATE,
                PipelineConstants.EVENT_EXTRACT_KEYWORDS,
                PipelineConstants.EVENT_SCORE,
                PipelineConstants.EVENT_END
        );

        // Article persisted
        final String id = Article.generateId(request.getArticle().getUrl());
        assertThat(articleRepository.findOne(id)).isNotNull();
    }

    private ArticleDataDto sampleArticleFR() throws Exception {
        final ArticleDataDto article = new ArticleDataDto();
        article.setTitle("Douala réconforte les accidentés du chemin de fer");
        article.setSlug("Les personnes accidentées à Eseka ont bénéficié d’une prise en charge idoine dans les hôpitaux.");
        article.setUrl("http://www.cameroon-tribune.cm/articles/1905/fr/");
        article.setContent(IOUtils.toString(getClass().getResourceAsStream("/pipeline/fr.html")));
        return article;
    }

    public static class EventHandler {
        private final List<String> events = new ArrayList<>();

        @EventListener
        public void handleEvent(final Event event) {
            events.add(event.getTopic());
        }

        public List<String> getEvents() {
            return events;
        }
    }
}
