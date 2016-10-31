package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.pipeline.ArticleActivity;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.support.ValidableArticle;
import com.tchepannou.kiosk.validator.Validation;
import com.tchepannou.kiosk.validator.Validator;
import com.tchepannou.kiosk.validator.ValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidateActivity extends ArticleActivity {
    @Autowired
    Validator validator;

    @Autowired
    ValidatorContext context;

    @Override
    protected String getTopic() {
        return PipelineConstants.EVENT_VALIDATE;
    }

    @Override
    protected String doHandleArticle(final Article article) {
        final ValidableArticle varticle = new ValidableArticle(article);

        final Validation validation = validator.validate(varticle, context);
        if (!validation.isSuccess()) {
            article.setStatus(Article.Status.rejected);
            article.setStatusReason(validation.getReason());
        }
        log(validation);

        return PipelineConstants.EVENT_END;
    }

    private void log(final Validation validation) {
        log.add("Success", validation.isSuccess());
        log.add("ValidationReason", validation.getReason());
    }
}
