package com.tchepannou.kiosk.api.filter;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Website;
import com.tchepannou.kiosk.core.filter.Filter;

import java.util.Locale;

public class ArticleTitleSuffixFilter implements Filter<Article> {
    @Override
    public Article filter(final Article article) {
        removeWebsiteName(article);
        return article;
    }

    /**
     * This is for CameroonOnline.org
     */
    private void removeWebsiteName(final Article article) {
        final Website website = article.getFeed().getWebsite();
        final String name = website.getName();

        final String suffix = "- " + name;
        final String title = article.getTitle();
        if (title.endsWith(suffix)) {
            final String xtitle = title.substring(0, title.length() - suffix.length());
            article.setTitle(xtitle);
        }
    }



}
