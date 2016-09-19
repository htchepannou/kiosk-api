package com.tchepannou.kiosk.api.jpa;

import com.tchepannou.kiosk.api.domain.Article;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArticleRepository extends CrudRepository<Article, String> {
    List<Article> findByStatusOrderByPublishedDateDesc(Article.Status status);
}
