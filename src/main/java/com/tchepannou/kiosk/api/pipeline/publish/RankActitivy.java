package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.support.RankableArticle;
import com.tchepannou.kiosk.ranker.DimensionSetProvider;
import com.tchepannou.kiosk.ranker.RankEntry;
import com.tchepannou.kiosk.ranker.Ranker;
import com.tchepannou.kiosk.ranker.RankerContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class RankActitivy extends Activity {
    @Autowired
    Ranker ranker;

    @Autowired
    DimensionSetProvider dimensionSetProvider;

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

        final List<RankEntry> entries = ranker.rank((List) rankables, ctx);

        // Update the articles
        for (RankEntry entry : entries){
            RankableArticle rankableArticle = (RankableArticle)entry.getRankable();
            rankableArticle.getArticle().setRank((int)(100d*entry.getFinalRank()));
        }

        // Save
        articleRepository.save(articles);
    }

    private RankerContext createRankerContext() {
        return new RankerContext() {
            @Override
            public DimensionSetProvider getDimensionSetProvider() {
                return dimensionSetProvider;
            }
        };
    }
}
