package io.tchepannou.kiosk.api.model;

import java.util.ArrayList;
import java.util.List;

public class ArticleModelList {
    private List<ArticleModel> articles;

    public void add (ArticleModel article){
        if (articles == null){
            articles = new ArrayList<>();
        }
        articles.add(article);
    }
    public int getArticleCount(){
        return articles != null ? articles.size() : 0;
    }

    public List<ArticleModel> getArticles() {
        return articles;
    }

    public void setArticles(final List<ArticleModel> articles) {
        this.articles = articles;
    }
}
