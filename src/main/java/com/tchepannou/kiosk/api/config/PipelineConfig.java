package com.tchepannou.kiosk.api.config;

import com.tchepannou.kiosk.api.pipeline.publish.CreateArticleActivity;
import com.tchepannou.kiosk.api.pipeline.publish.EndActivity;
import com.tchepannou.kiosk.api.pipeline.publish.ExtractContentActivity;
import com.tchepannou.kiosk.api.pipeline.publish.ExtractImageActivity;
import com.tchepannou.kiosk.api.pipeline.publish.RankActitivy;
import com.tchepannou.kiosk.api.pipeline.publish.ValidateActivity;
import com.tchepannou.kiosk.api.service.ImageService;
import com.tchepannou.kiosk.content.ContentExtractor;
import com.tchepannou.kiosk.content.DefaultFilterSetProvider;
import com.tchepannou.kiosk.content.FilterSetProvider;
import com.tchepannou.kiosk.content.TitleSanitizer;
import com.tchepannou.kiosk.image.ImageExtractor;
import com.tchepannou.kiosk.image.support.ImageGrabber;
import com.tchepannou.kiosk.ranker.Dimension;
import com.tchepannou.kiosk.ranker.Ranker;
import com.tchepannou.kiosk.ranker.RankerContext;
import com.tchepannou.kiosk.ranker.ScoreProvider;
import com.tchepannou.kiosk.ranker.score.ContentLengthScoreProvider;
import com.tchepannou.kiosk.ranker.score.ImageScoreProvider;
import com.tchepannou.kiosk.ranker.score.PublishedDateScoreProvider;
import com.tchepannou.kiosk.validator.Rule;
import com.tchepannou.kiosk.validator.Validator;
import com.tchepannou.kiosk.validator.ValidatorContext;
import com.tchepannou.kiosk.validator.rules.ContentLengthRule;
import com.tchepannou.kiosk.validator.rules.TitleLengthRule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class PipelineConfig {
    @Value("${kiosk.filters.ContentFilter.blocMinLength}")
    int minBlocLength;

    @Value("${kiosk.filters.ArticleTitleFilter.maxLength}")
    int titleMaxLength;

    @Value("${kiosk.rules.TextLengthRule.minLength}")
    int minTextLength;

    //-- Create
    @Bean
    CreateArticleActivity createArticleActivity() {
        return new CreateArticleActivity();
    }

    //-- Process
    @Bean
    ExtractContentActivity processArticleActivity() {
        return new ExtractContentActivity();
    }

    @Bean
    ContentExtractor contentExtractor() {
        return new ContentExtractor();
    }

    @Bean
    FilterSetProvider filterSetProvider() {
        return new DefaultFilterSetProvider(minTextLength);
    }

    @Bean
    TitleSanitizer titleSanitizer() {
        return new TitleSanitizer();
    }

    //-- Image
    @Bean
    ExtractImageActivity extractImageActivity() {
        return new ExtractImageActivity();
    }

    @Bean
    ImageExtractor imageExtractor() {
        return new ImageExtractor();
    }

    @Bean
    ImageService imageService() {
        return new ImageService();
    }

    @Bean
    ImageGrabber imageGrabber() {
        return new ImageGrabber();
    }

    //-- Validation
    @Bean
    ValidateActivity validateActivity() {
        return new ValidateActivity();
    }

    @Bean
    Validator validator() {
        return new Validator();
    }

    @Bean
    ValidatorContext validatorContext() {
        return new ValidatorContext() {
            @Override
            public List<Rule> getRules() {
                return Arrays.asList(
                        new ContentLengthRule(minTextLength),
                        new TitleLengthRule()
                );
            }
        };
    }

    //-- Rank
    @Bean
    RankActitivy rankActitivy() {
        return new RankActitivy();
    }

    @Bean
    Ranker ranker() {
        return new Ranker();
    }

    @Bean
    RankerContext rankerContext() {
        return new RankerContext() {
            @Override
            public List<Dimension> getDimensions() {
                return Arrays.asList(
                        createDimension("published-date", .50, new PublishedDateScoreProvider()),
                        createDimension("image", .35, new ImageScoreProvider()),
                        createDimension("content-length", .15, new ContentLengthScoreProvider())
                );
            }
        };
    }

    Dimension createDimension(final String name, final double weight, final ScoreProvider score) {
        return new Dimension() {
            @Override
            public double getWeight() {
                return weight;
            }

            @Override
            public ScoreProvider getScoreProvider() {
                return score;
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

    //-- End
    @Bean
    EndActivity endActivity(){
        return new EndActivity();
    }
}
