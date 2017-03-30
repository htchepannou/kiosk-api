package io.tchepannou.kiosk.api.persistence.repository;

import io.tchepannou.kiosk.api.persistence.domain.Asset;
import io.tchepannou.kiosk.api.persistence.domain.Link;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends CrudRepository<Asset, Long> {
    List<Asset> findByLinkIn(List<Link> link);
}
