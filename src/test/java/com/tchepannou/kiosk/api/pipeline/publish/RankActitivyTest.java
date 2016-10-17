package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.support.RankableArticle;
import com.tchepannou.kiosk.ranker.DimensionSetProvider;
import com.tchepannou.kiosk.ranker.RankEntry;
import com.tchepannou.kiosk.ranker.Ranker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.tchepannou.kiosk.api.Fixture.createArticle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RankActitivyTest {
    @Mock
    Ranker ranker;

    @Mock
    DimensionSetProvider dimensionSetProvider;

    @Mock
    ArticleRepository articleRepository;

    @InjectMocks
    RankActitivy actitivy;

    @Test
    public void testGetTopic() throws Exception {
        assertThat(actitivy.getTopic()).isEqualTo(PipelineConstants.TOPIC_ARTICLE_PROCESS);
    }

    @Test
    public void testDoHandleEvent() throws Exception {
        final Article a1 = createArticle();
        final Article a2 = createArticle();
        final Article a3 = createArticle();
        final List<Article> articles = Arrays.asList(a1, a2, a3);

        final RankEntry re1 = createRankEntry(a1, 5);
        final RankEntry re2 = createRankEntry(a2, 10);
        final RankEntry re3 = createRankEntry(a3, 15);
        when(ranker.rank(any(), any())).thenReturn(Arrays.asList(re1, re2, re3));

        // When
        actitivy.doHandleEvent(new Event("foo", articles));

        // Then
        assertThat(a1.getRank()).isEqualTo(5);
        assertThat(a2.getRank()).isEqualTo(10);
        assertThat(a3.getRank()).isEqualTo(15);

        verify(articleRepository).save(articles);
    }

    private RankEntry createRankEntry(final Article article, final double finalRank){
        final RankableArticle ra = new RankableArticle(article);
        final RankEntry entry = mock(RankEntry.class);
        when(entry.getFinalRank()).thenReturn(finalRank);
        when(entry.getRankable()).thenReturn(ra);

        return entry;
    }
}
