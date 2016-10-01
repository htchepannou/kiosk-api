package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.pipeline.ActivityTestSupport;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.core.service.FileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;

@RunWith(MockitoJUnitRunner.class)
public class ExtractImagesActivityTest extends ActivityTestSupport {
    @Mock
    FileService fileService;

    @InjectMocks
    ExtractImagesActivity activity;

    @Test
    public void testGetTopic() throws Exception {
        assertThat(activity.getTopic()).isEqualTo(PipelineConstants.TOPIC_ARTICLE_PROCESSED);
    }

    @Test
    public void shouldExtractImages() throws Exception {
        final Article article = Fixture.createArticle();
        final String url = "http://goo.com/img/image1.jpeg";
        doAnswer(articleContent("<p> <img src='" + url + "' alt='image #1' /> </p>")).when(fileService).get(any(), any());

        // When
        activity.doHandleEvent(new Event("foo", article));

        // Then
        final Image img = new Image();
        img.setId(Image.generateId(url));
        img.setTitle("image #1");
        img.setUrl(url);
        img.setContentType("image/jpeg");
        assertThatEventPublished(PipelineConstants.TOPIC_IMAGE_SUBMITTED, img);
    }

    private Answer articleContent(final String html) throws Exception{
        return new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
                final OutputStream out = (OutputStream)invocationOnMock.getArguments()[1];
                out.write(html.getBytes());
                return null;
            }
        };
    }

}
