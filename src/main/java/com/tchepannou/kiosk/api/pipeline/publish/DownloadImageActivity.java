package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.core.service.FileService;
import com.tchepannou.kiosk.core.service.HttpService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Transactional
public class DownloadImageActivity extends Activity {
    @Autowired
    FileService fileService;

    @Autowired
    HttpService httpService;

    @Autowired
    ImageRepository imageRepository;

    @Override
    protected String getTopic() {
        return PipelineConstants.TOPIC_IMAGE_SUBMITTED;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        final Image image = (Image) event.getPayload();

        /* already downloaded ?*/
        final String id = image.getId();
        if (imageRepository.findOne(id) != null) {
            log(image, null);
            return;
        }

        /* download */
        try {
            final String url = image.getUrl();
            final String keyPrefix = "/images/" + id + "/0";
            final String key = httpService.get(url, keyPrefix, fileService);
            final FileTypeMap typeMap = MimetypesFileTypeMap.getDefaultFileTypeMap();
            final String contentType = typeMap.getContentType(key);

            if (contentType != null && contentType.startsWith("image/")) {
                image.setKey(key);
                image.setContentType(contentType);
                getDimension(image);
                imageRepository.save(image);
                log(image, null);
            } else {
                log(image, null);
            }
        } catch (final Exception ex) {
            log(image, ex);
        }
    }

    private void getDimension(final Image image) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        fileService.get(image.getKey(), out);

        final BufferedImage bimg = ImageIO.read(new ByteArrayInputStream(out.toByteArray()));
        image.setWidth(bimg.getWidth());
        image.setHeight(bimg.getHeight());
    }

    private void log(final Image image, final Throwable ex) {
        log.add("ImageUrl", image.getUrl());

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
