package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.core.service.FileService;
import com.tchepannou.kiosk.core.service.HttpService;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceTest {
    @Mock
    Tika tika;

    @Mock
    FileService fileService;

    @Mock
    HttpService httpService;

    @InjectMocks
    ImageService service;

    //-- Extract
    @Test
    public void shouldExtractImages() throws Exception {
        // Given
        final String html = "<body> <img src='http://a.com/1.png' alt='foo'/> <img src='http://a.com/2.jpg'/> </body>";

        when(tika.detect(anyString()))
                .thenReturn("image/png")
                .thenReturn("image/jpeg");
        // When
        final List<Image> result = service.extractImages(html, "http://w.com");

        // Then
        assertThat(result).hasSize(2);

        final Image img1 = result.get(0);
        assertThat(img1.getBase64Content()).isNull();
        assertThat(img1.getContentType()).isEqualTo("image/png");
        assertThat(img1.getHeight()).isEqualTo(0);
        assertThat(img1.getId()).isEqualTo("8581387a54d7cd9280b6b2f8f612a2a6");
        assertThat(img1.getKey()).isNull();
        assertThat(img1.getTitle()).isEqualTo("foo");
        assertThat(img1.getUrl()).isEqualTo("http://a.com/1.png");
        assertThat(img1.getWidth()).isEqualTo(0);

        final Image img2 = result.get(1);
        assertThat(img2.getBase64Content()).isNull();
        assertThat(img2.getContentType()).isEqualTo("image/jpeg");
        assertThat(img2.getHeight()).isEqualTo(0);
        assertThat(img2.getId()).isEqualTo("84f30c632165063320f973b3ca7370fa");
        assertThat(img2.getKey()).isNull();
        assertThat(img2.getTitle()).isNullOrEmpty();
        assertThat(img2.getUrl()).isEqualTo("http://a.com/2.jpg");
        assertThat(img2.getWidth()).isEqualTo(0);
    }

    @Test
    public void shouldExtractAbsoluteUrl() throws Exception {
        // Given
        final String html = "<body> <img src='1.png' alt='foo'/> <img src='2.jpg'/> </body>";

        // When
        final List<Image> result = service.extractImages(html, "http://w.com");

        // Then
        assertThat(result).hasSize(2);

        final Image img1 = result.get(0);
        assertThat(img1.getBase64Content()).isNull();
        assertThat(img1.getId()).isEqualTo("37e95164af4271061428364c9cd9985d");
        assertThat(img1.getUrl()).isEqualTo("http://w.com/1.png");

        final Image img2 = result.get(1);
        assertThat(img2.getBase64Content()).isNull();
        assertThat(img2.getId()).isEqualTo("b558ff7ba54930309be2f29e7bff4f34");
        assertThat(img2.getUrl()).isEqualTo("http://w.com/2.jpg");
    }

    @Test
    public void shouldExtractBase64Image() throws Exception {
        // Given
        final String html = "<body> <img src='data:image/jpeg;base64,122212'/> </body>";

        // When
        final List<Image> result = service.extractImages(html, "http://w.com");

        // Then
        assertThat(result).hasSize(1);

        final Image img1 = result.get(0);
        assertThat(img1.getBase64Content()).isEqualTo("122212");
        assertThat(img1.getContentType()).isEqualTo("image/jpeg");
        assertThat(img1.getId()).isEqualTo("c2b67d8ecf90a4f39a9aff3ac6787057");
        assertThat(img1.getUrl()).isNull();
    }


    //-- Downlaod
    @Test
    public void shouldDownloadFromUrl() throws Exception {
        // Given
        final Image img = Fixture.createImage();

        service.publicBaseUrl = "http://img.com/asset/image";

        when(tika.detect(anyString())).thenReturn("image/png");

        when(httpService.get(any(), any(), any())).thenReturn("/a/b/0.png");

        doAnswer(image("/img/ionic.png")).when(fileService).get(any(), any());

        // When
        final Image result = service.download(img);

        // Then
        assertThat(result.getBase64Content()).isNull();
        assertThat(result.getContentType()).isEqualTo("image/png");
        assertThat(result.getHeight()).isEqualTo(128);
        assertThat(result.getId()).isEqualTo(img.getId());
        assertThat(result.getKey()).isEqualTo("/a/b/0.png");
        assertThat(result.getPublicUrl()).isEqualTo("http://img.com/asset/image/" + img.getId());
        assertThat(result.getTitle()).isEqualTo(img.getTitle());
        assertThat(result.getUrl()).isEqualTo(img.getUrl());
        assertThat(result.getWidth()).isEqualTo(128);
    }

    @Test
    public void shouldDownloadFromBase64() throws Exception {
        // Given
        final Image img = Fixture.createImage();
        img.setUrl(null);
        img.setBase64Content("R0lGODlhDwAPAKECAAAAzMzM/////\n"
                + "wAAACwAAAAADwAPAAACIISPeQHsrZ5ModrLlN48CXF8m2iQ3YmmKqVlRtW4ML\n"
                + "wWACH+H09wdGltaXplZCBieSBVbGVhZCBTbWFydFNhdmVyIQAAOw==");

        service.publicBaseUrl = "http://img.com/asset/image";

        doAnswer(image("/img/ionic.png")).when(fileService).get(any(), any());

        // When
        final Image result = service.download(img);

        // Then
        assertThat(result.getBase64Content()).isEqualTo(img.getBase64Content());
        assertThat(result.getContentType()).isEqualTo(img.getContentType());
        assertThat(result.getHeight()).isEqualTo(128);
        assertThat(result.getId()).isEqualTo(img.getId());
        assertThat(result.getKey()).isEqualTo("images/" + img.getId() + "/0.png");
        assertThat(result.getPublicUrl()).isEqualTo("http://img.com/asset/image/" + img.getId());
        assertThat(result.getTitle()).isEqualTo(img.getTitle());
        assertThat(result.getUrl()).isEqualTo(img.getUrl());
        assertThat(result.getWidth()).isEqualTo(128);
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
