package com.tchepannou.kiosk.api.mapper;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.ranker.Rankable;
import org.junit.Test;

import static com.tchepannou.kiosk.api.Fixture.createArticle;
import static com.tchepannou.kiosk.api.Fixture.createImage;
import static org.assertj.core.api.Assertions.assertThat;

public class RankableMapperTest {

    @Test
    public void testToRankableWithNoImage() throws Exception {
        final Article article = createArticle();
        article.setImage(null);

        final Rankable result = new RankableMapper().toRankable(article);

        assertThat(result.getContentLength()).isEqualTo(article.getContentLength());
        assertThat(result.getPublishedDate()).isEqualTo(article.getPublishedDate());
        assertThat(result.getId()).isEqualTo(article.getId());
        assertThat(result.isWithImage()).isFalse();
    }

    @Test
    public void testToRankableWithImage() throws Exception {
        final Article article = createArticle();
        article.setImage(createImage());

        final Rankable result = new RankableMapper().toRankable(article);

        assertThat(result.getContentLength()).isEqualTo(article.getContentLength());
        assertThat(result.getPublishedDate()).isEqualTo(article.getPublishedDate());
        assertThat(result.getId()).isEqualTo(article.getId());
        assertThat(result.isWithImage()).isTrue();
    }
}
