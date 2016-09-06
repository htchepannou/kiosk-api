package com.tchepannou.kiosk.api.mapper;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.dto.PublishRequestDto;

public class ArticleMapper {
    public Article toArticle(PublishRequestDto dto){
        final Article article = new Article();
        article.setCountryCode(dto.getCountryCode());
        article.setFeedId(dto.getFeedId());
        article.setLanguageCode(dto.getLanguageCode());
        article.setSlug(dto.getSlug());
        article.setTitle(dto.getTitle());
        article.setUrl(dto.getUrl());
        return article;
    }
}
