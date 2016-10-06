package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.mapper.RankableMapper;
import com.tchepannou.kiosk.api.ranker.Rankable;
import com.tchepannou.kiosk.core.ranker.RankEntry;
import com.tchepannou.kiosk.core.ranker.Ranker;
import com.tchepannou.kiosk.core.service.TimeService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RankerService {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    Ranker<Rankable> ranker;

    @Autowired
    RankableMapper rankableMapper;

    @Autowired
    TimeService timeService;

    @Transactional
    public void rank() {
        final List<Article> articles = getAllArticles();

        final List<Rankable> rankables = articles.stream()
                .map(a -> rankableMapper.toRankable(a))
                .collect(Collectors.toList());

        final List<RankEntry<Rankable>> entries = ranker.rank(rankables);
        final Map<String, Article> articleMap = articles.stream()
                .collect(Collectors.toMap(Article::getId, Function.identity()));

        for (final RankEntry<Rankable> entry : entries){
            Article article = articleMap.get(entry.getRankable().getId());
            article.setRank((int)(entry.getFinalRank()*100));
        }

        Collections.sort(articles, (a1, a2) -> a1.getRank() - a2.getRank());

        articleRepository.save(articles);
    }

    private List<Article> getAllArticles() {
        final List<Article> articles = new ArrayList<>();
        final Date end = timeService.now();
        final Date start = DateUtils.addDays(end, -1);
        for (int i=0 ; i<10 ; i++){
            final PageRequest page = new PageRequest(i, 1000);
            List<Article> result = articleRepository.findByStatusAndPublishedDateBetween(
                    Article.Status.processed,
                    start,
                    end,
                    page
            );

            articles.addAll(result);
            if (result.size() < page.getPageSize()){
                break;
            }
        }
        return articles;
    }

}
