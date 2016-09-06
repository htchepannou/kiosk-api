package com.tchepannou.kiosk.api.jpa;

import com.tchepannou.kiosk.api.domain.Article;
import org.springframework.data.repository.CrudRepository;

public interface ArticleRepository extends CrudRepository<Article, String> {
}
