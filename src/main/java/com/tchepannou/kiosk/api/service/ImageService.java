package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.pipeline.PipelineException;
import com.tchepannou.kiosk.core.service.FileService;
import com.tchepannou.kiosk.image.Dimension;
import com.tchepannou.kiosk.image.DimensionProvider;
import com.tchepannou.kiosk.image.support.ImageData;
import com.tchepannou.kiosk.image.support.ImageGrabber;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ImageService implements DimensionProvider {
    @Autowired
    ImageRepository imageRepository;

    @Autowired
    FileService fileService;

    @Autowired
    ImageGrabber grabber;

    @Value("${kiosk.image.public.baseUri}")
    String publicBaseUrl;

    public Image createImage(final String url) throws IOException, MimeTypeException {
        final ImageData data = grabber.grab(url);
        final String id = Image.generateId(url);
        final Image image = toImage(id, url, data);
        store(image, data);

        return image;
    }

    @Override
    public Dimension getDimension(final String url) {
        try {

            final String id = Image.generateId(url);
            Image image = imageRepository.findOne(id);
            if (image == null) {
                image = createImage(url);
            }

            return image;

        } catch (IOException | MimeTypeException ex) {
            throw new PipelineException(ex);
        }
    }

    private void store(final Image image, final ImageData data) throws IOException {
        fileService.put(image.getKey(), new ByteArrayInputStream(data.getContent()));

        imageRepository.save(image);
    }

    private Image toImage(
            final String id,
            final String url,
            final ImageData data
    ) throws IOException, MimeTypeException {

        final String key = key(id, data);
        final Image img = new Image();
        img.setId(id);
        img.setContentType(data.getContentType());
        img.setKey(key);
        img.setUrl(url);
        img.setPublicUrl(publicBaseUrl + "/" + id);

        final BufferedImage bimg = ImageIO.read(new ByteArrayInputStream(data.getContent()));
        if (bimg != null) {
            img.setWidth(bimg.getWidth());
            img.setHeight(bimg.getHeight());
        } else {
            img.setWidth(0);
            img.setHeight(0);
        }

        return img;
    }

    private String key(final String id, final ImageData data) throws MimeTypeException {
        final MimeType mime = MimeTypes.getDefaultMimeTypes().forName(data.getContentType());
        return "images/" + id + "/0." + mime.getExtension();
    }
}

