package io.tchepannou.kiosk.api.persistence.repository;

import io.tchepannou.kiosk.api.persistence.domain.Image;
import io.tchepannou.kiosk.api.persistence.domain.Link;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {
    List<Image> findByLinkIn(Collection<Link> links);
}
