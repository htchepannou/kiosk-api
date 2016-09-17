package com.tchepannou.kiosk.api.mapper;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.client.dto.ArticleDataDto;
import com.tchepannou.kiosk.client.dto.PublishRequest;
import com.tchepannou.kiosk.core.service.TimeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;

public class ArticleMapper {
    @Autowired
    TimeService timeService;

    public Article toArticle(final PublishRequest dto) {
        final Article article = new Article();

        article.setFeedId(dto.getFeedId());
        toArticle(dto.getArticle(), article);
        return article;
    }

    public Article toArticle(final ArticleDataDto dto) {
        final Article article = new Article();
        toArticle(dto, article);
        return article;
    }

    private void toArticle(final ArticleDataDto dto, final Article article) {
        article.setCountryCode(dto.getCountryCode());
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
    }
}
