package io.tchepannou.kiosk.api.service;

import io.tchepannou.kiosk.persistence.domain.Feed;
import io.tchepannou.kiosk.persistence.domain.Link;
import io.tchepannou.kiosk.persistence.domain.LinkStatusEnum;

import java.util.Date;

public class Fixtures {
    private static long uid = System.currentTimeMillis();

    public static Feed createFeed() {
        final Feed feed = new Feed();
        feed.setId(++uid);
        feed.setName("camfoot");
        feed.setUrl("http://camfoot.com");
        feed.setLogoUrl("feed/camfoot.png");
        return feed;
    }

    public static Link createArticle(final Feed feed) {
        final Link article = new Link();
        article.setFeed(feed);
        article.setId(++uid);
        article.setDisplayTitle("DisplayTitle");
        article.setPublishedDate(new Date());
        article.setS3Key("dev/content/222/11/1.html");
        article.setStatus(LinkStatusEnum.created);
        article.setSummary("Sumary");
        article.setTitle("Title");
        return article;
    }

}
