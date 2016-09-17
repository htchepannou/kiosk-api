package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import com.tchepannou.kiosk.client.dto.PublishResponse;
import com.tchepannou.kiosk.core.service.LogService;
import com.tchepannou.kiosk.core.service.TransactionIdProvider;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.UUID;

import static com.tchepannou.kiosk.api.Fixture.createPublishRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PublisherServiceTest {
    @Mock
    ArticleRepository articleRepository;

    @Mock
    ContentRepositoryService contentRepositoryService;

    @Mock
    ArticleMapper articleMapper;

    @Mock
    LogService logService;

    @Mock
    TransactionIdProvider transactionIdProvider;

    @InjectMocks
    PublisherService service;

    String transactionId;

    @Before
    public void setUp (){
        transactionId = UUID.randomUUID().toString();
        when(transactionIdProvider.get()).thenReturn(transactionId);
    }

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
        verify(contentRepositoryService).write(key.capture(), in.capture());

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

        verify(contentRepositoryService, never()).write(any(), any());

        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticleId()).isEqualTo("key-hash");
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getCode()).isEqualTo(ErrorConstants.ALREADY_PUBLISHED);
    }
}
