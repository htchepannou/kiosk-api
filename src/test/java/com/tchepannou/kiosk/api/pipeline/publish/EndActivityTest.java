package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EndActivityTest {
    @Mock
    ArticleRepository articleRepository;

    @InjectMocks
    EndActivity activity;

    @Test
    public void testGetTopic() throws Exception {
        assertThat(activity.getTopic()).isEqualTo(PipelineConstants.EVENT_END);
    }

    @Test
    public void testDoHandleEventArticle() throws Exception {
        // Given
        final Article article = Fixture.createArticle();

        // When
        activity.doHandleEvent(new Event("foo", article));

        // Then
        verify(articleRepository).save(article);
    }

    @Test
    public void testDoHandleEventArticleList() throws Exception {
        // Given
        final List<Article> articles = Arrays.asList(
                Fixture.createArticle(),
                Fixture.createArticle(),
                Fixture.createArticle()
        );

        // When
        activity.doHandleEvent(new Event("foo", articles));

        // Then
        verify(articleRepository).save(articles);

    }
}
