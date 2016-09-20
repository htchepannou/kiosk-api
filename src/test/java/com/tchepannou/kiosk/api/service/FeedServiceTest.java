package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.client.dto.GetFeedListResponse;
import com.tchepannou.kiosk.api.jpa.FeedRepository;
import com.tchepannou.kiosk.api.mapper.FeedMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.tchepannou.kiosk.api.Fixture.createFeed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FeedServiceTest {

    @Mock
    FeedRepository feedRepository;

    @Mock
    FeedMapper feedMapper;

    @InjectMocks
    FeedService service;

    @Test
    public void testAll() throws Exception {
        // Given
        final List<Feed> feeds = Arrays.asList(createFeed(), createFeed());
        when(feedRepository.findByActive(true)).thenReturn(feeds);

        final GetFeedListResponse response = new GetFeedListResponse();
        when(feedMapper.toFeedListDto(feeds)).thenReturn(response);

        // When
        final GetFeedListResponse result = service.all();

        // Then
        assertThat(result).isEqualTo(response);
    }
}
