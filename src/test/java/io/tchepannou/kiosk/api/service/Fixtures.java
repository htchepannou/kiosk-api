package io.tchepannou.kiosk.api.service;

import io.tchepannou.kiosk.api.persistence.domain.Article;
import io.tchepannou.kiosk.api.persistence.domain.Feed;
import io.tchepannou.kiosk.api.persistence.domain.Image;
import io.tchepannou.kiosk.api.persistence.domain.Link;
import io.tchepannou.kiosk.api.persistence.domain.Video;

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

    public static Link createLink(final Feed feed) {
        final Link link = new Link();
        link.setId(++uid);
        link.setFeed(feed);
        link.setUrl("http://ggg.com/" + uid);
        return link;
    }

    public static Video createVideo (final Link link){
        final Video video = new Video();
        video.setEmbedUrl("http://you.be/embed/1290190");
        video.setId(++uid);
        video.setLink(link);
        return video;
    }


    public static Image createImage(final Link link, final int type) {
        final Image img = new Image();
        img.setId(++uid);
        img.setLink(link);
        img.setS3Key("dev/content/222/11/" + img.getId() + ".png");
        img.setType(type);
        return img;
    }

    public static Article createArticle(final Link link) {
        final Article article = new Article();
        article.setId(++uid);
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
