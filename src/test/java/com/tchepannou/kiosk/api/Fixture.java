package com.tchepannou.kiosk.api;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.client.dto.ArticleDataDto;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import com.tchepannou.kiosk.core.service.TimeService;

import java.io.File;

public class Fixture {
    private static long uid = System.currentTimeMillis();

    public static void deleteRecursive(File home){
        if (home.isDirectory()) {
            final File[] files = home.listFiles();
            if (files != null && files.length>0){
                for (File file : files){
                    deleteRecursive(file);
                }

            }
        }

        home.delete();
    }

    public static PublishRequest createPublishRequest() {
        final PublishRequest request = new PublishRequest();
        request.setFeedId(112);
        request.setArticle(createArticleDataDto());

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

        article.setId(String.valueOf(id));
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

    public static ArticleDataDto createArticleDataDto(){
        ArticleDataDto article = new ArticleDataDto();

        article.setContent("<p>This is the content of the article</p>");
        article.setCountryCode("CA");
        article.setLanguageCode("FR");
        article.setPublishedDate("2012-10-15 11:00:00 -0500");
        article.setSlug("This is a slug");
        article.setTitle("Title of article");
        article.setUrl("http://fdlkfd.com/1232");

        return article;
    }

    public static PublishRequest createPublishRequest(long feedId, ArticleDataDto article) {
        PublishRequest request = new PublishRequest();
        request.setArticle(article);
        request.setFeedId(feedId);
        return request;
    }
}
