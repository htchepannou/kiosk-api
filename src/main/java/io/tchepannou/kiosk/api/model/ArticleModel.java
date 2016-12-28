package io.tchepannou.kiosk.api.model;

import java.util.Date;

public class ArticleModel {
    private long id;
    private FeedModel feed;
    private String title;
    private String displayTitle;
    private String summary;
    private Date publishedDate;
    private String contentUrl;
    private String imageUrl;
    private String thumbnailUrl;

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public FeedModel getFeed() {
        return feed;
    }

    public void setFeed(final FeedModel feed) {
        this.feed = feed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDisplayTitle() {
        return displayTitle;
    }

    public void setDisplayTitle(final String displayTitle) {
        this.displayTitle = displayTitle;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(final Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(final String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(final String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
