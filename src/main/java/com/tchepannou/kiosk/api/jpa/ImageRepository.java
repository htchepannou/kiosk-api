package com.tchepannou.kiosk.api.jpa;

import com.tchepannou.kiosk.api.domain.Image;
import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<Image, String> {
}
