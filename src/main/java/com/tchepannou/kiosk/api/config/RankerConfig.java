package com.tchepannou.kiosk.api.config;

import com.tchepannou.kiosk.api.mapper.RankableMapper;
import com.tchepannou.kiosk.api.ranker.ArticleDimension;
import com.tchepannou.kiosk.api.ranker.Rankable;
import com.tchepannou.kiosk.api.ranker.comparator.ContentLengthComparator;
import com.tchepannou.kiosk.api.ranker.comparator.ImageComparator;
import com.tchepannou.kiosk.api.ranker.comparator.PublishedDateComparator;
import com.tchepannou.kiosk.core.ranker.Ranker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class RankerConfig {
    @Value("${kiosk.ranker.dimension.publishedDate.weight")
    private float publishedDateWeight;

    @Value("${kiosk.ranker.dimension.image.weight")
    private float imageWeight;

    @Value("${kiosk.ranker.dimension.contentLength.weight")
    private float contentLengthWeight;

    @Bean
    RankableMapper rankableMapper(){
        return new RankableMapper();
    }

    @Bean
    Ranker<Rankable> ranker() {
        return new Ranker<>(Arrays.asList(
                new ArticleDimension("publishedDate", publishedDateWeight, publishedDateComparator()),
                new ArticleDimension("image", imageWeight, imageComparator()),
                new ArticleDimension("contentLength", contentLengthWeight, contentLengthComparator())
        ));
    }

    @Bean
    PublishedDateComparator publishedDateComparator() {
        return new PublishedDateComparator();
    }

    @Bean
    ImageComparator imageComparator() {
        return new ImageComparator();
    }

    @Bean
    ContentLengthComparator contentLengthComparator() {
        return new ContentLengthComparator();
    }
}
