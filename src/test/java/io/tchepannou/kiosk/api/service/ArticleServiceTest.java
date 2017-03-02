package io.tchepannou.kiosk.api.service;

import io.tchepannou.kiosk.api.model.ArticleModelList;
import io.tchepannou.kiosk.api.persistence.domain.Article;
import io.tchepannou.kiosk.api.persistence.domain.Feed;
import io.tchepannou.kiosk.api.persistence.domain.Image;
import io.tchepannou.kiosk.api.persistence.domain.Link;
import io.tchepannou.kiosk.api.persistence.domain.Video;
import io.tchepannou.kiosk.api.persistence.repository.ArticleRepository;
import io.tchepannou.kiosk.api.persistence.repository.ImageRepository;
import io.tchepannou.kiosk.api.persistence.repository.VideoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static io.tchepannou.kiosk.api.service.Fixtures.createArticle;
import static io.tchepannou.kiosk.api.service.Fixtures.createFeed;
import static io.tchepannou.kiosk.api.service.Fixtures.createImage;
import static io.tchepannou.kiosk.api.service.Fixtures.createLink;
import static io.tchepannou.kiosk.api.service.Fixtures.createVideo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArticleServiceTest {
    @Mock
    ArticleRepository articleRepository;

    @Mock
    ImageRepository imageRepository;

    @Mock
    VideoRepository videoRepository;

    @Mock
    ArticleMapper mapper;

    @Mock
    FeatureFlagService featureFlagService;

    @InjectMocks
    ArticleService service;

    @Before
    public void setUp (){
        when(featureFlagService.isVideoEnabled()).thenReturn(true);
    }

    @Test
    public void shouldReturnListOfVideo() throws Exception {
        // Given
        final Feed feed1 = createFeed();
        final Feed feed2 = createFeed();
        final Feed feed3 = createFeed();

        final Link link11 = createLink(feed1);
        final Link link12 = createLink(feed1);
        final Link link21 = createLink(feed2);
        final Link link22 = createLink(feed2);
        final Link link31 = createLink(feed3);

        final Article article11 = createArticle(link11);
        final Article article12 = createArticle(link12);
        final Article article21 = createArticle(link21);
        final Article article22 = createArticle(link22);
        final Article article31 = createArticle(link31);
        when(articleRepository.findByStatus(anyInt(), any()))
                .thenReturn(Arrays.asList(article11, article12, article21, article22, article31));

        final Image image11 = createImage(link11, Image.TYPE_MAIN);
        final Image image21 = createImage(link21, Image.TYPE_MAIN);
        final Image image31 = createImage(link31, Image.TYPE_MAIN);
        final Image thumbnail11 = createImage(link11, Image.TYPE_THUMBNAIL);
        final Image thumbnail21 = createImage(link21, Image.TYPE_THUMBNAIL);
        final Image thumbnail31 = createImage(link31, Image.TYPE_THUMBNAIL);
        when(imageRepository.findByLinkIn(any()))
                .thenReturn(Arrays.asList(image11, image21, image31, thumbnail11, thumbnail21, thumbnail31));

        final Video video111 = createVideo(link11);
        final Video video112 = createVideo(link11);
        final Video video121 = createVideo(link12);
        when(videoRepository.findByLinkIn(any()))
                .thenReturn(Arrays.asList(video111, video112, video121));

        final ArticleModelList list = new ArticleModelList();
        when(mapper.toArticleListModel(any())).thenReturn(list);

        // Given
        final ArticleModelList result = service.list(0, 20);

        // Then
        assertThat(result).isEqualTo(list);
    }

    @Test
    public void shouldNotReturnVideoWhenFeatureFlagOFF() throws Exception {
        // Given
        final Feed feed1 = createFeed();
        final Feed feed2 = createFeed();
        final Feed feed3 = createFeed();

        final Link link11 = createLink(feed1);
        final Link link12 = createLink(feed1);
        final Link link21 = createLink(feed2);
        final Link link22 = createLink(feed2);
        final Link link31 = createLink(feed3);

        final Article article11 = createArticle(link11);
        final Article article12 = createArticle(link12);
        final Article article21 = createArticle(link21);
        final Article article22 = createArticle(link22);
        final Article article31 = createArticle(link31);
        when(articleRepository.findByStatus(anyInt(), any()))
                .thenReturn(Arrays.asList(article11, article12, article21, article22, article31));

        final Image image11 = createImage(link11, Image.TYPE_MAIN);
        final Image image21 = createImage(link21, Image.TYPE_MAIN);
        final Image image31 = createImage(link31, Image.TYPE_MAIN);
        final Image thumbnail11 = createImage(link11, Image.TYPE_THUMBNAIL);
        final Image thumbnail21 = createImage(link21, Image.TYPE_THUMBNAIL);
        final Image thumbnail31 = createImage(link31, Image.TYPE_THUMBNAIL);
        when(imageRepository.findByLinkIn(any()))
                .thenReturn(Arrays.asList(image11, image21, image31, thumbnail11, thumbnail21, thumbnail31));

        final Video video111 = createVideo(link11);
        final Video video112 = createVideo(link11);
        final Video video121 = createVideo(link12);
        when(videoRepository.findByLinkIn(any()))
                .thenReturn(Arrays.asList(video111, video112, video121));

        final ArticleModelList list = new ArticleModelList();
        when(mapper.toArticleListModel(any())).thenReturn(list);

        when(featureFlagService.isVideoEnabled()).thenReturn(false);

        // Given
        service.list(0, 20);

        // Then
        verify(videoRepository, never()).findByLinkIn(any());

    }

}
