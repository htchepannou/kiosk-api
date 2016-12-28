package io.tchepannou.kiosk.api.service;

import io.tchepannou.kiosk.api.persistence.repository.ArticleRepository;
import io.tchepannou.kiosk.api.persistence.repository.ImageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ArticleServiceTest {
    @Mock
    ArticleRepository articleRepository;

    @Mock
    ImageRepository imageRepository;

    @Mock
    ArticleMapper mapper;

    @InjectMocks
    ArticleService service;

    @Test
    public void testList() throws Exception {

    }
}
