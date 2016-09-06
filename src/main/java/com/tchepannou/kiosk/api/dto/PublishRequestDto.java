package com.tchepannou.kiosk.api.dto;

import java.util.Date;

public class PublishRequestDto {
    private long feedId;
    private String url;
    private String title;
    private String slug;
    private String countryCode;
    private String languageCode;
    private Date publishedDate;
    private String content;

    public long getFeedId() {
        return feedId;
    }

    public void setFeedId(final long feedId) {
        this.feedId = feedId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(final String slug) {
        this.slug = slug;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(final String languageCode) {
        this.languageCode = languageCode;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(final Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }
}
