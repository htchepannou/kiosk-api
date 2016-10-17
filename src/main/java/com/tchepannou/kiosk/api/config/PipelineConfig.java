package com.tchepannou.kiosk.api.config;

import com.tchepannou.kiosk.api.filter.ArticleFilterSet;
import com.tchepannou.kiosk.api.filter.ArticleLanguageFilter;
import com.tchepannou.kiosk.api.filter.ArticleTitleFilter;
import com.tchepannou.kiosk.api.pipeline.publish.CreateArticleActivity;
import com.tchepannou.kiosk.api.pipeline.publish.ExtractImageActivity;
import com.tchepannou.kiosk.api.pipeline.publish.ExtractContentActivity;
import com.tchepannou.kiosk.api.service.ImageService;
import com.tchepannou.kiosk.content.ContentExtractor;
import com.tchepannou.kiosk.content.DefaultFilterSetProvider;
import com.tchepannou.kiosk.content.FilterSetProvider;
import com.tchepannou.kiosk.content.TitleSanitizer;
import com.tchepannou.kiosk.image.ImageExtractor;
import com.tchepannou.kiosk.image.support.ImageGrabber;
import com.tchepannou.kiosk.ranker.Dimension;
import com.tchepannou.kiosk.ranker.DimensionSetProvider;
import com.tchepannou.kiosk.ranker.Rankable;
import com.tchepannou.kiosk.ranker.Ranker;
import com.tchepannou.kiosk.ranker.comparator.ContentLengthComparator;
import com.tchepannou.kiosk.ranker.comparator.ImageComparator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Comparator;

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

    @Bean
    ArticleFilterSet articleFilterSet() {
        return new ArticleFilterSet(Arrays.asList(
                new ArticleLanguageFilter(),
                new ArticleTitleFilter(titleMaxLength)
        ));
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

    //-- Rank
    @Bean
    Ranker ranker() {
        return new Ranker();
    }

    @Bean
    DimensionSetProvider dimensionSetProvider() {
        return () -> Arrays.asList(
                createDimension("image", .6, new ImageComparator()),
                createDimension("content-length", .4, new ContentLengthComparator())
        );
    }

    Dimension createDimension(final String name, final double weight, final Comparator<Rankable> comparator) {
        return new Dimension() {
            @Override
            public double getWeight() {
                return weight;
            }

            @Override
            public Comparator<Rankable> getComparator() {
                return comparator;
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }
}
