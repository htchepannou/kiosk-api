package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.pipeline.ActivityTestSupport;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.core.service.FileService;
import com.tchepannou.kiosk.core.service.HttpService;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.InputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DownloadImageActivityTest extends ActivityTestSupport{
    @Mock
    FileService fileService;

    @Mock
    HttpService httpService;

    @Mock
    ImageRepository imageRepository;

    @InjectMocks
    DownloadImageActivity activity;

    @Test
    public void testGetTopic() throws Exception {
        assertThat(activity.getTopic()).isEqualTo(PipelineConstants.TOPIC_ARTICLE_SUBMITTED);
    }

    @Test
    public void shouldDownloadImage() throws Exception {
        // Given
        final Image img = Fixture.createImage();

        final String key = "foo/bar/1.jpeg";
        when(httpService.get(any(), any(), any())).thenReturn(key);

        doAnswer(image("/images/ionic.png")).when(fileService).get(any(), any());

        // When
        activity.doHandleEvent(new Event("fo", img));

        // Then
        verify(imageRepository).save(img);
        assertThat(img.getContentType()).isEqualTo("image/jpeg");
        assertThat(img.getWidth()).isEqualTo(128);
        assertThat(img.getWidth()).isEqualTo(118);
    }

    private Answer image(final String path) throws Exception {
        return new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
                final OutputStream out = (OutputStream) invocationOnMock.getArguments()[1];
                final InputStream in = getClass().getResourceAsStream(path);
                IOUtils.copy(in, out);
                return null;
            }
        };
    }

}
