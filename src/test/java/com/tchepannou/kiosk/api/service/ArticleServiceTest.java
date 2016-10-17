package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.jpa.FeedRepository;
import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import com.tchepannou.kiosk.api.mapper.ImageMapper;
import com.tchepannou.kiosk.api.mapper.WebsiteMapper;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.client.dto.ArticleDto;
import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.client.dto.GetArticleResponse;
import com.tchepannou.kiosk.client.dto.ImageDto;
import com.tchepannou.kiosk.client.dto.ProcessRequest;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import com.tchepannou.kiosk.client.dto.PublishResponse;
import com.tchepannou.kiosk.client.dto.WebsiteDto;
import com.tchepannou.kiosk.core.service.FileService;
import com.tchepannou.kiosk.core.service.LogService;
import com.tchepannou.kiosk.core.service.TimeService;
import com.tchepannou.kiosk.core.service.TransactionIdProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArticleServiceTest {
    @Mock
    protected ApplicationEventPublisher publisher;

    @Mock
    ArticleRepository articleRepository;

    @Mock
    FileService fileService;

    @Mock
    ArticleMapper articleMapper;

    @Mock
    WebsiteMapper websiteMapper;

    @Mock
    LogService logService;

    @Mock
    FeedRepository feedRepository;

    @Mock
    TransactionIdProvider transactionIdProvider;

    @Mock
    ImageMapper imageMapper;

    @Mock
    TimeService timeService;

    @InjectMocks
    ArticleService service;

    String transactionId;

    @Before
    public void setUp() {
        transactionId = UUID.randomUUID().toString();
        when(transactionIdProvider.get()).thenReturn(transactionId);
    }

    //-- GetArticle
    @Test
    public void shouldReturnArticleWithContent() throws Exception {
        // Given
        final Article article = Fixture.createArticle();
        final String articleId = article.getId();
        when(articleRepository.findOne(articleId)).thenReturn(article);

        final ArticleDto articleDto = new ArticleDto();
        when(articleMapper.toArticleDto(article)).thenReturn(articleDto);

        final WebsiteDto websiteDto = new WebsiteDto();
        when(websiteMapper.toWebsiteDto(any())).thenReturn(websiteDto);

        final String html = "hello world";
        doAnswer(read(html)).when(fileService).get(anyString(), any(OutputStream.class));

        // When
        final GetArticleResponse response = service.get(articleId, true);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticle()).isEqualTo(articleDto);

        assertThat(articleDto.getContent()).isEqualTo("hello world");
    }

    @Test
    public void shouldReturnArticleWithoutContent() throws Exception {
        // Given
        final Article article = Fixture.createArticle();
        final String articleId = article.getId();
        when(articleRepository.findOne(articleId)).thenReturn(article);

        final ArticleDto articleDto = new ArticleDto();
        when(articleMapper.toArticleDto(article)).thenReturn(articleDto);

        final WebsiteDto websiteDto = new WebsiteDto();
        when(websiteMapper.toWebsiteDto(any())).thenReturn(websiteDto);

        // When
        final GetArticleResponse response = service.get(articleId, false);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticle()).isEqualTo(articleDto);

        verify(fileService, never()).get(anyString(), any(OutputStream.class));

        assertThat(articleDto.getContent()).isNull();
    }

    @Test
    public void shouldReturnArticleWithImage() throws Exception {
        // Given
        final Article article = Fixture.createArticle();
        final String articleId = article.getId();
        when(articleRepository.findOne(articleId)).thenReturn(article);

        final ArticleDto articleDto = new ArticleDto();
        when(articleMapper.toArticleDto(article)).thenReturn(articleDto);

        final WebsiteDto websiteDto = new WebsiteDto();
        when(websiteMapper.toWebsiteDto(any())).thenReturn(websiteDto);

        article.setImage(new Image());
        final ImageDto imageDto = new ImageDto();
        when(imageMapper.toImageDto(any())).thenReturn(imageDto);

        final String html = "hello world";
        doAnswer(read(html)).when(fileService).get(anyString(), any(OutputStream.class));

        // When
        final GetArticleResponse response = service.get(articleId, true);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticle()).isEqualTo(articleDto);

        assertThat(articleDto.getContent()).isEqualTo("hello world");
    }

    @Test
    public void shouldReturnErrorWhenArticleNotFound() throws Exception {
        // When
        final GetArticleResponse response = service.get("???", false);

        // Then
        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticle()).isNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getCode()).isEqualTo(ErrorConstants.ARTICLE_NOT_FOUND);
    }

    @Test
    public void shouldReturnErrorWhenContentNotFound() throws Exception {
        // Given
        final Article article = Fixture.createArticle();
        final String articleId = article.getId();
        when(articleRepository.findOne(articleId)).thenReturn(article);

        final ArticleDto dto = new ArticleDto();
        when(articleMapper.toArticleDto(article)).thenReturn(dto);

        final IOException ex = new IOException(new RuntimeException());
        doThrow(ex).when(fileService).get(anyString(), any(OutputStream.class));

        // When
        final GetArticleResponse response = service.get(articleId, true);

        // Then
        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticle()).isNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getCode()).isEqualTo(ErrorConstants.CONTENT_NOT_FOUND);
    }

    //-- Publish
    @Test
    public void shouldPublish() throws Exception {
        // Given
        final PublishRequest request = Fixture.createPublishRequest();

        when(articleRepository.findOne(any())).thenReturn(null);

        when(feedRepository.findOne(any())).thenReturn(new Feed());

        // When
        final PublishResponse response = service.publish(request);

        // Then
        final ArgumentCaptor<Event> event = ArgumentCaptor.forClass(Event.class);
        verify(publisher).publishEvent(event.capture());
        assertThat(event.getValue().getTopic()).isEqualTo(PipelineConstants.TOPIC_ARTICLE_SUBMITTED);
        assertThat(event.getValue().getPayload()).isEqualTo(request);

        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticleId()).isNotNull();
        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    public void shouldRePublishIfForceIsSet() throws Exception {
        // Given
        final PublishRequest request = Fixture.createPublishRequest();
        request.setForce(true);

        when(articleRepository.findOne(any())).thenReturn(new Article());

        when(feedRepository.findOne(any())).thenReturn(new Feed());

        // When
        final PublishResponse response = service.publish(request);

        // Then
        final ArgumentCaptor<Event> event = ArgumentCaptor.forClass(Event.class);
        verify(publisher).publishEvent(event.capture());
        assertThat(event.getValue().getTopic()).isEqualTo(PipelineConstants.TOPIC_ARTICLE_SUBMITTED);
        assertThat(event.getValue().getPayload()).isEqualTo(request);

        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticleId()).isNotNull();
        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    public void shouldNotRePublishIfForceIsSet() throws Exception {
        // Given
        final PublishRequest request = Fixture.createPublishRequest();
        request.setForce(false);

        when(articleRepository.findOne(any())).thenReturn(new Article());

        when(feedRepository.findOne(any())).thenReturn(new Feed());

        // When
        final PublishResponse response = service.publish(request);

        // Then
        verify(publisher, never()).publishEvent(any());

        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticleId()).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getCode()).isEqualTo(ErrorConstants.ALREADY_PUBLISHED);
    }


    @Test
    public void shouldNotPublishIfFeedIsInvalid() throws Exception {
        // Given
        final PublishRequest request = Fixture.createPublishRequest();
        request.setForce(false);

        when(articleRepository.findOne(any())).thenReturn(null);

        when(feedRepository.findOne(any())).thenReturn(null);

        // When
        final PublishResponse response = service.publish(request);

        // Then
        verify(publisher, never()).publishEvent(any());

        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticleId()).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getCode()).isEqualTo(ErrorConstants.FEED_INVALID);
    }

    //-- Private
    private ProcessRequest createProcessRequest(final String articleId) {
        final ProcessRequest request = new ProcessRequest();
        request.setArticleId(articleId);
        return request;
    }

    private Answer read(final String html) {
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
