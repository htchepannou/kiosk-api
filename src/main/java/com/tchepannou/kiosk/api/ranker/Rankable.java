package com.tchepannou.kiosk.api.ranker;

import java.util.Date;

public class Rankable {
    private String id;
    private Date publishedDate;
    private int contentLength;
    private boolean withImage;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(final Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(final int contentLength) {
        this.contentLength = contentLength;
    }

    public boolean isWithImage() {
        return withImage;
    }

    public void setWithImage(final boolean withImage) {
        this.withImage = withImage;
    }
}
