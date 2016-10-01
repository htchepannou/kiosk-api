package com.tchepannou.kiosk.api.filter;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Article;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArticleTitleFilterTest {

    private ArticleTitleFilter filter = new ArticleTitleFilter(10);
    
    @Test
    public void shouldNotFilterShortTitle() throws Exception {
        final Article article = createArticle("12345");
        
        final Article result = filter.filter(article);
        
        assertThat(result.getTitle()).isEqualTo("12345");
    }

    @Test
    public void shouldRemoveWebsiteFromTitleSuffix() throws Exception {
        final Article article = createArticle("12345");
        article.setTitle(article.getTitle() + "-" + article.getFeed().getWebsite().getName());

        final Article result = filter.filter(article);

        assertThat(result.getTitle()).isEqualTo("12345");
    }

    @Test
    public void shouldRemoveWebsiteFromTitlePrefix() throws Exception {
        final Article article = createArticle("12345");
        article.setTitle(article.getFeed().getWebsite().getName() + ":" + article.getTitle());

        final Article result = filter.filter(article);

        assertThat(result.getTitle()).isEqualTo("12345");
    }

    @Test
    public void shouldNoRemovePrefixIfTitleNotTooLong() throws Exception {
        final Article article = createArticle("p: 12345");

        final Article result = filter.filter(article);

        assertThat(result.getTitle()).isEqualTo("p: 12345");
    }

    @Test
    public void shouldRemovePrefixIfTitleTooLong() throws Exception {
        final Article article = createArticle("pppp: 1234567890");

        final Article result = filter.filter(article);

        assertThat(result.getTitle()).isEqualTo("1234567890");
    }

    private Article createArticle (final String title){
        final Article article = Fixture.createArticle();
        article.setTitle(title);
        return article;
    }
}
