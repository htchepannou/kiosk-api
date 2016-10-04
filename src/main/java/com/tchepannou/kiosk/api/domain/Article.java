package com.tchepannou.kiosk.api.domain;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Article {
    public enum Status {
        submitted,
        processed,
        rejected
    }

    @Id
    private String id;

    @ManyToOne
    private Feed feed;

    @ManyToOne
    private Image image;

    private String url;
    private String title;
    private String slug;
    private String countryCode;
    private String languageCode;
    private Date publishedDate;
    private Status status;
    private String statusReason;
    private int contentLength;
    private int rank;

    public Article() {
    }

    public static String generateId(final String url) {
        return url == null
                ? null
                : DigestUtils.md5Hex(url.getBytes());
    }

    public String contentKey(final Status status) {
        return "/articles/" + getId() + "/" + status.name() + ".html";
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

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(final Feed feed) {
        this.feed = feed;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(final String statusReason) {
        this.statusReason = statusReason;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(final Image image) {
        this.image = image;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(final int contentLength) {
        this.contentLength = contentLength;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    @Override
    public boolean equals(final Object obj){
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode (){
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this);
    }

}
