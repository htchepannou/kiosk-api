package io.tchepannou.kiosk.api.service;

import com.google.common.base.Strings;
import io.tchepannou.kiosk.api.model.ArticleModel;
import io.tchepannou.kiosk.api.model.FeedModel;
import io.tchepannou.kiosk.api.model.ImageModel;
import io.tchepannou.kiosk.api.persistence.domain.Feed;
import io.tchepannou.kiosk.api.persistence.domain.Link;

public class ArticleMapper {
    private String assetUrlPrefix;
    private String feedLogoFolder;

    public ArticleModel toArticleModel(final Link article){
        final ArticleModel model = new ArticleModel();

        model.setDisplayTitle(article.getDisplayTitle());
        model.setContentUrl(assetUrl(article.getS3Key()));
        model.setId(article.getId());
        model.setPublishedDate(article.getPublishedDate());
        model.setSummary(article.getSummary());
        model.setTitle(article.getTitle());
        model.setUrl(article.getUrl());

        if (Strings.isNullOrEmpty(model.getDisplayTitle())){
            model.setDisplayTitle(article.getTitle());
        }

        return model;
    }

    public ImageModel toImageModel (final Link image){
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

    public FeedModel toFeed (final Feed feed){
        if (feed == null){
            return null;
        }

        FeedModel model = new FeedModel();
        model.setId(feed.getId());
        model.setName(feed.getName());
        model.setLogoUrl(feedLogoUrl(feed.getLogoUrl()));

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
