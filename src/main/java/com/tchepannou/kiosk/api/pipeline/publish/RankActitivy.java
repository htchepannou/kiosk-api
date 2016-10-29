package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.support.RankableArticle;
import com.tchepannou.kiosk.ranker.Ranker;
import com.tchepannou.kiosk.ranker.RankerContext;
import com.tchepannou.kiosk.ranker.Score;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class RankActitivy extends Activity {
    @Autowired
    Ranker ranker;

    @Autowired
    RankerContext context;

    @Autowired
    ArticleRepository articleRepository;

    @Override
    protected String getTopic() {
        return PipelineConstants.EVENT_RANK;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        // Rank
        final List<Article> articles = (List) event.getPayload();
        final List<RankableArticle> rankables = articles.stream()
                .map(a -> new RankableArticle(a))
                .collect(Collectors.toList());

        final List<Score> scores = ranker.rank((List) rankables, context);

        // Update the articles
        int rank = scores.size();
        for (Score score : scores){
            final RankableArticle rankableArticle = (RankableArticle)score.getRankable();
            final Article article = rankableArticle.getArticle();

            article.setScore(score.getValue());
            article.setRank(rank--);
        }

        // Save
        articleRepository.save(articles);
        publishEvent(new Event(PipelineConstants.EVENT_EXTRACT_KEYWORDS, articles));
    }

}
