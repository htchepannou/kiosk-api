package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.service.ImageService;
import com.tchepannou.kiosk.core.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ExtractImagesActivity extends Activity {
    @Autowired
    FileService fileService;

    @Autowired
    ImageService imageService;

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

            images = imageService.extractImages(html, baseUrl);
            for (final Image img : images){
                publishEvent(new Event(PipelineConstants.TOPIC_IMAGE_SUBMITTED, img));
            }
            publishEvent(new Event(PipelineConstants.TOPIC_ARTICLE_IMAGES_DOWNLOADED, article));

            log(article, images, null);

        } catch (Exception ex){

            log(article, images, ex);

        }
    }

    private String fetchContent(final Article article, final Article.Status status) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final String key = article.contentKey(status);
        fileService.get(key, out);
        return out.toString();
    }

    private void log(final Article article, final List<Image> images, final Throwable ex){
        log.add("ImageCount", images.size());
        addToLog(article);
        addToLog(ex);
        log.log(ex);
    }
}
