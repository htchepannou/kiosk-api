package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.pipeline.ActivityTestSupport;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.support.ArticleImageSet;
import com.tchepannou.kiosk.api.service.ImageService;
import com.tchepannou.kiosk.core.service.FileService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.OutputStream;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MainImageActivityTest extends ActivityTestSupport {
    @Mock
    ImageRepository imageRepository;

    @Mock
    ArticleRepository articleRepository;

    @Mock
    FileService fileService;

    @Mock
    ImageService imageService;

    @InjectMocks
    MainImageActivity activity;

    @Before
    public void setUp() {
        activity.minHeidth = 100;
        activity.minWidth = 100;
    }

    @Test
    public void testGetTopic() throws Exception {
        assertThat(activity.getTopic()).isEqualTo(PipelineConstants.TOPIC_ARTICLE_IMAGES_DOWNLOADED);
    }

    @Test
    public void shouldSelectMainImage() throws Exception {
        // Given
        final Article article = Fixture.createArticle();
        article.setContentCssId("main-content");

        final Image img1 = createImage(200, 200);
        final Image img2 = createImage(301, 310);

        final String html = "<html><body>" +
                "<div>" +
                        "<img src='" + img1.getUrl() + ">" +
                        "<p id='main-content'>Hello</p>" +
                "</div>" +
                "<div>" +
                        "<img src='" + img2.getUrl() + ">" +
                "</div>" +
                "</body></html>"
        ;
        doAnswer(articleContent(html)).when(fileService).get(any(), any());

        // When
        activity.doHandleEvent(new Event("foo", new ArticleImageSet(article, Arrays.asList(img1, img2))));

        // Then
        assertThat(article.getImage()).isEqualTo(img1);
        verify(articleRepository).save(article);
    }

    @Test
    public void shouldNotSelectMainImageWithWidthBelowThreshold() throws Exception {
        // Given
        final Article article = Fixture.createArticle();

        final Image img1 = createImage(10, 400);

        // When
        activity.doHandleEvent(new Event("foo", new ArticleImageSet(article, Arrays.asList(img1))));

        // Then
        assertThat(article.getImage()).isNull();
        verify(articleRepository).save(article);
    }

    @Test
    public void shouldNotSelectMainImageWithHeightBellowThreshold() throws Exception {
        // Given
        final Article article = Fixture.createArticle();

        final Image img1 = createImage(400, 10);

        // When
        activity.doHandleEvent(new Event("foo", new ArticleImageSet(article, Arrays.asList(img1))));

        // Then
        assertThat(article.getImage()).isNull();
        verify(articleRepository).save(article);
    }

    private Image createImage(final int w, final int h) {
        final Image img = Fixture.createImage();
        img.setWidth(w);
        img.setHeight(h);
        return img;
    }

    private Answer articleContent(final String html) throws Exception {
        return new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
                final OutputStream out = (OutputStream) invocationOnMock.getArguments()[1];
                out.write(html.getBytes());
                return null;
            }
        };
    }
}
