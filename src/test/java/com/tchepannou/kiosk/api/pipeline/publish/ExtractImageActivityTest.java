package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.pipeline.ActivityTestSupport;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.service.ArticleService;
import com.tchepannou.kiosk.api.service.ImageService;
import com.tchepannou.kiosk.image.ImageExtractor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExtractImageActivityTest extends ActivityTestSupport {
    @Mock
    ImageRepository imageRepository;

    @Mock
    ArticleRepository articleRepository;

    @Mock
    ArticleService articleService;

    @Mock
    ImageExtractor extractor;

    @Mock
    ImageService imageService;

    @InjectMocks
    ExtractImageActivity activity;

    @Before
    public void setUp() {

        activity.minWidth = 100;
        activity.minHeidth = 100;
    }

    @Test
    public void testGetTopic() throws Exception {
        assertThat(activity.getTopic()).isEqualTo(PipelineConstants.TOPIC_ARTICLE_PROCESSED);
    }

    @Test
    public void testDoHandleEvent() throws Exception {
        // Given
        final Article article = Fixture.createArticle();

        final Image img1 = createImage(200, 200);
        final Image img2 = createImage(301, 310);
        when(imageService.getDimension(any()))
                .thenReturn(img1)
                .thenReturn(img2);

        final String html = "<html><body>" +
                "<img src='" + img1.getUrl() + "'>" +
                "<p id='main-content'>Hello</p>" +
                "<div>This is a bloc</div>" +
                "<img src='" + img2.getUrl() + "'>" +
                "</body></html>";
        when(articleService.fetchContent(any(), any())).thenReturn(html);

        when(extractor.extract(any(), any())).thenReturn(img1.getUrl());
        when(imageRepository.findOne(img1.getId())).thenReturn(img1);

        // When
        activity.doHandleEvent(new Event("foo", article));

        // Then
        assertThat(articleRepository.save(article));
        assertThat(article.getImage()).isEqualTo(img1);
    }

    private Image createImage(final int w, final int h) {
        final Image img = Fixture.createImage();
        final String url = "http://www.img.com/" + UUID.randomUUID();

        img.setWidth(w);
        img.setHeight(h);
        img.setId(Image.generateId(url));
        img.setUrl(url);

        return img;
    }

}
