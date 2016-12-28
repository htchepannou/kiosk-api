package io.tchepannou.kiosk.api.persistence.repository;

import io.tchepannou.kiosk.api.persistence.domain.Link;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepository extends CrudRepository<Link, Long> {
}
