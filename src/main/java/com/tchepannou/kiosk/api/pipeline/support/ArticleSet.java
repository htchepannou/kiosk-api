package com.tchepannou.kiosk.api.pipeline.support;

import com.tchepannou.kiosk.api.domain.Article;

import java.util.List;

public class ArticleSet {
    private List<Article> articles;

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(final List<Article> articles) {
        this.articles = articles;
    }
}
