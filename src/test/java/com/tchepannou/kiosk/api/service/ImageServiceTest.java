package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.core.service.FileService;
import com.tchepannou.kiosk.core.service.HttpService;
import com.tchepannou.kiosk.core.service.LogService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.io.OutputStream;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceTest {
    @Mock
    TransactionStatus tx;
    @Mock
    PlatformTransactionManager txManager;

    @Mock
    ArticleRepository articleRepository;

    @Mock
    FileService fileService;

    @Mock
    HttpService httpService;

    @Mock
    ImageRepository imageRepository;

    @Mock
    LogService logService;

    @InjectMocks
    ImageService service;

    @Before
    public void setUp() {
        when(txManager.getTransaction(any())).thenReturn(tx);
    }

    @Test
    public void shouldGrabImages() throws Exception{
        // Given
        final Article article = new Article();
        article.setId("123435");

        doAnswer(articleContent("<p> <img src='img/image1.jpeg' alt='image #1' /> </p>")).when(fileService).get(any(), any());

        when(httpService.get(any(), any(), any())).thenReturn("images/12345/1.jpeg");

        // When
        service.process(article);

        // Then
        ArgumentCaptor<Image> image = ArgumentCaptor.forClass(Image.class);
        verify(imageRepository).save(image.capture());
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
}
