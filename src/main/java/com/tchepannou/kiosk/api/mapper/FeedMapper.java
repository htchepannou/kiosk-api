package com.tchepannou.kiosk.api.mapper;

import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.client.dto.FeedListResponse;
import com.tchepannou.kiosk.client.dto.FeedDto;
import com.tchepannou.kiosk.core.service.TransactionIdProvider;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FeedMapper {
    @Autowired
    TransactionIdProvider transactionIdProvider;

    public FeedDto toFeedDto(final Feed domain) {
        final FeedDto dto = new FeedDto();
        BeanUtils.copyProperties(domain, dto);

        return dto;
    }

    public FeedListResponse toFeedListDto(final Iterable<Feed> domains) {
        final FeedListResponse dto = new FeedListResponse();
        dto.setTransactionId(transactionIdProvider.get());
        dto.setFeeds(
                StreamSupport.stream(domains.spliterator(), false)
                        .map(domain -> toFeedDto(domain))
                        .collect(Collectors.toList())
        );
        return dto;
    }
}
