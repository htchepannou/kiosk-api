package io.tchepannou.kiosk.api.persistence.repository;

import io.tchepannou.kiosk.api.persistence.domain.Link;
import io.tchepannou.kiosk.api.persistence.domain.LinkStatusEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkRepository extends CrudRepository<Link, Long> {
    List<Link> findByStatus(LinkStatusEnum status, Pageable pagination);
}
