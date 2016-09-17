package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import com.tchepannou.kiosk.client.dto.ArticleDto;
import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.client.dto.GetArticleResponse;
import com.tchepannou.kiosk.core.service.LogService;
import com.tchepannou.kiosk.core.service.TransactionIdProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.OutputStream;
import java.util.UUID;

import static com.tchepannou.kiosk.api.Fixture.createArticle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
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

    @InjectMocks
    ArticleService service;

    String transactionId;

    @Before
    public void setUp() {
        transactionId = UUID.randomUUID().toString();
        when(transactionIdProvider.get()).thenReturn(transactionId);
    }

    @Test
    public void shouldReturnArticle() throws Exception {
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
    public void shouldReturnErrorWhenArticleNotFound() throws Exception {
        // When
        final GetArticleResponse response = service.get("???");

        // Then
        assertThat(response.getTransactionId()).isEqualTo(transactionId);
        assertThat(response.getArticle()).isNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getCode()).isEqualTo(ErrorConstants.ARTICLE_NOT_FOUND);
    }

    @Test
    public void shouldReturnErrorWhenContentNotFound() throws Exception {
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
