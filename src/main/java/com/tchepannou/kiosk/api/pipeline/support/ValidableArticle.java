package com.tchepannou.kiosk.api.pipeline.support;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.validator.Validable;

public class ValidableArticle implements Validable {
    private final Article article;

    public ValidableArticle(final Article article) {
        this.article = article;
    }

    @Override
    public String getId() {
        return article.getId();
    }

    @Override
    public int getContentLength() {
        return article.getContentLength();
    }

    @Override
    public String getTitle() {
        return article.getTitle();
    }

    @Override
    public String getLanguage() {
        return article.getLanguageCode();
    }
}
