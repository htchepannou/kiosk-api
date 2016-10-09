package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.support.ArticleImageSet;
import com.tchepannou.kiosk.api.service.ArticleService;
import com.tchepannou.kiosk.api.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExtractImagesActivity extends Activity {
    @Autowired
    ImageService imageService;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ArticleService articleService;

    @Override
    protected String getTopic() {
        return PipelineConstants.TOPIC_ARTICLE_PROCESSED;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        final Article article = (Article) event.getPayload();
        List<Image> images = Collections.emptyList();
        try {

            final String html = articleService.fetchContent(article, Article.Status.submitted);
            final String baseUrl = article.getFeed().getWebsite().getUrl();

            /* extract images */
            images = imageService.extractImages(html, baseUrl);

            /* merge with DB */
            final List<Image> merged = new ArrayList<>();
            for (final Image img : images) {
                final Image ximg = imageRepository.findOne(img.getId());
                merged.add(ximg == null ? img : ximg);
            }

            /* publish */
            for (final Image img : merged) {
                publishEvent(new Event(PipelineConstants.TOPIC_IMAGE_SUBMITTED, img));
            }
            publishEvent(new Event(PipelineConstants.TOPIC_ARTICLE_IMAGES_DOWNLOADED, new ArticleImageSet(article, merged)));

            log(article, images, null);

        } catch (final Exception ex) {

            log(article, images, ex);

        }
    }

    private void log(final Article article, final List<Image> images, final Throwable ex) {
        log.add("ImageCount", images.size());
        addToLog(article);
        addToLog(ex);
        log.log(ex);
    }
}
