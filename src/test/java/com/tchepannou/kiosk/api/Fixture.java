package com.tchepannou.kiosk.api;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.api.dto.PublishRequestDto;
import com.tchepannou.kiosk.core.service.TimeService;

public class Fixture {
    private static long uid = System.currentTimeMillis();

    public static PublishRequestDto createPublishRequest() {
        final PublishRequestDto request = new PublishRequestDto();
        request.setContent("<p>This is the content of the article</p>");
        request.setCountryCode("CA");
        request.setFeedId(112);
        request.setLanguageCode("FR");
        request.setPublishedDate("2012-10-15 11:00:00 -0500");
        request.setSlug("This is a slug");
        request.setTitle("Title of article");
        request.setUrl("http://fdlkfd.com/1232");

        return request;
    }

    public static Feed createFeed() {
        final Feed feed = new Feed();
        final long id = ++uid;

        feed.setCountryCode("US");
        feed.setId(id);
        feed.setName("Feed #" + id);
        feed.setType("rss");
        feed.setUrl("http://www.google.ca/" + id + "/rss");
        return feed;
    }

    public static Article createArticle() {
        final Article article = new Article();
        final long id = ++uid;

        article.setCountryCode("US");
        article.setFeedId(id);
        article.setLanguageCode("en");
        article.setPublishedDate(new TimeService().now());
        article.setSlug("This is a slug");
        article.setStatus(Article.Status.submitted);
        article.setTitle("This is title");
        article.setUrl("http://article.com/" + id);

        return article;
    }
}
