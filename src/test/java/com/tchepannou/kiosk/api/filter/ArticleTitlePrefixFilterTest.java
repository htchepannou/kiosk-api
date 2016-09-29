package com.tchepannou.kiosk.api.filter;

import com.tchepannou.kiosk.api.domain.Article;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArticleTitlePrefixFilterTest {

    @Test
    public void testFilter() throws Exception {
        final Article article = new Article();
        article.setTitle("Cameroon - Justice: Biya met tout le monde en prison");

        new ArticleTitlePrefixFilter().filter(article);

        assertThat(article.getTitle()).isEqualTo("Justice: Biya met tout le monde en prison");
    }
}
