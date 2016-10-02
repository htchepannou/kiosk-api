package com.tchepannou.kiosk.api.pipeline.support;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

public class ArticleImageSet {
    private final Article article;
    private final List<Image> images;

    public ArticleImageSet(final Article article, List<Image> images) {
        this.article = article;
        this.images = images;
    }

    public void addImages(List<Image> images){
        this.images.addAll(images);
    }

    public Article getArticle() {
        return article;
    }

    public List<Image> getImages() {
        return images;
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
