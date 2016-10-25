package com.tchepannou.kiosk.api.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Website {
    @Id
    private long id;

    @Column(length = 100)
    private String name;

    @Column(columnDefinition = "text")
    private String url;

    private String articleUrlPrefix;
    private String articleUrlSuffix;
    private String titleCssSelector;
    private String slugCssSelector;
    private String imageCssSelector;
    private String titleSanitizeRegex;
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

    public String getImageCssSelector() {
        return imageCssSelector;
    }

    public void setImageCssSelector(final String imageCssSelector) {
        this.imageCssSelector = imageCssSelector;
    }

    public String getTitleSanitizeRegex() {
        return titleSanitizeRegex;
    }

    public void setTitleSanitizeRegex(final String titleSanitizeRegex) {
        this.titleSanitizeRegex = titleSanitizeRegex;
    }

    @Override
    public boolean equals(final Object obj){
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode (){
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this);
    }

}
