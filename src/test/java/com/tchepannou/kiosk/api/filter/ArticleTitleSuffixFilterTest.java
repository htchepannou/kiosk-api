package com.tchepannou.kiosk.api.filter;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.api.domain.Website;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArticleTitleSuffixFilterTest {

    @Test
    public void testFilter() throws Exception {
        final Website website = new Website();
        website.setName("CameroonOnline.org");

        final Feed feed = new Feed();
        feed.setWebsite(website);

        final Article article = new Article();
        article.setFeed(feed);
        article.setTitle("Biya met tout le monde en prison - CameroonOnline.org");

        new ArticleTitleSuffixFilter().filter(article);

        assertThat(article.getTitle()).isEqualTo("Biya met tout le monde en prison ");

    }
}
