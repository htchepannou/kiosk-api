package com.tchepannou.kiosk.api.filter;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.core.filter.Filter;

public class ArticleTitleFilter implements Filter<Article> {
    private final int maxLength;

    public ArticleTitleFilter(final int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public Article filter(final Article article) {
        removeWebsiteFromTitle(article);

        if (article.getTitle().length() > maxLength) {
            removeTitlePrefix(article, ':');
        }

        trimTitle(article);
        return article;
    }

    private void removeWebsiteFromTitle(final Article article) {
        final String website = article.getFeed().getWebsite().getName();

        String title = article.getTitle();
        if (title.startsWith(website)){
            title = title.substring(website.length());
        }
        if (title.endsWith(website)){
            title = title.substring(0, title.length() - website.length());
        }
        article.setTitle(title);
    }

    private boolean removeTitlePrefix(final Article article, final char separator) {
        final String title = article.getTitle();
        final int i = title.indexOf(separator);
        if (i > 0) {
            final String prefix = title.substring(0, i).trim();
            final String suffix = title.substring(i + 1).trim();
            if (prefix.length() < suffix.length()) {
                article.setTitle(suffix);
            }
            return true;
        }
        return false;
    }

    private void trimTitle (final Article article){
        String title = article.getTitle().trim();
        if (title.startsWith(":") || title.startsWith("-")){
            title = title.substring(1);
        }
        if (title.endsWith(":") || title.endsWith("-")){
            title = title.substring(0, title.length()-1);
        }
        article.setTitle(title);
    }
}
