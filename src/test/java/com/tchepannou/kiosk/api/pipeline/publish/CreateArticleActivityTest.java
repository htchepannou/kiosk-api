package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.api.filter.ArticleFilterSet;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.jpa.FeedRepository;
import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import com.tchepannou.kiosk.api.pipeline.ActivityTestSupport;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import com.tchepannou.kiosk.core.filter.TextFilterSet;
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
public class CreateArticleActivityTest extends ActivityTestSupport {
    @Mock
    ArticleMapper articleMapper;

    @Mock
    FeedRepository feedRepository;

    @Mock
    ArticleFilterSet articleFilters;

    @Mock
    FileService fileService;

    @Mock
    ArticleRepository articleRepository;

    @Mock
    TextFilterSet textFilterSet;

    @InjectMocks
    CreateArticleActivity activity;

    @Test
    public void testGetTopic() throws Exception {
        assertThat(activity.getTopic()).isEqualTo(PipelineConstants.TOPIC_ARTICLE_SUBMITTED);
    }

    @Test
    public void shouldCreateActicle() throws Exception {
        // Given
        final PublishRequest request = Fixture.createPublishRequest();

        final Article article = Fixture.createArticle();
        when(articleMapper.toArticle(request)).thenReturn(article);

        final Feed feed = Fixture.createFeed();
        when(feedRepository.findOne(request.getFeedId())).thenReturn(feed);

        final String content = "!!! content !!!";
        when(textFilterSet.filter(any())).thenReturn(content);

        // When
        final Event event = new Event("foo", request);
        activity.doHandleEvent(event);

        // Then
        assertThatEventPublished(PipelineConstants.TOPIC_ARTICLE_CREATED, article);

        verify(articleRepository).save(article);

        final ArgumentCaptor<InputStream> in = ArgumentCaptor.forClass(InputStream.class);
        final ArgumentCaptor<String> key = ArgumentCaptor.forClass(String.class);
        verify(fileService).put(key.capture(), in.capture());

        assertThat(key.getValue()).isEqualTo(article.contentKey(Article.Status.submitted));
        assertThat(IOUtils.toString(in.getValue())).isEqualTo(content);
    }
}
