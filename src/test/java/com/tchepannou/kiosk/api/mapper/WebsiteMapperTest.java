package com.tchepannou.kiosk.api.mapper;

import com.tchepannou.kiosk.api.domain.Website;
import com.tchepannou.kiosk.client.dto.WebsiteDto;
import org.junit.Test;

import static com.tchepannou.kiosk.api.Fixture.createWebsite;
import static org.assertj.core.api.Assertions.assertThat;

public class WebsiteMapperTest {
    @Test
    public void shouldMapWebsite (){
        // Give
        Website w = createWebsite();

        // When
        WebsiteDto dto = new WebsiteMapper().toWebsiteDto(w);

        // Then
        assertThat(dto.getArticleUrlPrefix()).isEqualTo(w.getArticleUrlPrefix());
        assertThat(dto.getArticleUrlSuffix()).isEqualTo(w.getArticleUrlSuffix());
        assertThat(dto.getId()).isEqualTo(w.getId());
        assertThat(dto.getName()).isEqualTo(w.getName());
        assertThat(dto.getSlugCssSelector()).isEqualTo(w.getSlugCssSelector());
        assertThat(dto.getTitleCssSelector()).isEqualTo(w.getTitleCssSelector());
        assertThat(dto.getUrl()).isEqualTo(w.getUrl());
    }
}
