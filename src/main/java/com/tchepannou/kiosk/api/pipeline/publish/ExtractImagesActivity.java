package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.support.ArticleImageSet;
import com.tchepannou.kiosk.api.service.ImageService;
import com.tchepannou.kiosk.core.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExtractImagesActivity extends Activity {
    @Autowired
    FileService fileService;

    @Autowired
    ImageService imageService;

    @Autowired
    ImageRepository imageRepository;

    @Override
    protected String getTopic() {
        return PipelineConstants.TOPIC_ARTICLE_PROCESSED;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        final Article article = (Article) event.getPayload();
        List<Image> images = Collections.emptyList();
        try {

            final String html = fetchContent(article, Article.Status.submitted);
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

    private String fetchContent(final Article article, final Article.Status status) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final String key = article.contentKey(status);
        fileService.get(key, out);
        return out.toString();
    }

    private void log(final Article article, final List<Image> images, final Throwable ex) {
        log.add("ImageCount", images.size());
        addToLog(article);
        addToLog(ex);
        log.log(ex);
    }
}
