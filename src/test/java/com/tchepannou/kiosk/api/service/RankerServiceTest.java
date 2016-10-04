package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.Fixture;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.mapper.RankableMapper;
import com.tchepannou.kiosk.api.ranker.Rankable;
import com.tchepannou.kiosk.core.ranker.RankEntry;
import com.tchepannou.kiosk.core.ranker.Ranker;
import com.tchepannou.kiosk.core.service.TimeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RankerServiceTest {
    @Mock
    ArticleRepository articleRepository;

    @Mock
    Ranker<Rankable> ranker;

    @Mock
    RankableMapper rankableMapper;

    @Mock
    TimeService timeService;

    @InjectMocks
    RankerService service;

    @Test
    public void testRank() throws Exception {
        // Given
        when(timeService.now()).thenReturn(new Date());

        final Article a1 = createArticle("1");
        final Article a2 = createArticle("2");
        final Article a3 = createArticle("3");
        when(articleRepository.findByStatusByPublishedDateBetween(any(), any(), any(), any()))
                .thenReturn(Arrays.asList(a1, a2, a3));

        final Rankable r1 = createRankable(a1);
        final Rankable r2 = createRankable(a2);
        final Rankable r3 = createRankable(a3);
        when(rankableMapper.toRankable(any()))
                .thenReturn(r1)
                .thenReturn(r2)
                .thenReturn(r3);

        final RankEntry<Rankable> e1 = createRankEntry(2, r1);
        final RankEntry<Rankable> e2 = createRankEntry(1, r2);
        final RankEntry<Rankable> e3 = createRankEntry(3, r3);
        when(ranker.rank(any())).thenReturn(Arrays.asList(e1, e2, e3));

        // When
        service.rank();

        // Then
        final ArgumentCaptor<Iterable<Article>> it = (ArgumentCaptor)ArgumentCaptor.forClass(Iterable.class);
        verify(articleRepository).save(it.capture());

        final List<Article> articles = toList(it.getValue());
        assertThat(articles).containsExactly(a2, a1, a3);
        assertThat(a2.getRank()).isEqualTo(100);
        assertThat(a1.getRank()).isEqualTo(200);
        assertThat(a3.getRank()).isEqualTo(300);
    }

    private Article createArticle(final String id){
        final Article article = Fixture.createArticle();
        article.setId(id);
        return article;
    }

    private RankEntry<Rankable> createRankEntry(final float finalRank, final Rankable r) {
        return new RankEntry(r){
            @Override
            public float getFinalRank() {
                return finalRank;
            }
        };
    }

    private Rankable createRankable(final Article article) {
        final Rankable r = new Rankable();
        r.setId(article.getId());
        return r;
    }

    private List<Article> toList(Iterable<Article> it){
        final List<Article> articles = new ArrayList<>();
        for (Article article : it){
            articles.add(article);
        }
        return articles;
    }
}
