package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.core.service.FileService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import javax.activation.MimetypesFileTypeMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ExtractImagesActivity extends Activity {
    @Autowired
    FileService fileService;

    @Override
    protected String getTopic() {
        return PipelineConstants.TOPIC_ARTICLE_PROCESSED;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        Elements img = null;
        final Article article = (Article) event.getPayload();
        try {
            final String html = fetchContent(article, Article.Status.submitted);

            final Document doc = Jsoup.parse(html);
            img = doc.body().select("img");
            for (final Element elt : img) {
                handleImage(elt);
            }
            log(article, img, null);
        } catch (Exception ex){
            log(article, img, ex);
        }
    }

    private void handleImage(final Element elt){
        final String url = elt.attr("src");
        final String id = Image.generateId(url);
        final Image image = new Image();

        image.setId(id);
        image.setTitle(elt.attr("alt"));
        image.setUrl(url);
        image.setContentType(MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(url));
        publishEvent(new Event(PipelineConstants.TOPIC_IMAGE_SUBMITTED, image));
    }

    private String fetchContent(final Article article, final Article.Status status) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final String key = article.contentKey(status);
        fileService.get(key, out);
        return out.toString();
    }

    private void log(final Article article, final Elements img, final Throwable ex){
        log.add("Title", article.getTitle());
        log.add("Url", article.getUrl());
        log.add("Id", article.getId());
        log.add("ImageCount", img.size());

        if (ex != null) {
            log.add("Success", false);
            log.add("Exception", ex.getClass().getName());
            log.add("ExceptionMessage", ex.getMessage());
            log.log(ex);
        } else {
            log.add("Success", true);
            log.log();
        }
    }
}
