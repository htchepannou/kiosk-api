package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.pipeline.ActivityTestSupport;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.validator.Validation;
import com.tchepannou.kiosk.validator.Validator;
import com.tchepannou.kiosk.validator.ValidatorContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.tchepannou.kiosk.api.Fixture.createArticle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidateActivityTest extends ActivityTestSupport {
    @Mock
    Validator validator;

    @Mock
    ValidatorContext context;

    @InjectMocks
    ValidateActivity activity;

    @Test
    public void testGetTopic() throws Exception {
        assertThat(activity.getTopic()).isEqualTo(PipelineConstants.TOPIC_ARTICLE_IMAGE_EXTRACTED);
    }

    @Test
    public void testDoHandleEventAccept() throws Exception {
        // Given
        when(validator.validate(any(), any())).thenReturn(Validation.success());

        // When
        final Article article = createArticle();
        article.setStatus(Article.Status.processed);
        activity.doHandleEvent(new Event("foo", article));

        // Then
        assertThat(article.getStatus()).isEqualTo(Article.Status.processed);

        assertThatEventPublished(PipelineConstants.TOPIC_END, article);
    }

    @Test
    public void testDoHandleEventReject() throws Exception {
        // Given
        when(validator.validate(any(), any())).thenReturn(Validation.failure("err"));

        // When
        final Article article = createArticle();
        article.setStatus(Article.Status.processed);
        activity.doHandleEvent(new Event("foo", article));

        // Then
        assertThat(article.getStatus()).isEqualTo(Article.Status.rejected);
        assertThat(article.getStatusReason()).isEqualTo("err");

        assertThatEventPublished(PipelineConstants.TOPIC_END, article);
    }
}
