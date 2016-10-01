package com.tchepannou.kiosk.api.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Website {
    @Id
    private long id;
    private String name;
    private String url;
    private String articleUrlPrefix;
    private String articleUrlSuffix;
    private String titleCssSelector;
    private String slugCssSelector;
    private boolean active;

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getArticleUrlPrefix() {
        return articleUrlPrefix;
    }

    public void setArticleUrlPrefix(final String articleUrlPrefix) {
        this.articleUrlPrefix = articleUrlPrefix;
    }

    public String getArticleUrlSuffix() {
        return articleUrlSuffix;
    }

    public void setArticleUrlSuffix(final String articleUrlSuffix) {
        this.articleUrlSuffix = articleUrlSuffix;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public String getTitleCssSelector() {
        return titleCssSelector;
    }

    public void setTitleCssSelector(final String titleCssSelector) {
        this.titleCssSelector = titleCssSelector;
    }

    public String getSlugCssSelector() {
        return slugCssSelector;
    }

    public void setSlugCssSelector(final String slugCssSelector) {
        this.slugCssSelector = slugCssSelector;
    }
}
