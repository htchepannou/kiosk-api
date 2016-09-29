package com.tchepannou.kiosk.api.filter;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.core.filter.Filter;

import java.util.Locale;

public class ArticleTitlePrefixFilter implements Filter<Article> {
    @Override
    public Article filter(final Article article) {
        final Feed feed = article.getFeed();
        final Locale locale = new Locale("fr", feed.getCountryCode());

        String title = article.getTitle();
        title = removePrefix(title, locale.getDisplayCountry());
        title = removePrefix(title, locale.getDisplayCountry(Locale.ENGLISH));
        article.setTitle(title);

        return article;
    }

    private String removePrefix(final String title, final String country) {
        if (!title.startsWith(country)) {
            return title;
        }
        final String xtitle = title.substring(country.length()).trim();
        final char ch = xtitle.charAt(0);
        return ch == ':' || ch == '-' ? xtitle.substring(1) : xtitle;
    }
}
