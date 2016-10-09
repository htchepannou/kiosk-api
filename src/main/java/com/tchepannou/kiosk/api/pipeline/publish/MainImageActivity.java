package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.support.ArticleImageSet;
import com.tchepannou.kiosk.api.service.ArticleService;
import com.tchepannou.kiosk.api.service.ImageService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainImageActivity extends Activity {
    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleService articleService;

    @Autowired
    ImageService imageService;

    @Value("${kiosk.image.minWidth}")
    int minWidth;

    @Value("${kiosk.image.minHeight}")
    int minHeidth;

    @Override
    protected String getTopic() {
        return PipelineConstants.TOPIC_ARTICLE_IMAGES_DOWNLOADED;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        final ArticleImageSet articleImageSet = (ArticleImageSet) event.getPayload();
        final Article article = articleImageSet.getArticle();
        Image img = null;
        try {
            img = selectMainImage(articleImageSet);
            article.setImage(img);
            articleRepository.save(article);

            log(article, img, null);
        } catch (final Exception ex) {
            log(article, img, ex);
        }

    }

    private Image selectMainImage(final ArticleImageSet articleImageSet) throws IOException {

        final Article article = articleImageSet.getArticle();
        final String html = articleService.fetchContent(article, Article.Status.submitted);
        final Element elt = Jsoup.parse(html).body().select('#' + article.getContentCssId()).first();

        // Collect the size of all images
        final Map<Image, Integer> distances = new HashMap<>();
        for (final Image img : articleImageSet.getImages()) {
            if (accept(img)) {
                final String filename = filename(img.getUrl());
                final int dist = distance(elt, filename, 0);
                distances.put(img, dist);
            }
        }

        // Sort the images
        if (!distances.isEmpty()) {
            final ArrayList<Image> lst = new ArrayList<>(distances.keySet());
            Collections.sort(lst, (u, v) -> distances.get(u) - distances.get(v));

            // return the 1st
            return lst.get(0);
        }
        return null;
    }

    private boolean accept(final Image img) {
        return img != null && img.getWidth() >= minWidth && img.getHeight() >= minHeidth;
    }

    private void log(final Article article, final Image image, final Throwable ex) {
        addToLog(article);
        addToLog(image);
        log.log(ex);
    }

    private int distance(final Element elt, final String filename, final int dist) {
        if (elt == null) {
            return Integer.MAX_VALUE;
        }

        if (!elt.select("img[src~=" + filename + "]").isEmpty()) {
            return dist;
        }

        return distance(elt.parent(), filename, dist + 1);
    }

    private String filename(final String src) {
        final int i = src.lastIndexOf('/');
        return i > 0 ? src.substring(i + 1) : src;
    }
}
