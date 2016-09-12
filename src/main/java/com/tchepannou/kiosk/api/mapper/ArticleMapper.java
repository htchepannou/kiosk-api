package com.tchepannou.kiosk.api.mapper;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.dto.PublishRequestDto;
import com.tchepannou.kiosk.core.service.TimeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;

public class ArticleMapper {
    @Autowired
    TimeService timeService;

    public Article toArticle(final PublishRequestDto dto) {
        final Article article = new Article();

        article.setCountryCode(dto.getCountryCode());
        article.setFeedId(dto.getFeedId());
        article.setLanguageCode(dto.getLanguageCode());
        article.setSlug(dto.getSlug());
        article.setTitle(dto.getTitle());
        article.setUrl(dto.getUrl());

        final String publishedDate = dto.getPublishedDate();
        if (publishedDate != null) {
            try {
                article.setPublishedDate(timeService.parse(publishedDate));
            } catch (final ParseException e) {
                throw new MappingException(String.format("%s format doesn't match $s", publishedDate, TimeService.DATETIME_FORMAT), e);
            }
        }
        return article;
    }
}
