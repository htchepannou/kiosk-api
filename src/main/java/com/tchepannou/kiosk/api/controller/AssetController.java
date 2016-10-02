package com.tchepannou.kiosk.api.controller;

import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.api.jpa.ImageRepository;
import com.tchepannou.kiosk.core.service.FileService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@Api(basePath = "/kiosk/v1/asset", value = "Asset API")
@RequestMapping(value = "/kiosk/v1/asset")
public class AssetController {
    @Autowired
    ImageRepository imageRepository;

    @Autowired
    FileService fileService;

    @RequestMapping(value = "/image/{imageId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> image(@PathVariable final String imageId) throws IOException {
        final Image img = imageRepository.findOne(imageId);
        if (img == null) {

            return (ResponseEntity) ResponseEntity.notFound();

        } else {

            try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                fileService.get(img.getKey(), out);

                try (final InputStream in = new ByteArrayInputStream(out.toByteArray())) {
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(img.getContentType()))
                            .body(new InputStreamResource(in));
                }
            }

        }
    }
}
