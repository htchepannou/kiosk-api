package io.tchepannou.kiosk.api.service;

import io.tchepannou.kiosk.api.model.ArticleModelList;
import io.tchepannou.kiosk.api.persistence.domain.Article;
import io.tchepannou.kiosk.api.persistence.domain.Image;
import io.tchepannou.kiosk.api.persistence.domain.Link;
import io.tchepannou.kiosk.api.persistence.domain.Video;
import io.tchepannou.kiosk.api.persistence.repository.ArticleRepository;
import io.tchepannou.kiosk.api.persistence.repository.ImageRepository;
import io.tchepannou.kiosk.api.persistence.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArticleService {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    ArticleMapper mapper;

    public ArticleModelList list(final int page, final int limit) {

        // Load the articles
        final PageRequest pagination = new PageRequest(page, limit, Sort.Direction.DESC, "publishedDate");
        final List<Article> articles = articleRepository.findByStatus(Article.STATUS_VALID, pagination);
        final List<ArticleContainer> containers = toArticleContainer(articles);

        // Collect the links
        final Map<Link, ArticleContainer> containerByLink = indexByLink(containers);
        final Collection<Link> links = containerByLink.keySet();

        // Collect the images
        final List<Image> images = imageRepository.findByLinkIn(links);
        assignImages(containerByLink, images);

        // Collect videos
        final List<Video> videos = videoRepository.findByLinkIn(links);
        assignVideos(containerByLink, videos);

        return mapper.toArticleListModel(containers);
    }

    private List<ArticleContainer> toArticleContainer(final List<Article> articles) {
        return articles.stream()
                .map(a -> {
                    ArticleContainer c = new ArticleContainer();
                    c.setArticle(a);
                    c.setLink(a.getLink());

                    return c;
                })
                .collect(Collectors.toList());
    }

    private Map<Link, ArticleContainer> indexByLink(final List<ArticleContainer> containers) {
        final Map<Link, ArticleContainer> map = new HashMap<>();
        for (final ArticleContainer container : containers) {
            map.put(container.getLink(), container);
        }
        return map;
    }

    private void assignImages(final Map<Link, ArticleContainer> containerMap, final List<Image> images){
        for (final Image image : images) {
            ArticleContainer container = containerMap.get(image.getLink());
            if (container != null){
                final int type = image.getType();
                if (type == Image.TYPE_MAIN){
                    container.setImage(image);
                } else if (type == Image.TYPE_THUMBNAIL){
                    container.setThumbnail(image);
                }
            }
        }

    }


    private void assignVideos(final Map<Link, ArticleContainer> containerMap, final List<Video> videos){
        for (final Video video : videos) {
            ArticleContainer container = containerMap.get(video.getLink());
            if (container != null){
                container.addVideo(video);
            }
        }

    }
}
