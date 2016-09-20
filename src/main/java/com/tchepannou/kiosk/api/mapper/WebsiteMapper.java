package com.tchepannou.kiosk.api.mapper;

import com.tchepannou.kiosk.api.domain.Website;
import com.tchepannou.kiosk.client.dto.WebsiteDto;

public class WebsiteMapper {
    public static WebsiteDto toWebsiteDto (Website domain){
        final WebsiteDto dto = new WebsiteDto();
        dto.setArticleUrlPrefix(domain.getArticleUrlPrefix());
        dto.setArticleUrlSuffix(domain.getArticleUrlSuffix());
        dto.setId(domain.getId());
        dto.setName(domain.getName());
        dto.setSlugCssSelector(domain.getSlugCssSelector());
        dto.setTitleCssSelector(domain.getTitleCssSelector());
        dto.setUrl(domain.getUrl());
        return dto;
    }
}
