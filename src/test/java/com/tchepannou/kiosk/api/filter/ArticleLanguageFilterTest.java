package com.tchepannou.kiosk.api.filter;

import com.tchepannou.kiosk.api.domain.Article;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArticleLanguageFilterTest {

    @Test
    public void shouldDetectFrench() throws Exception {
        final Article article = new Article();
        article.setTitle("Paul Biya en visite en France pour raison indeterminée");
        article.setSlug("Le president de la république a quitté précipipamment la capitale pour une destination inconne");

        final Article result = new ArticleLanguageFilter().filter(article);

        assertThat(result.getLanguageCode()).isEqualTo("fr");
    }
}
