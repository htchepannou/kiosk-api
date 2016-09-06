package com.tchepannou.kiosk.api.jpa;

import com.tchepannou.kiosk.api.domain.Feed;
import org.springframework.data.repository.CrudRepository;

public interface FeedRepository extends CrudRepository<Feed, Long> {
}
