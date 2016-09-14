package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.ErrorConstants;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.dto.PublishRequestDto;
import com.tchepannou.kiosk.api.dto.PublishResponseDto;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;

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

    @InjectMocks
    PublisherService service;

    @Test
    public void shouldPublishArticle() throws Exception {
        // Given
        PublishRequestDto request = createPublishRequest();

        Article article = mock(Article.class);
        when(article.contentKey(any())).thenReturn("/foo/bar");
        when(article.getKeyhash()).thenReturn("key-hash");
        when(articleMapper.toArticle(request)).thenReturn(article);

        // When
        PublishResponseDto response = service.publish(request);

        // Then
        verify(articleRepository).save(article);

        final ArgumentCaptor<InputStream> in = ArgumentCaptor.forClass(InputStream.class);
        final ArgumentCaptor<String> key = ArgumentCaptor.forClass(String.class);
        verify(contentRepositoryService).write(key.capture(), in.capture());

        assertThat(key.getValue()).isEqualTo("/foo/bar");
        assertThat(IOUtils.toString(in.getValue())).isEqualTo(request.getContent());

        assertThat(response.getTransactionId()).isEqualTo("key-hash");
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getErrorCode()).isNull();
        assertThat(response.getErrorMessage()).isNull();
    }


    @Test
    public void shouldNotRepublishArticle() throws Exception {
        // Given
        PublishRequestDto request = createPublishRequest();

        Article article = mock(Article.class);
        when(article.getKeyhash()).thenReturn("key-hash");
        when(articleRepository.findOne(anyString())).thenReturn(article);

        // When
        PublishResponseDto response = service.publish(request);

        // Then
        verify(articleRepository, never()).save(article);

        verify(contentRepositoryService, never()).write(any(), any());

        assertThat(response.getTransactionId()).isEqualTo("key-hash");
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getErrorCode()).isEqualTo(ErrorConstants.ALREADY_PUBLISHED);
        assertThat(response.getErrorMessage()).isNull();
    }
}
