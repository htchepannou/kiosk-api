package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.core.service.FileService;
import com.tchepannou.kiosk.core.service.HttpService;
import com.tchepannou.kiosk.core.service.LogService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    @Autowired
    PlatformTransactionManager txManager;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    FileService fileService;

    @Autowired
    HttpService httpService;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    LogService logService;

    public void process(final Article article) throws IOException {
        final String html = fetchContent(article, Article.Status.submitted);
        final Document doc = Jsoup.parse(html);
        int i = 0;
        for (final Element elt : doc.body().select("img")) {
            final String url = elt.attr("src");
            final String id = Image.generateId(url);
            LOGGER.info("processing " + url);
            if (imageRepository.findOne(id) != null) {
                // Image already exist
                continue;
            }

            Image image = null;
            Exception ex = null;
            try {
                image = grabImage(++i, elt);
                if (image != null){
                    save(image);
                }
            } catch (final Exception e) {
                ex = e;
            } finally {
                log(elt, image, ex);
            }
        }
    }

    private void log(final Element elt, final Image image, final Exception ex) {
        logService.add("ImageUrl", elt.attr("src"));
        if (image != null){
            logService.add("ImageId", image.getId());
            logService.add("ImageKey", image.getKey());
            logService.add("ImageWidth", image.getWidth());
            logService.add("ImageHeight", image.getHeight());
            logService.add("ImageContentType", image.getContentType());
        }

        if (ex != null){
            logService.add("Success", false);
            logService.add("Exception", ex.getClass().getName());
            logService.add("ExceptionMessage", ex.getMessage());
        } else {
            logService.add("Success", true);
        }
    }

    private void save (final Image image){
        TransactionStatus tx = txManager.getTransaction(new DefaultTransactionDefinition());
        try {
            imageRepository.save(image);
            txManager.commit(tx);
        } catch (RuntimeException ex){
            txManager.rollback(tx);
            throw ex;
        }
    }

    private Image grabImage(final int index, final Element elt) throws IOException {
        final String url = elt.attr("src");
        final Image image = new Image();
        image.setId(Image.generateId(url));

        final String keyPrefix = "";
        final String key = httpService.get(url, keyPrefix, fileService);
        final FileTypeMap typeMap = MimetypesFileTypeMap.getDefaultFileTypeMap();
        final String contentType = typeMap.getContentType(key);

        if (contentType != null && contentType.startsWith("image/")) {
            image.setTitle(elt.attr("alt"));
            image.setUrl(url);
            image.setKey(key);
            image.setContentType(MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(key));
            getDimension(image);
            return image;
        }

        return null;
    }

    private void getDimension(final Image image) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        fileService.get(image.getKey(), out);

        BufferedImage bimg = ImageIO.read(new ByteArrayInputStream(out.toByteArray()));
        image.setWidth(bimg.getWidth());
        image.setHeight(bimg.getHeight());
    }

    private String fetchContent(final Article article, final Article.Status status) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final String key = article.contentKey(status);
        fileService.get(key, out);
        return out.toString();
    }

}
