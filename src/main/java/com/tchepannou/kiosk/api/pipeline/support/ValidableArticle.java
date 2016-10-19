package com.tchepannou.kiosk.api.pipeline.support;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.validator.Validable;

public class ValidableArticle implements Validable {
    private final Article article;

    public ValidableArticle(final Article article) {
        this.article = article;
    }

    @Override
    public int getContentLength() {
        return article.getContentLength();
    }
}
