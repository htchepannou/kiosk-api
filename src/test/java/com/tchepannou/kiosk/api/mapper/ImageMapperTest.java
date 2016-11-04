package com.tchepannou.kiosk.api.mapper;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.client.dto.ImageDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ImageMapperTest {

    @InjectMocks
    ImageMapper mapper;

    @Test
    public void testToImageDto() throws Exception {
        final Image img = Fixture.createImage();

        mapper.publicBaseUrl = "http://foo.com";
        final ImageDto dto = mapper.toImageDto(img);

        assertThat(dto.getContentType()).isEqualTo(img.getContentType());
        assertThat(dto.getHeight()).isEqualTo(img.getHeight());
        assertThat(dto.getId()).isEqualTo(img.getId());
        assertThat(dto.getPublicUrl()).isEqualTo("http://foo.com/" + img.getId());
        assertThat(dto.getTitle()).isEqualTo(img.getTitle());
        assertThat(dto.getWidth()).isEqualTo(img.getWidth());
    }
}
