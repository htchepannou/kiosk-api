package com.tchepannou.kiosk.api.mapper;

import com.tchepannou.kiosk.api.domain.Website;
import com.tchepannou.kiosk.client.dto.GetWebsiteListResponse;
import com.tchepannou.kiosk.client.dto.WebsiteDto;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class WebsiteMapper {
    public WebsiteDto toWebsiteDto (Website domain){
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

    public GetWebsiteListResponse toGetWebsiteListResponse(Iterable<Website> websites) {
        final GetWebsiteListResponse response = new GetWebsiteListResponse();
        response.setWebsites(
                StreamSupport.stream(websites.spliterator(), false)
                    .map(w -> toWebsiteDto(w))
                    .collect(Collectors.toList())
        );
        return response;
    }
}
