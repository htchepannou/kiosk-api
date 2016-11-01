package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.pipeline.ArticleActivity;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.support.RankableArticle;
import com.tchepannou.kiosk.ranker.RankerContext;
import com.tchepannou.kiosk.ranker.Score;
import com.tchepannou.kiosk.ranker.Scorer;
import org.springframework.beans.factory.annotation.Autowired;

public class ScoreActitivy extends ArticleActivity {
    @Autowired
    RankerContext context;

    @Autowired
    Scorer scorer;

    @Override
    protected String getTopic() {
        return PipelineConstants.EVENT_SCORE;
    }

    @Override
    protected String doHandleArticle(final Article article){
        Score score = scorer.compute(new RankableArticle(article), context);
        article.setScore(score.getValue());

        return PipelineConstants.EVENT_END;
    }

}
