package io.tchepannou.kiosk.api.service;

import io.tchepannou.kiosk.api.model.ArticleModel;
import io.tchepannou.kiosk.api.model.ArticleModelList;
import io.tchepannou.kiosk.api.model.FeedModel;
import io.tchepannou.kiosk.api.model.ImageModel;
import io.tchepannou.kiosk.api.persistence.domain.Article;
import io.tchepannou.kiosk.api.persistence.domain.Feed;
import io.tchepannou.kiosk.api.persistence.domain.Image;

public class ArticleMapper {
    private String assetUrlPrefix;
    private String feedLogoFolder;

    public ArticleModel toArticleModel(final ArticleContainer container){
        final ArticleModel model = new ArticleModel();

        mapArticle(container.getArticle(), model);
        mapMainImage(container.getImage(), model);
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

    public String getFeedLogoFolder() {
        return feedLogoFolder;
    }

    public void setFeedLogoFolder(final String feedLogoFolder) {
        this.feedLogoFolder = feedLogoFolder;
    }

    //-- Private
    private void mapArticle(final Article article, ArticleModel model){
        model.setDisplayTitle(article.getDisplayTitle());
        model.setContentUrl(assetUrl(article.getS3Key()));
        model.setId(article.getId());
        model.setPublishedDate(article.getPublishedDate());
        model.setSummary(article.getSummary());
        model.setTitle(article.getTitle());
        model.setUrl(article.getLink().getUrl());
    }

    private void mapFeed (final Feed feed, final ArticleModel model){
        if (feed == null){
            return ;
        }

        FeedModel feedModel = new FeedModel();
        feedModel.setId(feed.getId());
        feedModel.setName(feed.getName());
        feedModel.setLogoUrl(feedLogoUrl(feed.getLogoUrl()));

        model.setFeed(feedModel);
    }

    private ImageModel toImageModel (final Image image){
        if (image == null){
            return null;
        }
        ImageModel model = new ImageModel();
        model.setContentLength(image.getContentLength());
        model.setContentType(image.getContentType());
        model.setWidth(image.getWidth());
        model.setHeight(image.getHeight());
        model.setUrl(assetUrl(image.getS3Key()));

        return model;
    }

    private void mapMainImage (final Image image, ArticleModel model){
        model.setMainImage(toImageModel(image));
    }

    private void mapThumbmail (final Image image, ArticleModel model){
        model.setThumbnailImage(toImageModel(image));
    }

    private String feedLogoUrl(final String url){
        if (url == null){
            return null;
        }

        return assetUrl(String.format("%s/%s", feedLogoFolder, url));
    }

    private String assetUrl(final String url){
        return url != null ? String.format("%s/%s", assetUrlPrefix, url) : null;
    }


}
