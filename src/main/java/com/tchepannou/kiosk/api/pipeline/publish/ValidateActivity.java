package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.support.ValidableArticle;
import com.tchepannou.kiosk.validator.Validation;
import com.tchepannou.kiosk.validator.Validator;
import com.tchepannou.kiosk.validator.ValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidateActivity extends Activity {
    @Autowired
    Validator validator;

    @Autowired
    ValidatorContext context;

    @Autowired
    ArticleRepository articleRepository;

    @Override
    protected String getTopic() {
        return PipelineConstants.TOPIC_ARTICLE_IMAGE_EXTRACTED;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        final Article article = (Article) event.getPayload();
        final ValidableArticle varticle = new ValidableArticle(article);

        final Validation validation = validator.validate(varticle, context);
        log(article, validation);
        if (validation.isSuccess()) {
            publishEvent(new Event(PipelineConstants.TOPIC_ARTICLE_VALIDATED, article));
        } else {
            article.setStatus(Article.Status.rejected);
            article.setStatusReason(validation.getReason());
            articleRepository.save(article);

            publishEvent(new Event(PipelineConstants.TOPIC_ARTICLE_REJECTED, article));
        }
    }

    private void log(final Article article, final Validation validation){
        super.addToLog(article);

        log.add("Success", validation.isSuccess());
        log.add("ValidationReason", validation.getReason());
    }
}
