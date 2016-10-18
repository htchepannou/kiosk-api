package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import com.tchepannou.kiosk.api.pipeline.support.RankableArticle;
import com.tchepannou.kiosk.image.Dimension;
import com.tchepannou.kiosk.ranker.Ranker;
import com.tchepannou.kiosk.ranker.Score;
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
    List<Dimension> dimensions;

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

        final Score re1 = createSore(a1, 5);
        final Score re2 = createSore(a2, 10);
        final Score re3 = createSore(a3, 15);
        when(ranker.rank(any(), any())).thenReturn(Arrays.asList(re1, re2, re3));

        // When
        actitivy.doHandleEvent(new Event("foo", articles));

        // Then
        assertThat(a1.getScore()).isEqualTo(5);
        assertThat(a1.getRank()).isEqualTo(3);

        assertThat(a2.getScore()).isEqualTo(10);
        assertThat(a2.getRank()).isEqualTo(2);

        assertThat(a3.getScore()).isEqualTo(15);
        assertThat(a3.getRank()).isEqualTo(1);

        verify(articleRepository).save(articles);
    }

    private Score createSore(final Article article, final int value){
        final RankableArticle ra = new RankableArticle(article);
        final Score entry = mock(Score.class);
        when(entry.getValue()).thenReturn(value);
        when(entry.getRankable()).thenReturn(ra);

        return entry;
    }
}
