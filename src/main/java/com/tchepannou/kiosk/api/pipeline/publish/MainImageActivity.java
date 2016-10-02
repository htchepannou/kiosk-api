package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.service.ImageService;
import com.tchepannou.kiosk.core.service.FileService;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayOutputStream;
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
        final Article article = (Article) event.getPayload();
        Image img = null;
        try {
            final String html = fetchContent(article, Article.Status.submitted);

            img = selectMainImage(html, article);
            article.setImage(img);
            articleRepository.save(article);

            log(article, img, null);
        } catch (final Exception ex) {
            log(article, img, ex);
        }

    }

    private void log(final Article article, final Image image, final Throwable ex) {
        addToLog(article);
        addToLog(image);
        log.log(ex);
    }

    private Image selectMainImage(final String html, final Article article) {
        final String baseUrl = article.getFeed().getWebsite().getUrl();
        final Elements elts = imageService.extractImageTags(html, baseUrl);
        if (elts.isEmpty()) {
            return null;
        }

        // Collect the size of all images
        final Map<Image, Integer> sizes = new HashMap<>();
        for (final Element elt : elts) {
            final String id = id(elt);
            final Image img = imageRepository.findOne(id);
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

    private String fetchContent(final Article article, final Article.Status status) throws IOException {

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final String key = article.contentKey(status);
        fileService.get(key, out);
        return out.toString();
    }
}
