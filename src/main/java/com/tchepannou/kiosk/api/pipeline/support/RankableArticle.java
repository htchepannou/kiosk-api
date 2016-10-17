package com.tchepannou.kiosk.api.pipeline.support;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.ranker.Rankable;

import java.util.Date;

public class RankableArticle implements Rankable {
    private final Article article;

    public RankableArticle(final Article article) {
        this.article = article;
    }

    public Article getArticle() {
        return article;
    }

    @Override
    public Date getPublishedDate() {
        return article.getPublishedDate();
    }

    @Override
    public int getContentLength() {
        return article.getContentLength();
    }

    @Override
    public boolean isWithImage() {
        return article.getImage() != null;
    }
}
