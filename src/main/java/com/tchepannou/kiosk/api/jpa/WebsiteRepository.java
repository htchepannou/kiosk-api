package com.tchepannou.kiosk.api.jpa;

import com.tchepannou.kiosk.api.domain.Website;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WebsiteRepository extends CrudRepository<Website, Long> {
    List<Website> findByActive(boolean active);
}
