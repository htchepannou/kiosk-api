package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Website;
import com.tchepannou.kiosk.api.jpa.WebsiteRepository;
import com.tchepannou.kiosk.api.mapper.WebsiteMapper;
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
}
