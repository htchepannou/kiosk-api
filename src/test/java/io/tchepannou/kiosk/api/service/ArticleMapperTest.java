package io.tchepannou.kiosk.api.service;

import io.tchepannou.kiosk.api.model.ArticleModel;
import io.tchepannou.kiosk.api.persistence.domain.Article;
import io.tchepannou.kiosk.api.persistence.domain.Feed;
import io.tchepannou.kiosk.api.persistence.domain.Image;
import io.tchepannou.kiosk.api.persistence.domain.Link;
import org.junit.Before;
import org.junit.Test;

import static io.tchepannou.kiosk.api.service.Fixtures.createArticle;
import static io.tchepannou.kiosk.api.service.Fixtures.createFeed;
import static io.tchepannou.kiosk.api.service.Fixtures.createImage;
import static io.tchepannou.kiosk.api.service.Fixtures.createLink;
import static org.assertj.core.api.Assertions.assertThat;

public class ArticleMapperTest {
    final static String ASSET_URL = "http://img.com/test";

    ArticleMapper mapper = new ArticleMapper();

    @Before
    public void setUp() {
        mapper.setAssetUrlPrefix(ASSET_URL);
        mapper.setFeedLogoFolder("dev/image");
    }

    @Test
    public void shouldConvertToModel() throws Exception {
        // Given
        final Feed feed = createFeed();
        final Link link = createLink(feed);
        final Article article = createArticle(link);
        final Image thumbnail = createImage(link, Image.TYPE_THUMBNAIL);
        final Image image = createImage(link, Image.TYPE_MAIN);
        final ArticleContainer container = new ArticleContainer();
        container.setArticle(article);
        container.setThumbnail(thumbnail);
        container.setLink(link);
        container.setImage(image);

        // When
        final ArticleModel model = mapper.toArticleModel(container);

        // Then
        assertThat(model.getContentUrl()).isEqualTo(ASSET_URL + "/" + article.getS3Key());
        assertThat(model.getDisplayTitle()).isEqualTo(article.getDisplayTitle());
        assertThat(model.getId()).isEqualTo(article.getId());
        assertThat(model.getPublishedDate()).isEqualTo(article.getPublishedDate());
        assertThat(model.getSummary()).isEqualTo(article.getSummary());
        assertThat(model.getTitle()).isEqualTo(article.getTitle());
        assertThat(model.getUrl()).isEqualTo(link.getUrl());

        assertThat(model.getFeed().getId()).isEqualTo(feed.getId());
        assertThat(model.getFeed().getName()).isEqualTo(feed.getName());
        assertThat(model.getFeed().getLogoUrl()).isEqualTo(ASSET_URL + "/dev/image/" + feed.getLogoUrl());

        assertThat(model.getMainImage().getContentLength()).isEqualTo(image.getContentLength());
        assertThat(model.getMainImage().getContentType()).isEqualTo(image.getContentType());
        assertThat(model.getMainImage().getHeight()).isEqualTo(image.getHeight());
        assertThat(model.getMainImage().getUrl()).isEqualTo(ASSET_URL + "/" + image.getS3Key());
        assertThat(model.getMainImage().getWidth()).isEqualTo(image.getWidth());

        assertThat(model.getThumbnailImage().getContentLength()).isEqualTo(thumbnail.getContentLength());
        assertThat(model.getThumbnailImage().getContentType()).isEqualTo(thumbnail.getContentType());
        assertThat(model.getThumbnailImage().getHeight()).isEqualTo(thumbnail.getHeight());
        assertThat(model.getThumbnailImage().getUrl()).isEqualTo(ASSET_URL + "/" + thumbnail.getS3Key());
        assertThat(model.getThumbnailImage().getWidth()).isEqualTo(thumbnail.getWidth());
    }

    @Test
    public void displayTitleShouldFallbackToTitle() throws Exception {
        // Given
        final Feed feed = createFeed();
        final Link link = createLink(feed);
        final Article article = createArticle(link);
        final Image thumbnail = createImage(link, Image.TYPE_THUMBNAIL);
        final Image image = createImage(link, Image.TYPE_MAIN);
        final ArticleContainer container = new ArticleContainer();
        container.setArticle(article);
        container.setThumbnail(thumbnail);
        container.setLink(link);
        container.setImage(image);

        article.setDisplayTitle(null);

        // When
        final ArticleModel model = mapper.toArticleModel(container);

        // Then
        assertThat(model.getDisplayTitle()).isEqualTo(article.getTitle());
    }
}
