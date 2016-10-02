package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.support.ArticleImageSet;
import com.tchepannou.kiosk.api.service.ImageService;
import com.tchepannou.kiosk.core.service.FileService;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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
    FileService fileService;

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

    private Image selectMainImage(final ArticleImageSet articleImageSet) {

        // Collect the size of all images
        final Map<Image, Integer> sizes = new HashMap<>();
        for (final Image img : articleImageSet.getImages()) {
            if (accept(img)) {
                sizes.put(img, img.getHeight() * img.getWidth());
            }
        }

        // Sort the images
        if (!sizes.isEmpty()) {
            final ArrayList<Image> lst = new ArrayList<>(sizes.keySet());
            Collections.sort(lst, (u, v) -> sizes.get(v) - sizes.get(u));

            // return the 1st
            return lst.get(0);
        }
        return null;
    }

    private boolean accept(final Image img) {
        return img != null && img.getWidth() >= minWidth && img.getHeight() >= minHeidth;
    }

    private String id(final Element elt) {
        final String src = elt.attr("abs:src");
        return Image.generateId(src);
    }

    private void log(final Article article, final Image image, final Throwable ex) {
        addToLog(article);
        addToLog(image);
        log.log(ex);
    }
}
