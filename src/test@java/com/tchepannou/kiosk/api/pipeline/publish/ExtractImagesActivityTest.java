package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.pipeline.ActivityTestSupport;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.service.ImageService;
import com.tchepannou.kiosk.core.service.FileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.OutputStream;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExtractImagesActivityTest extends ActivityTestSupport {
    @Mock
    FileService fileService;

    @Mock
    ImageService imageService;

    @InjectMocks
    ExtractImagesActivity activity;

    @Test
    public void testGetTopic() throws Exception {
        assertThat(activity.getTopic()).isEqualTo(PipelineConstants.TOPIC_ARTICLE_PROCESSED);
    }

    @Test
    public void shouldExtractImages() throws Exception {

        doAnswer(articleContent("html")).when(fileService).get(any(), any());

        Image img1 = Fixture.createImage();
        Image img2 = Fixture.createImage();
        when(imageService.extractImages(anyString(), anyString()))
                .thenReturn(Arrays.asList(img1, img2));

        // When
        final Article article = Fixture.createArticle();
        activity.doHandleEvent(new Event("foo", article));

        // Then
        ArgumentCaptor<Event> event = ArgumentCaptor.forClass(Event.class);
        verify(publisher, times(3)).publishEvent(event.capture());

        assertThat(event.getAllValues().get(0).getTopic()).isEqualTo(PipelineConstants.TOPIC_IMAGE_SUBMITTED);
        assertThat(event.getAllValues().get(0).getPayload()).isEqualTo(img1);

        assertThat(event.getAllValues().get(1).getTopic()).isEqualTo(PipelineConstants.TOPIC_IMAGE_SUBMITTED);
        assertThat(event.getAllValues().get(1).getPayload()).isEqualTo(img2);

        assertThat(event.getAllValues().get(2).getTopic()).isEqualTo(PipelineConstants.TOPIC_ARTICLE_IMAGES_DOWNLOADED);
        assertThat(event.getAllValues().get(2).getPayload()).isEqualTo(article);


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
