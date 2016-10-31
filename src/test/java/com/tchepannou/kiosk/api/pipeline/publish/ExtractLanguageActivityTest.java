package com.tchepannou.kiosk.api.pipeline.publish;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.pipeline.ActivityTestSupport;
import com.tchepannou.kiosk.api.pipeline.PipelineConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ExtractLanguageActivityTest extends ActivityTestSupport {

    @InjectMocks
    ExtractLanguageActivity activity;

    @Test
    public void testGetTopic() throws Exception {
        assertThat(activity.getTopic()).isEqualTo(PipelineConstants.EVENT_EXTRACT_LANGUAGE);
    }

    @Test
    public void testDoHandleEventFR() throws Exception {
        final Article article = createArticle(
                "Joseph Otto Wilson, le Gouverneur de la Région du Centre, est mort !",
                "La triste nouvelle n’est pas encore officielle. Mais, des sources proches de la famille du Gouverneur affirment que ce dernier est décédé dans la matinée de ce lundi 24 octobre 2016 en France des suites de maladie."
        );

        final String next = activity.doHandleArticle(article);

        assertThat(article.getLanguageCode()).isEqualTo("fr");
        assertThat(next).isEqualTo(PipelineConstants.EVENT_VALIDATE);
    }

    @Test
    public void testDoHandleEventEN() throws Exception {
        final Article article = createArticle(
                "New MELA Executive To Sustain Common Law Lawyers’ Momentum",
                "A new executive of the Meme Lawyers’ Association, MELA, elected into office on October 15, has pledged to sustain the current momentum of Common Law lawyers’ in Cameroon."
        );

        final String next = activity.doHandleArticle(article);

        assertThat(article.getLanguageCode()).isEqualTo("en");
        assertThat(next).isEqualTo(PipelineConstants.EVENT_VALIDATE);
    }

    private Article createArticle(final String title, final String slug) {
        final Article article = new Article();
        article.setTitle(title);
        article.setSlug(slug);
        return article;
    }
}
