package com.tchepannou.kiosk.api.pipeline;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ArticleActivityTest extends ActivityTestSupport{

    @Mock
    ArticleRepository articleRepository;

    @InjectMocks
    ArticleActivity activity = createArticleActivity();

    private String next;

    @Test
    public void shouldHandleArticleEvent() throws Exception {
        final Article article = new Article();


        next = "done";
        activity.doHandleEvent(new Event("foo", article));

        assertThatEventPublished("done", article);
        verify(articleRepository).save(article);
    }

    @Test
    public void shouldNotPublishEventWhenResultIsNull() throws Exception {
        final Article article = new Article();


        next = null;
        activity.doHandleEvent(new Event("foo", article));

        assertThatNoEventPublished();
        verify(articleRepository).save(article);
    }

    ArticleActivity createArticleActivity(){
        return new ArticleActivity() {
            @Override
            protected String doHandleArticle(final Article article) {
                return next;
            }

            @Override
            protected String getTopic() {
                return "foo";
            }
        };
    }
}
