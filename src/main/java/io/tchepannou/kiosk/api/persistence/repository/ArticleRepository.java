package io.tchepannou.kiosk.api.persistence.repository;

import io.tchepannou.kiosk.api.persistence.domain.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends CrudRepository<Article, Long> {
    List<Article> findByStatus(int status, Pageable page);
}
