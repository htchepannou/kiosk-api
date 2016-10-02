package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.pipeline.ActivityTestSupport;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainImageActivityTest extends ActivityTestSupport{
    @Mock
    ImageRepository imageRepository;

    @Mock
    ArticleRepository articleRepository;

    @Mock
    FileService fileService;

    @InjectMocks
    MainImageActivity activity;

    @Before
    public void setUp (){
        activity.minHeidth = 100;
        activity.minWidth = 100;
    }

    @Test
    public void testGetTopic() throws Exception {
        assertThat(activity.getTopic()).isEqualTo(PipelineConstants.TOPIC_ARTICLE_IMAGES_DOWNLOADED);
    }

    @Test
    public void testDoHandleEvent() throws Exception {
        // Given
        final Article article = Fixture.createArticle();
        doAnswer(articleContent("<p> <img src='http://a.com/1.jpeg' /> <img src='http://a.com/2.jpeg' /> </p>"))
                .when(fileService).get(any(), any());

        final Image img1 = createImage(10, 10);
        final Image img2 = createImage(101, 110);
        when(imageRepository.findOne(any())).thenReturn(img1).thenReturn(img2);

        // When
        activity.doHandleEvent(new Event("foo", article));

        // Then
        assertThat(article.getImage()).isEqualTo(img2);
        verify(articleRepository).save(article);
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

    private Image createImage(final int w, final int h){
        final Image img = Fixture.createImage();
        img.setWidth(w);
        img.setHeight(h);
        return img;
    }
}
