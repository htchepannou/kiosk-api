package io.tchepannou.kiosk.api.service;

import io.tchepannou.kiosk.api.persistence.domain.Article;
import io.tchepannou.kiosk.api.persistence.domain.Feed;
import io.tchepannou.kiosk.api.persistence.domain.Image;
import io.tchepannou.kiosk.api.persistence.domain.Link;
import io.tchepannou.kiosk.api.persistence.domain.Video;

import java.util.ArrayList;
import java.util.List;

public class ArticleContainer {
    private Article article;
    private Image image;
    private Image thumbnail;
    private Link link;
    private List<Video> videos = new ArrayList<>();

    public void addVideo(Video video){
        if (videos == null){
            videos = new ArrayList<>();
        }
        videos.add(video);
    }


    public Link getLink() {
        return link;
    }

    public void setLink(final Link link) {
        this.link = link;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(final Article article) {
        this.article = article;
    }

    public Feed getFeed() {
        return link != null ? link.getFeed() : null;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(final Image image) {
        this.image = image;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(final Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(final List<Video> videos) {
        this.videos = videos;
    }
}
