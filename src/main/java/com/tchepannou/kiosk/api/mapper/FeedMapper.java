package com.tchepannou.kiosk.api.mapper;

import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.api.dto.FeedListResponseDto;
import com.tchepannou.kiosk.api.dto.FeedDto;
import org.springframework.beans.BeanUtils;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FeedMapper {
    public FeedDto toFeedDto(final Feed domain) {
        final FeedDto dto = new FeedDto();
        BeanUtils.copyProperties(domain, dto);

        return dto;
    }

    public FeedListResponseDto toFeedListDto(final Iterable<Feed> domains) {
        final FeedListResponseDto dto = new FeedListResponseDto();
        dto.setFeeds(
                StreamSupport.stream(domains.spliterator(), false)
                        .map(domain -> toFeedDto(domain))
                        .collect(Collectors.toList())
        );
        return dto;
    }
}
