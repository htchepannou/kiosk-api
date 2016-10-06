package com.tchepannou.kiosk.api.filter;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.core.filter.Filter;
import org.apache.tika.language.LanguageIdentifier;

public class ArticleLanguageFilter implements Filter<Article> {

    @Override
    public Article filter(final Article article) {
        final String text = article.getTitle() + "\n" + article.getSlug();
        final String language = new LanguageIdentifier(text).getLanguage();
        article.setLanguageCode(language);
        return article;
    }
}
