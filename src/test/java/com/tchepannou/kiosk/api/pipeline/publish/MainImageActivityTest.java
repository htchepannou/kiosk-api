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
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
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

        final Image img1 = createImage(200, 200);
        final Image img2 = createImage(301, 310);

        // When
        activity.doHandleEvent(new Event("foo", new ArticleImageSet(article, Arrays.asList(img1, img2))));

        // Then
        assertThat(article.getImage()).isEqualTo(img2);
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
}
