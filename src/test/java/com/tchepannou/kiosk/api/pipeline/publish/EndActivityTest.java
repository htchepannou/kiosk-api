package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class EndActivityTest {
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
        final String next = activity.doHandleArticle(article);

        // Then
        assertThat(next).isNull();
    }
}
