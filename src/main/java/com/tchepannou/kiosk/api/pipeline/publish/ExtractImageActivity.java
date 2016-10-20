package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.domain.Website;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.PipelineException;
import com.tchepannou.kiosk.api.service.ArticleService;
import com.tchepannou.kiosk.api.service.ImageService;
import com.tchepannou.kiosk.image.DimensionProvider;
import com.tchepannou.kiosk.image.ImageContext;
import com.tchepannou.kiosk.image.ImageExtractor;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class ExtractImageActivity extends Activity {
    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ArticleService articleService;

    @Autowired
    ImageExtractor extractor;

    @Autowired
    ImageService imageService;

    @Value("${kiosk.image.minWidth}")
    int minWidth;

    @Value("${kiosk.image.minHeight}")
    int minHeidth;

    @Override
    protected String getTopic() {
        return PipelineConstants.TOPIC_ARTICLE_CONTENT_EXTRACTED;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        final Article article = (Article) event.getPayload();

        final String html = articleService.fetchContent(article, Article.Status.submitted);
        final Website website = article.getFeed().getWebsite();

        final ImageContext ctx = createImageContext(website);
        final String url = extractor.extract(html, ctx);

        if (url != null) {
            Image image = imageRepository.findOne(Image.generateId(url));
            if (image == null){
                try {
                    image = imageService.createImage(url);
                } catch (IOException | MimeTypeException ex){
                    throw new PipelineException(ex);
                }
            }

            article.setImage(image);
        }

        log(article);
        publishEvent(new Event(PipelineConstants.TOPIC_ARTICLE_IMAGE_EXTRACTED, article));
    }

    private void log(final Article article) {
        final Image image = article.getImage();
        if (image != null) {
            log.add("Image", image.getUrl());
        }
        addToLog(article);
        log.log();
    }

    private ImageContext createImageContext(final Website website) {
        return new ImageContext() {
            @Override
            public String getBaseUri() {
                return website.getUrl();
            }

            @Override
            public String getImageCssSelector() {
                return website.getImageCssSelector();
            }

            @Override
            public DimensionProvider getDimensionProvider() {
                return imageService;
            }

            @Override
            public int getImageMinWidth() {
                return minWidth;
            }

            @Override
            public int getImageMinHeight() {
                return minHeidth;
            }
        };
    }
}
