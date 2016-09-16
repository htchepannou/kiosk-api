package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.exception.ArticleNotFoundException;
import com.tchepannou.kiosk.api.exception.ContentNotFoundException;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.client.dto.ProcessRequest;
import com.tchepannou.kiosk.client.dto.ProcessResponse;
import com.tchepannou.kiosk.core.filter.TextFilterSet;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.tchepannou.kiosk.api.Fixture.createArticle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PipelineServiceTest {
    @Mock
    ArticleRepository articleRepository;

    @Mock
    ContentRepositoryService contentRepository;

    @Mock
    TextFilterSet filters;

    @InjectMocks
    PipelineService service;

    @Test
    public void shouldProcessArticle() throws Exception {
        // Given
        final Article article = createArticle();
        final String keyhash = article.getKeyhash();
        when(articleRepository.findOne(keyhash)).thenReturn(article);

        final String html = "hello world";
        doAnswer(new Answer() {
                     @Override
                     public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
                         final OutputStream out = (OutputStream)invocationOnMock.getArguments()[1];
                         out.write(html.getBytes());
                         return null;
                     }
                 }
        ).when(contentRepository).read(anyString(), any(OutputStream.class));

        final String xhtml = "!! hello";
        when(filters.filter(html)).thenReturn(xhtml);

        // When
        ProcessResponse response = service.process(createProcessRequest(keyhash));

        // Then
        assertThat(response.getTransactionId()).isEqualTo(keyhash);

        assertThat(article.getStatus()).isEqualTo(Article.Status.processed);
        verify(articleRepository).save(article);

        final ArgumentCaptor<String> key = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<InputStream> in = ArgumentCaptor.forClass(InputStream.class);
        verify(contentRepository).write(key.capture(), in.capture());
        assertThat(key.getValue()).isEqualTo(article.contentKey(Article.Status.processed));
        assertThat(IOUtils.toString(in.getValue())).isEqualTo(xhtml);
    }

    @Test(expected = ArticleNotFoundException.class)
    public void shouldThrowArticleNotFoundExceptionForInvalidArticle() throws Exception {
        service.process(createProcessRequest("????"));
    }

    @Test(expected = ContentNotFoundException.class)
    public void shouldThrowContentNotFoundExceptionForArticleWithNoContent() throws Exception {
        final String keyhash = "430940393";

        final Article article = new Article();
        when(articleRepository.findOne(keyhash)).thenReturn(article);

        doThrow(FileNotFoundException.class).when(contentRepository).read(anyString(), any(OutputStream.class));

        service.process(createProcessRequest(keyhash));
    }

    private ProcessRequest createProcessRequest(final String keyhash){
        final ProcessRequest request = new ProcessRequest();
        request.setKeyhash(keyhash);
        return request;
    }
}
