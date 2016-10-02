package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.pipeline.ActivityTestSupport;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.service.ImageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DownloadImageActivityTest extends ActivityTestSupport{
    @Mock
    ImageService imageService;

    @Mock
    ImageRepository imageRepository;

    @InjectMocks
    DownloadImageActivity activity;

    @Test
    public void testGetTopic() throws Exception {
        assertThat(activity.getTopic()).isEqualTo(PipelineConstants.TOPIC_IMAGE_SUBMITTED);
    }

    @Test
    public void shouldSaveDownloadedImage() throws Exception {
        // Given
        final Image img = Fixture.createImage();

        when(imageService.download(img)).thenReturn(img);

        // When
        activity.doHandleEvent(new Event("fo", img));

        // Then
        verify(imageRepository).save(img);
    }

    @Test
    public void shouldNotSaveNonDownloadedImage() throws Exception {
        // Given
        final Image img = Fixture.createImage();

        when(imageService.download(img)).thenReturn(null);

        // When
        activity.doHandleEvent(new Event("fo", img));

        // Then
        verify(imageRepository, never()).save(img);
    }

}
