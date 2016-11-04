package com.tchepannou.kiosk.api.mapper;

import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.client.dto.ImageDto;
import org.springframework.beans.factory.annotation.Value;

public class ImageMapper {
    @Value("${kiosk.image.public.baseUri}")
    String publicBaseUrl;

    public ImageDto toImageDto(final Image image){
        final ImageDto dto = new ImageDto();
        dto.setContentType(image.getContentType());
        dto.setHeight(image.getHeight());
        dto.setId(image.getId());
        dto.setWidth(image.getWidth());
        dto.setTitle(image.getTitle());
        dto.setPublicUrl(publicBaseUrl + "/" + image.getId());
        return dto;
    }
}
