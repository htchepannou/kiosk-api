package io.tchepannou.kiosk.api.service;

import io.tchepannou.kiosk.api.model.ArticleModelList;
import io.tchepannou.kiosk.api.model.ArticleModel;
import io.tchepannou.kiosk.api.model.FeedModel;
import io.tchepannou.kiosk.api.persistence.domain.Article;
import io.tchepannou.kiosk.api.persistence.domain.Feed;
import io.tchepannou.kiosk.api.persistence.domain.Image;

public class ArticleMapper {
    private String assetUrlPrefix;

    public ArticleModel toArticleModel(final ArticleContainer container){
        final ArticleModel model = new ArticleModel();

        mapArticle(container.getArticle(), model);
        mapImage(container.getImage(), model);
        mapThumbmail(container.getThumbnail(), model);
        mapFeed(container.getFeed(), model);
        return model;
    }

    public ArticleModelList toArticleListModel (final Iterable<ArticleContainer> containers){
        final ArticleModelList model = new ArticleModelList();
        for (ArticleContainer container : containers){
            model.add(toArticleModel(container));
        }
        return model;
    }

    //-- Getter/Setter
    public String getAssetUrlPrefix() {
        return assetUrlPrefix;
    }

    public void setAssetUrlPrefix(final String assetUrlPrefix) {
        this.assetUrlPrefix = assetUrlPrefix;
    }

    //-- Private
    private void mapArticle(final Article article, ArticleModel model){
        model.setDisplayTitle(article.getDisplayTitle());
        model.setContentUrl(assetUrl(article.getS3Key()));
        model.setId(article.getId());
        model.setPublishedDate(article.getPublishedDate());
        model.setSummary(article.getSummary());
        model.setTitle(article.getTitle());
    }

    private void mapFeed (final Feed feed, final ArticleModel model){
        if (feed == null){
            return ;
        }

        FeedModel feedModel = new FeedModel();
        feedModel.setId(feed.getId());
        feedModel.setName(feed.getName());
        feedModel.setLogoUrl(assetUrl(feed.getLogoUrl()));

        model.setFeed(feedModel);
    }

    private void mapImage (final Image image, ArticleModel model){
        if (image == null){
            return;
        }
        model.setImageUrl(assetUrl(image.getS3Key()));
    }

    private void mapThumbmail (final Image image, ArticleModel model){
        if (image == null){
            return;
        }
        model.setThumbnailUrl(assetUrl(image.getS3Key()));
    }

    private String assetUrl(final String url){
        return url != null ? String.format("%s/%s", assetUrlPrefix, url) : null;
    }
}
