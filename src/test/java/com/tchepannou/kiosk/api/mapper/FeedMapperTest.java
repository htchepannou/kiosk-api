package com.tchepannou.kiosk.api.mapper;

import com.tchepannou.kiosk.api.domain.Feed;
import com.tchepannou.kiosk.client.dto.FeedListResponse;
import com.tchepannou.kiosk.client.dto.FeedDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.tchepannou.kiosk.api.Fixture.createFeed;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FeedMapperTest {

    @InjectMocks
    FeedMapper mapper;

    @Test
    public void shouldMapToFeedDto() throws Exception {
        // Give
        final Feed feed = createFeed();

        // When
        FeedDto result = mapper.toFeedDto(feed);

        // Then
        assertEquals(feed, result);
    }

    @Test
    public void shoulMapToFeedListDto() throws Exception {
        // Given
        final List<Feed> feeds = Arrays.asList(
                createFeed(),
                createFeed(),
                createFeed()
        );

        // When
        FeedListResponse result = mapper.toFeedListDto(feeds);

        // Then
        assertThat(result.getSize()).isEqualTo(feeds.size());
        assertEquals(feeds.get(0), result.getFeeds().get(0));
        assertEquals(feeds.get(1), result.getFeeds().get(1));
        assertEquals(feeds.get(2), result.getFeeds().get(2));
    }

    private void assertEquals(final Feed expected, final FeedDto result){
        assertThat(result.getCountryCode()).isEqualTo(expected.getCountryCode());
        assertThat(result.getId()).isEqualTo(expected.getId());
        assertThat(result.getName()).isEqualTo(expected.getName());
        assertThat(result.getType()).isEqualTo(expected.getType());
        assertThat(result.getUrl()).isEqualTo(expected.getUrl());
    }
}
