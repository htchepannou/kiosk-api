package com.tchepannou.kiosk.api.mapper;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.ranker.Rankable;

public class RankableMapper {
    public Rankable toRankable (final Article article){
        final Rankable rankable = new Rankable();
        rankable.setId(article.getId());
        rankable.setContentLength(article.getContentLength());
        rankable.setPublishedDate(article.getPublishedDate());
        rankable.setWithImage(article.getImage() != null);
        return rankable;
    }
}
