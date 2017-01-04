package io.tchepannou.kiosk.api.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArticleModel {
    private long id;
    private FeedModel feed;
    private ImageModel mainImage;
    private ImageModel thumbnailImage;
    private String title;
    private String displayTitle;
    private String summary;
    private Date publishedDate;
    private String contentUrl;
    private String url;
    private List<VideoModel> videos;

    public void addVideo(VideoModel video){
        if (videos == null){
            videos = new ArrayList<>();
        }
        videos.add(video);
    }

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

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public ImageModel getMainImage() {
        return mainImage;
    }

    public void setMainImage(final ImageModel mainImage) {
        this.mainImage = mainImage;
    }

    public ImageModel getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(final ImageModel thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public List<VideoModel> getVideos() {
        return videos;
    }

    public void setVideos(final List<VideoModel> videos) {
        this.videos = videos;
    }
}
