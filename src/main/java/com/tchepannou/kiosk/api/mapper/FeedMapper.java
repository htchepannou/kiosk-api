package com.tchepannou.kiosk.api.mapper;

import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.client.dto.GetFeedListResponse;
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

    public GetFeedListResponse toFeedListDto(final Iterable<Feed> domains) {
        final GetFeedListResponse dto = new GetFeedListResponse();
        dto.setTransactionId(transactionIdProvider.get());
        dto.setFeeds(
                StreamSupport.stream(domains.spliterator(), false)
                        .map(domain -> toFeedDto(domain))
                        .collect(Collectors.toList())
        );
        return dto;
    }
}
