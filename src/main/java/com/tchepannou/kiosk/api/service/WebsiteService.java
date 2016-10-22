package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Website;
import com.tchepannou.kiosk.api.jpa.WebsiteRepository;
import com.tchepannou.kiosk.api.mapper.WebsiteMapper;
import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.client.dto.ErrorDto;
import com.tchepannou.kiosk.client.dto.GetWebsiteResponse;
import com.tchepannou.kiosk.client.dto.GetWebsiteListResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class WebsiteService {
    @Autowired
    WebsiteRepository websiteRepository;

    @Autowired
    WebsiteMapper websiteMapper;

    public GetWebsiteListResponse all(){
        final Iterable<Website> websites = websiteRepository.findByActive(true);
        return websiteMapper.toGetWebsiteListResponse(websites);
    }

    public GetWebsiteResponse get(final long id){
        GetWebsiteResponse response = new GetWebsiteResponse();
        final Website website = websiteRepository.findOne(id);
        if (website == null){
            response.setError(new ErrorDto(ErrorConstants.WEBSITE_NOT_FOUND));
        } else {
            response.setWebsite(websiteMapper.toWebsiteDto(website));
        }
        return response;
    }
}
