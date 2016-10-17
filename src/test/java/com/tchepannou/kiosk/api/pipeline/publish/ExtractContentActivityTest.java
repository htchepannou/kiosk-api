package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.pipeline.ActivityTestSupport;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.service.ArticleService;
import com.tchepannou.kiosk.content.ContentExtractor;
import com.tchepannou.kiosk.content.FilterSetProvider;
import com.tchepannou.kiosk.content.TitleSanitizer;
import com.tchepannou.kiosk.core.service.FileService;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExtractContentActivityTest extends ActivityTestSupport {
    @Mock
    FileService fileService;

    @Mock
    ArticleService articleService;

    @Mock
    ContentExtractor extractor;

    @Mock
    FilterSetProvider filterSetProvider;

    @Mock
    TitleSanitizer titleSanitizer;

    @Mock
    ArticleRepository articleRepository;

    @InjectMocks
    ExtractContentActivity activity;

    @Test
    public void testGetTopic() throws Exception {
        assertThat(activity.getTopic()).isEqualTo(PipelineConstants.TOPIC_ARTICLE_CREATED);
    }

    @Test
    public void shouldProcessArticle() throws Exception {
        // Given
        final Article article = Fixture.createArticle();
        final String articleId = article.getId();
        when(articleRepository.findOne(articleId)).thenReturn(article);

        final String html = "hello world";
        when(articleService.fetchContent(any(), any())).thenReturn(html);

        final String xhtml = "<p id='item1'>!! hello</p><p id='item2'>world !!</p>";
        when(extractor.extract(any(), any())).thenReturn(xhtml);

        when(titleSanitizer.sanitize(any(), any())).thenReturn("!!! Title");

        // When
        final Event event = new Event("foo", article);
        activity.doHandleEvent(event);

        // Then
        assertThatEventPublished(PipelineConstants.TOPIC_ARTICLE_PROCESSED, article);

        assertThat(article.getTitle()).isEqualTo("!!! Title");
        assertThat(article.getStatus()).isEqualTo(Article.Status.processed);
        assertThat(article.getContentLength()).isEqualTo(17);
        assertThat(article.getContentCssId()).isEqualTo("item1");
        verify(articleRepository).save(article);

        final ArgumentCaptor<String> key = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<InputStream> in = ArgumentCaptor.forClass(InputStream.class);
        verify(fileService).put(key.capture(), in.capture());
        assertThat(key.getValue()).isEqualTo(article.contentKey(Article.Status.processed));
        assertThat(IOUtils.toString(in.getValue())).isEqualTo(xhtml);
    }
}
