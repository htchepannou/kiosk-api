package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;

public class DownloadImageActivity extends Activity {
    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ImageService imageService;

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
            return;
        }

        /* download */
        Image img = null;
        try {
            img = imageService.download(image);
            if (img != null){
                imageRepository.save(img);
                log(img, null);
            }

        } catch (final Exception ex) {
            log(img, ex);
        }
    }

    private void log(final Image image, final Throwable ex) {
        addToLog(image);
        addToLog(ex);
        log.log(ex);
    }
}
