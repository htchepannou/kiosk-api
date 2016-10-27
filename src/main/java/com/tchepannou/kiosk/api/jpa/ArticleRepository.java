package com.tchepannou.kiosk.api.jpa;

import com.tchepannou.kiosk.api.domain.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface ArticleRepository extends CrudRepository<Article, String> {
    List<Article> findByPublishedDateBetweenAndTitleAndIdNot(Date start, Date end, String title, String id);

    List<Article> findByStatus(Article.Status status, Pageable page);

    List<Article> findByStatusAndPublishedDateBetween(Article.Status status, Date start, Date end, Pageable page);
}
