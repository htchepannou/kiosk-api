package com.tchepannou.kiosk.api.domain;

import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Article {
    public enum Status {
        submitted,
        processed
    }

    @Id
    private String id;
    private String url;
    private String title;
    private String slug;
    private String countryCode;
    private String languageCode;
    private Date publishedDate;
    private Status status;
    private Long feedId;

    public Article() {
    }

    public static String generateId(final String url) {
        return url == null
                ? null
                : DigestUtils.md5Hex(url.getBytes());
    }

    public String contentKey(final Status status) {
        return getId() + "/" + status.name() + ".html";
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(final String url) {
        this.url = url;
        this.id = generateId(url);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getSlug() {
        return this.slug;
    }

    public void setSlug(final String slug) {
        this.slug = slug;
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public void setLanguageCode(final String languageCode) {
        this.languageCode = languageCode;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    public Date getPublishedDate() {
        return this.publishedDate;
    }

    public void setPublishedDate(final Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Long getFeedId() {
        return feedId;
    }

    public void setFeedId(final Long feedId) {
        this.feedId = feedId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }
}
