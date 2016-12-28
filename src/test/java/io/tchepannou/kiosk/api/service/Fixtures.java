package io.tchepannou.kiosk.api.service;

import io.tchepannou.kiosk.api.persistence.domain.Article;
import io.tchepannou.kiosk.api.persistence.domain.Feed;
import io.tchepannou.kiosk.api.persistence.domain.Image;
import io.tchepannou.kiosk.api.persistence.domain.Link;

import java.util.Date;

public class Fixtures {
    private static long uid = System.currentTimeMillis();

    public static Feed createFeed (){
        Feed feed = new Feed();
        feed.setId(System.currentTimeMillis());
        feed.setName("camfoot");
        feed.setUrl("http://camfoot.com");
        feed.setLogoUrl("dev/img/camfoot.png");
        return feed;
    }

    public static Link createLink (Feed feed){
        final Link link = new Link();
        link.setId(System.currentTimeMillis());
        link.setFeed(feed);
        return link;
    }

    public static Image createImage (Link link, int type){
        final Image img = new Image();
        img.setId(System.currentTimeMillis());
        img.setLink(link);
        img.setS3Key("dev/content/222/11/" + img.getId() + ".png");
        img.setType(type);
        return img;
    }

    public static Article createArticle(final Link link) {
        final Article article = new Article();
        article.setId(System.currentTimeMillis());
        article.setDisplayTitle("DisplayTitle");
        article.setLink(link);
        article.setPublishedDate(new Date());
        article.setS3Key("dev/content/222/11/1.html");
        article.setStatus(0);
        article.setSummary("Sumary");
        article.setTitle("Title");
        return article;
    }
    
}
