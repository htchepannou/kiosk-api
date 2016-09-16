package com.tchepannou.kiosk.client.dto;

public class PublishRequest {
    private long feedId;
    private ArticleDto article;

    public long getFeedId() {
        return feedId;
    }

    public void setFeedId(final long feedId) {
        this.feedId = feedId;
    }

    public ArticleDto getArticle() {
        return article;
    }

    public void setArticle(final ArticleDto article) {
        this.article = article;
    }
}
