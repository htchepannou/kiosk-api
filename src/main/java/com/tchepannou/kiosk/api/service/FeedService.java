package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.api.dto.FeedAllResponseDto;
import com.tchepannou.kiosk.api.jpa.FeedRepository;
import com.tchepannou.kiosk.api.mapper.FeedMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class FeedService {
    @Autowired
    FeedRepository feedRepository;

    @Autowired
    FeedMapper feedMapper;

    public FeedAllResponseDto all(){
        final Iterable<Feed> feeds = feedRepository.findAll();
        return feedMapper.toFeedListDto(feeds);
    }
}
