package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.pipeline.ActivityTestSupport;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ExtractKeywordsActivityTest extends ActivityTestSupport {

    @InjectMocks
    ExtractKeywordsActivity activity;

    @Test
    public void testGetTopic() throws Exception {
        assertThat(activity.getTopic()).isEqualToIgnoringCase(PipelineConstants.EVENT_EXTRACT_KEYWORDS);
    }

    @Test
    public void testDoHandleArticle() throws Exception {
        final Article article = new Article();

        final String next = activity.doHandleArticle(article);

        assertThat(next).isEqualTo(PipelineConstants.EVENT_SCORE);
    }
}
