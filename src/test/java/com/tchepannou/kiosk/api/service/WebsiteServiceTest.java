package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.jpa.WebsiteRepository;
import com.tchepannou.kiosk.api.mapper.WebsiteMapper;
import com.tchepannou.kiosk.client.dto.GetWebsiteListResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static com.tchepannou.kiosk.api.Fixture.createWebsite;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebsiteServiceTest {
    @Mock
    WebsiteRepository websiteRepository;

    @Mock
    WebsiteMapper websiteMapper;

    @InjectMocks
    WebsiteService service;

    @Test
    public void shouldReturnActiveWebsites() throws Exception {
        // Given
        when(websiteRepository.findByActive(true)).thenReturn(Arrays.asList(
                createWebsite(),
                createWebsite(),
                createWebsite()
        ));

        final GetWebsiteListResponse response = mock(GetWebsiteListResponse.class);
        when(websiteMapper.toGetWebsiteListResponse(any())).thenReturn(response);

        // When
        final GetWebsiteListResponse result = service.all();

        // Then
        assertThat(result).isEqualTo(response);
    }
}
