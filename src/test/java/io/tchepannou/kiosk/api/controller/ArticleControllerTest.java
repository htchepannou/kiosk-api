package io.tchepannou.kiosk.api.controller;

import io.tchepannou.kiosk.api.model.ArticleModelList;
import io.tchepannou.kiosk.api.service.ArticleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArticleControllerTest {
    @Mock
    ArticleService service;

    @InjectMocks
    ArticleController controller;

    @Test
    public void shouldReturnArticles(){
        final ArticleModelList articles = new ArticleModelList();
        when(service.list(anyInt(), anyInt())).thenReturn(articles);

        final ArticleModelList result = controller.list(1, 2);

        assertThat(result).isEqualTo(articles);
    }
}
