package com.tchepannou.kiosk.api.mapper;

import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.client.dto.ImageDto;

public class ImageMapper {
    public ImageDto toImageDto(final Image image){
        final ImageDto dto = new ImageDto();
        dto.setContentType(image.getContentType());
        dto.setHeight(image.getHeight());
        dto.setId(image.getId());
        dto.setWidth(image.getWidth());
        dto.setTitle(image.getTitle());
        dto.setPublicUrl(image.getPublicUrl());
        return dto;
    }
}
