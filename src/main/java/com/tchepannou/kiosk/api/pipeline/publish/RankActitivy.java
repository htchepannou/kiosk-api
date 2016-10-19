package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.support.RankableArticle;
import com.tchepannou.kiosk.ranker.Dimension;
import com.tchepannou.kiosk.ranker.Ranker;
import com.tchepannou.kiosk.ranker.RankerContext;
import com.tchepannou.kiosk.ranker.Score;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.stream.Collectors;

public class RankActitivy extends Activity {
    @Autowired
    Ranker ranker;

    @Autowired
    @Qualifier(PipelineConstants.BEAN_RANKER_DIMENSIONS)
    List<Dimension> dimensions;

    @Autowired
    ArticleRepository articleRepository;

    @Override
    protected String getTopic() {
        return PipelineConstants.TOPIC_ARTICLE_PROCESS;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        // Rank
        final List<Article> articles = (List) event.getPayload();
        final RankerContext ctx = createRankerContext();
        final List<RankableArticle> rankables = articles.stream()
                .map(a -> new RankableArticle(a))
                .collect(Collectors.toList());

        final List<Score> scores = ranker.rank((List) rankables, ctx);

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
    }

    private RankerContext createRankerContext() {
        return new RankerContext() {
            @Override
            public List<com.tchepannou.kiosk.ranker.Dimension> getDimensions() {
                return dimensions;
            }
        };
    }
}
