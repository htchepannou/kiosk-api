package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.api.jpa.FeedRepository;
import com.tchepannou.kiosk.api.mapper.FeedMapper;
import com.tchepannou.kiosk.client.dto.GetFeedListResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class FeedService {
    @Autowired
    FeedRepository feedRepository;

    @Autowired
    FeedMapper feedMapper;

    public GetFeedListResponse all(){
        final Iterable<Feed> feeds = feedRepository.findByActive(true);
        return feedMapper.toFeedListDto(feeds);
    }
}
