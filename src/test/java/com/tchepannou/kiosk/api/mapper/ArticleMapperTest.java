package com.tchepannou.kiosk.api.mapper;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.dto.PublishRequestDto;
import com.tchepannou.kiosk.core.service.TimeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.Date;

import static com.tchepannou.kiosk.api.Fixture.createPublishRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArticleMapperTest {
    @Mock
    private TimeService timeService;

    @InjectMocks
    private ArticleMapper mapper;

    @Test
    public void shouldConvertArticle () throws Exception{
        // Given
        final PublishRequestDto request = createPublishRequest();

        final Date date = new Date();
        when(timeService.parse(anyString())).thenReturn(date);

        // When
        Article article = mapper.toArticle(request);

        // Then
        assertThat(article.getCountryCode()).isEqualTo(request.getCountryCode());
        assertThat(article.getFeedId()).isEqualTo(request.getFeedId());
        assertThat(article.getLanguageCode()).isEqualTo(request.getLanguageCode());
        assertThat(article.getPublishedDate()).isEqualTo(date);
        assertThat(article.getSlug()).isEqualTo(request.getSlug());
        assertThat(article.getStatus()).isNull();
        assertThat(article.getTitle()).isEqualTo(request.getTitle());
        assertThat(article.getUrl()).isEqualTo(request.getUrl());
    }

    @Test
    public void shouldNotConvertPublishedDateWhenNull () throws Exception{
        // Given
        final PublishRequestDto request = createPublishRequest();
        request.setPublishedDate(null);

        // When
        Article article = mapper.toArticle(request);

        // Then
        assertThat(article.getPublishedDate()).isNull();
    }

    @Test(expected = MappingException.class)
    public void shouldThrowMappingExceptionIfDateMalformed () throws Exception{
        // Given
        final PublishRequestDto request = createPublishRequest();

        when(timeService.parse(anyString())).thenThrow(new ParseException("foo", 1));

        // When
        mapper.toArticle(request);

        // Then
    }
}