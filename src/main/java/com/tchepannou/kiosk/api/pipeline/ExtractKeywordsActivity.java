package com.tchepannou.kiosk.api.pipeline;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.service.ArticleService;
import com.tchepannou.kiosk.core.service.FileService;
import com.tchepannou.kiosk.core.service.TimeService;
import com.tchepannou.kiosk.core.text.Fragment;
import com.tchepannou.kiosk.core.text.TextKit;
import com.tchepannou.kiosk.core.text.TextKitProvider;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ExtractKeywordsActivity extends Activity {
    @Autowired
    FileService fileService;

    @Autowired
    TextKitProvider textKitProvider;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    TimeService timeService;

    @Autowired
    ArticleService articleService;

    @Override
    protected String getTopic() {
        return PipelineConstants.EVENT_EXTRACT_KEYWORDS;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        final List<Article> articles = getArticles(event);

        final Map<Article, List<String>> allKeywords = new HashMap<>();

        for (final Article article : articles) {
            final List<String> keywords = extractKeywords(article);
            allKeywords.put(article, keywords);
        }

        printFrequency(allKeywords);

        publishEvent(new Event(PipelineConstants.EVENT_END, event.getPayload()));
    }

    private List<Article> getArticles(final Event event) {
        final Date endDate = timeService.now();
        final Date startDate = DateUtils.addDays(endDate, -7);
        final PageRequest pagination = new PageRequest(0, 300);
        final List<Article> articles = articleRepository.findByStatusAndPublishedDateBetween(Article.Status.processed, startDate, endDate, pagination);

        final List<Article> result = ((List<Article>) event.getPayload()).stream()
                .filter(a -> Article.Status.processed.equals(a.getStatus()))
                .collect(Collectors.toList());
        result.addAll(articles);

        return result;
    }

    private List<String> extractKeywords(final Article article) {
        final String content = articleService.fetchContent(article, Article.Status.processed);
        final String xcontent = Jsoup.parse(content).text();
        final String title = article.getTitle();
        final TextKit kit = textKitProvider.get(article.getLanguageCode());
        final List<Fragment> fragments = Fragment.parse(title + "." + xcontent, kit);
        final List<String> keywords = new ArrayList<>();
        for (final Fragment fragment : fragments) {
            keywords.addAll(fragment.extractKeywords(3));
        }

        Collections.sort(keywords);
        return keywords;
    }

    private void printFrequency(final Map<Article, List<String>> allKeywords){
        final StringBuilder sb = new StringBuilder();

        for (final Article article : allKeywords.keySet()) {
            final List<String> keywords = allKeywords.get(article);
            final Set<String> keywordSet = new HashSet<>(keywords);
            final List<Word> words = new ArrayList<>();

            for (final String keyword : keywordSet) {
                final double tf = tf(keyword, keywords);
                final double idf = idf(keyword, allKeywords);

                words.add(new Word(keyword, tf * idf));
            }

            Collections.sort(words, Collections.reverseOrder());
            sb.append(article.getId() + " - " + article.getTitle() + "\n");
            for (Word word : words) {
                sb.append(String.format("  %50s %.5f\n", word.getValue(), word.getScore()));
            }
        }

        try {
            System.out.println(sb.toString());
            fileService.put("keywords/keywords.txt", new ByteArrayInputStream(sb.toString().getBytes()));
        } catch (IOException e){
            // Nothing
        }

    }

    private double tf(final String keyword, final List<String> keywords) {
        double count = 0;
        for (final String kw : keywords) {
            if (kw.equals(keyword)) {
                count++;
            }
        }
        return count / keywords.size();
    }

    private double idf(final String keyword, final Map<Article, List<String>> allKeywords) {
        double count = 0;
        for (final List<String> keywords : allKeywords.values()) {
            if (keywords.contains(keyword)) {
                count++;
            }
        }
        return Math.log(allKeywords.size()/count);
    }

    public static class Word implements Comparable<Word> {
        private final String value;
        private final double score;

        public Word(final String value, final double score) {
            this.value = value;
            this.score = score;
        }

        public String getValue() {
            return value;
        }

        public double getScore() {
            return score;
        }

        @Override
        public int compareTo(final Word o) {
            return (int)(1000000*(score-o.score));
        }
    }
}
