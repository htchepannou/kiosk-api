package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import com.tchepannou.kiosk.client.dto.ArticleDto;
import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.client.dto.GetArticleResponse;
import com.tchepannou.kiosk.client.dto.ProcessRequest;
import com.tchepannou.kiosk.client.dto.ProcessResponse;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import com.tchepannou.kiosk.client.dto.PublishResponse;
import com.tchepannou.kiosk.core.filter.TextFilterSet;
import com.tchepannou.kiosk.core.service.LogService;
import com.tchepannou.kiosk.core.service.TransactionIdProvider;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static com.tchepannou.kiosk.api.Fixture.createArticle;
import static com.tchepannou.kiosk.api.Fixture.createPublishRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArticleServiceTest {
    @Mock
    ArticleRepository articleRepository;

    @Mock
    ContentRepositoryService contentRepository;

    @Mock
    ArticleMapper articleMapper;

    @Mock
    LogService logService;

    @Mock
    TransactionIdProvider transactionIdProvider;

    @Mock
    TextFilterSet filters;

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
    public void getArticleShouldReturnArticle() throws Exception {
        // Given
        final Article article = createArticle();
        final String articleId = article.getId();
        when(articleRepository.findOne(articleId)).thenReturn(article);

        final ArticleDto dto = new ArticleDto();
        when(articleMapper.toArticleDto(article)).thenReturn(dto);

        final String html = "hello world";
        doAnswer(read(html)).when(contentRepository).read(anyString(), any(OutputStream.class));

        // When
        final GetArticleResponse response = service.get(articleId);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticle()).isEqualTo(dto);

        assertThat(dto.getData().getContent()).isEqualTo("hello world");
    }

    @Test
    public void getArticleShouldReturnErrorWhenArticleNotFound() throws Exception {
        // When
        final GetArticleResponse response = service.get("???");

        // Then
        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticle()).isNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getCode()).isEqualTo(ErrorConstants.ARTICLE_NOT_FOUND);
    }

    @Test
    public void getArticleShouldReturnErrorWhenContentNotFound() throws Exception {
        // Given
        final Article article = createArticle();
        final String articleId = article.getId();
        when(articleRepository.findOne(articleId)).thenReturn(article);

        final ArticleDto dto = new ArticleDto();
        when(articleMapper.toArticleDto(article)).thenReturn(dto);

        final ContentRepositoryException ex = new ContentRepositoryException(new RuntimeException());
        doThrow(ex).when(contentRepository).read(anyString(), any(OutputStream.class));

        // When
        final GetArticleResponse response = service.get(articleId);

        // Then
        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticle()).isNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getCode()).isEqualTo(ErrorConstants.CONTENT_NOT_FOUND);
    }

    //-- Process
    @Test
    public void shouldProcessArticle() throws Exception {
        // Given
        final Article article = createArticle();
        final String articleId = article.getId();
        when(articleRepository.findOne(articleId)).thenReturn(article);

        final String html = "hello world";
        doAnswer(read(html)).when(contentRepository).read(anyString(), any(OutputStream.class));

        final String xhtml = "!! hello";
        when(filters.filter(html)).thenReturn(xhtml);

        // When
        final ProcessResponse response = service.process(createProcessRequest(articleId));

        // Then
        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticleId()).isEqualTo(articleId);
        assertThat(response.isSuccess()).isTrue();

        assertThat(article.getStatus()).isEqualTo(Article.Status.processed);
        verify(articleRepository).save(article);

        final ArgumentCaptor<String> key = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<InputStream> in = ArgumentCaptor.forClass(InputStream.class);
        verify(contentRepository).write(key.capture(), in.capture());
        assertThat(key.getValue()).isEqualTo(article.contentKey(Article.Status.processed));
        assertThat(IOUtils.toString(in.getValue())).isEqualTo(xhtml);
    }

    @Test
    public void shouldNotProcessArticleNotFound() throws Exception {
        // When
        final ProcessResponse response = service.process(createProcessRequest("????"));

        // Then
        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticleId()).isEqualTo("????");
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getCode()).isEqualTo(ErrorConstants.ARTICLE_NOT_FOUND);
    }

    @Test
    public void shouldNotProcessWhenDBError() throws Exception {
        // Given
        when(articleRepository.findOne(any())).thenThrow(new DataIntegrityViolationException("failed"));

        // When
        final ProcessResponse response = service.process(createProcessRequest("????"));

        // Then
        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticleId()).isEqualTo("????");
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getCode()).isEqualTo(ErrorConstants.ARTICLE_NOT_FOUND);
    }

    @Test
    public void shouldNotProcessWhebContentNotFound() throws Exception {
        // Givevn
        final String articleId = "430940393";
        final Article article = new Article();
        when(articleRepository.findOne(articleId)).thenReturn(article);

        final ContentRepositoryException ex = new ContentRepositoryException(new FileNotFoundException());
        doThrow(ex).when(contentRepository).read(anyString(), any(OutputStream.class));

        final ProcessResponse response = service.process(createProcessRequest(articleId));

        // Then
        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticleId()).isEqualTo(articleId);
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getCode()).isEqualTo(ErrorConstants.CONTENT_NOT_FOUND);
    }

    //-- Publish
    @Test
    public void shouldPublishArticle() throws Exception {
        // Given
        final PublishRequest request = createPublishRequest();

        final Article article = mock(Article.class);
        when(article.contentKey(any())).thenReturn("/foo/bar");
        when(article.getId()).thenReturn("key-hash");
        when(articleMapper.toArticle(request)).thenReturn(article);

        // When
        final PublishResponse response = service.publish(request);

        // Then
        verify(articleRepository).save(article);

        final ArgumentCaptor<InputStream> in = ArgumentCaptor.forClass(InputStream.class);
        final ArgumentCaptor<String> key = ArgumentCaptor.forClass(String.class);
        verify(contentRepository).write(key.capture(), in.capture());

        assertThat(key.getValue()).isEqualTo("/foo/bar");
        assertThat(IOUtils.toString(in.getValue())).isEqualTo(request.getArticle().getContent());

        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticleId()).isEqualTo("key-hash");
        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    public void shouldNotRepublishArticle() throws Exception {
        // Given
        final PublishRequest request = createPublishRequest();

        final Article article = mock(Article.class);
        when(article.getId()).thenReturn("key-hash");
        when(articleRepository.findOne(anyString())).thenReturn(article);

        // When
        final PublishResponse response = service.publish(request);

        // Then
        verify(articleRepository, never()).save(article);

        verify(contentRepository, never()).write(any(), any());

        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticleId()).isEqualTo("key-hash");
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getCode()).isEqualTo(ErrorConstants.ALREADY_PUBLISHED);
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
