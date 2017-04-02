package io.tchepannou.kiosk.api.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.tchepannou.kiosk.api.model.ArticleModel;
import io.tchepannou.kiosk.api.model.ArticleModelList;
import io.tchepannou.kiosk.api.model.ImageModel;
import io.tchepannou.kiosk.api.persistence.domain.Asset;
import io.tchepannou.kiosk.api.persistence.domain.AssetTypeEnum;
import io.tchepannou.kiosk.api.persistence.domain.Link;
import io.tchepannou.kiosk.api.persistence.domain.LinkStatusEnum;
import io.tchepannou.kiosk.api.persistence.domain.LinkTypeEnum;
import io.tchepannou.kiosk.api.persistence.repository.AssetRepository;
import io.tchepannou.kiosk.api.persistence.repository.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.List;

public class ArticleService {
    @Autowired
    LinkRepository linkRepository;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    ArticleMapper mapper;

    public ArticleModelList list(final int page, final int limit) {

        // Load the articles
        final PageRequest pagination = new PageRequest(page, limit, Sort.Direction.DESC, "publishedDate");
        final List<Link> articles = linkRepository.findByTypeAndStatus(LinkTypeEnum.article, LinkStatusEnum.published, pagination);
        final List<Asset> assets = assetRepository.findByLinkIn(articles);
        final Multimap<Link, Asset> assetMap = indexByLink(assets);

        final ArticleModelList result = new ArticleModelList();
        for (final Link article : articles){
            final Collection<Asset> articleAssets = assetMap.get(article);
            final ArticleModel model = toArticleModel(article, articleAssets);
            result.add(model);
        }
        return result;
    }

    private Multimap<Link, Asset> indexByLink(final List<Asset> assets){
        Multimap<Link, Asset> map = ArrayListMultimap.create();
        for (final Asset asset : assets){
            map.put(asset.getLink(), asset);
        }
        return map;
    }

    private ArticleModel toArticleModel(final Link article, final Collection<Asset> assets){
        final ArticleModel model = mapper.toArticleModel(article);
        model.setFeed(mapper.toFeed(article.getFeed()));
        for (final Asset asset : assets){
            final AssetTypeEnum type = asset.getType();
            if (AssetTypeEnum.thumbnail.equals(type)){
                final ImageModel thumbnail = mapper.toImageModel(asset.getTarget());
                model.setThumbnailImage(thumbnail);
            }
        }
        return model;
    }
}
