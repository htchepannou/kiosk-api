package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.pipeline.ActivityTestSupport;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PublishArticleGatewayTest extends ActivityTestSupport{
    @Mock
    ArticleRepository articleRepository;

    @InjectMocks
    PublishArticleGateway activity;

    @Test
    public void testGetTopic() throws Exception {
        assertThat(activity.getTopic()).isEqualTo(PipelineConstants.TOPIC_ARTICLE_SUBMITTED);
    }

    @Test
    public void shouldAcceptArticlesNeverSubmitted() throws Exception {
        // Given
        final Event event = createEvent();

        final Article article = Fixture.createArticle();
        final String articleId = article.getId();
        when(articleRepository.findOne(articleId)).thenReturn(article);

        // When
        activity.doHandleEvent(event);

        // Then
        assertThatEventPublished(PipelineConstants.TOPIC_ARTICLE_ACCEPTED, event.getPayload());
    }

    @Test
    public void shouldNeverAcceptArticlesAlreadySubmitted() throws Exception {
        // Given
        final Event event = createEvent();

        final Article article = new Article();
        when(articleRepository.findOne(anyString())).thenReturn(article);

        // When
        activity.doHandleEvent(event);

        // Then
        assertThatNoEventPublished();
    }

    private Event createEvent(){
        return new Event("foo", Fixture.createPublishRequest());
    }
}
