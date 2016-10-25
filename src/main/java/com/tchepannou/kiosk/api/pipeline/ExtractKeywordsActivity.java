package com.tchepannou.kiosk.api.pipeline;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.core.service.FileService;
import com.tchepannou.kiosk.core.text.Stemmer;
import com.tchepannou.kiosk.core.text.StopWord;
import com.tchepannou.kiosk.core.text.TextKit;
import com.tchepannou.kiosk.core.text.TextKitProvider;
import com.tchepannou.kiosk.core.text.Tokenizer;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ExtractKeywordsActivity extends Activity {
    @Autowired
    FileService fileService;

    @Autowired
    TextKitProvider textKitProvider;

    @Override
    protected String getTopic() {
        return PipelineConstants.EVENT_EXTRACT_KEYWORDS;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        final Article article = (Article) event.getPayload();
        Throwable ex = null;
        try {
            if (Article.Status.processed.equals(article.getStatus())){
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final String url = article.contentKey(article.getStatus());
                fileService.get(url, out);

                final Collection<Word> words = extract(article, out.toString("utf-8"));
                output(article, words);
            }

            publishEvent(new Event(PipelineConstants.EVENT_END, article));
        } catch (Exception e){
            ex = e;
        } finally {
            log(article, ex);
        }
    }

    private void output(Article article, Collection<Word> words) throws IOException {
        final int total = totalWords(words);
        final StringBuilder sb = new StringBuilder();
        for (Word word: words){
            double frequency = (double)word.getCount() / (double)total;
            sb.append(word.getText() + "," + word.getCount() + "," + frequency + "\n");
        }

        final String key = article.keywordKey();
        final ByteArrayInputStream in = new ByteArrayInputStream(sb.toString().getBytes("utf-8"));
        fileService.put(key, in);
    }

    private Collection<Word> extract(final Article article, final String content) {
        final TextKit kit = textKitProvider.get(article.getLanguageCode());
        final String title = article.getTitle();
        final String text = Jsoup.parse(content).text();

        final Tokenizer tokenizer = kit.getTokenizer(title + "\n" + text);
        final Map<String, Word> words = new HashMap<>();
        final Stemmer stemmer = kit.getStemmer();
        final StopWord stopWord = kit.getStopWord();
        while (true) {
            final String token = tokenizer.nextToken();
            if (token == null) {
                break;
            } else if (token.length() <= 1 || stopWord.is(token)){
                continue;
            }

            final String stem = stemmer.stem(token);
            Word word = words.get(stem);
            if (word == null){
                word = new Word(token);
                words.put(token, word);
            }
            word.incr();
        }
        return words.values();
    }

    private int totalWords(Collection<Word> words){
        int total = 0;
        for (Word word : words){
            total += word.getCount();
        }
        return total;
    }

    private void log (final Article article, final Throwable ex){
        addToLog(article);
        addToLog(ex);
    }

    private static class Word{
        private String text;
        private int count;

        public Word(final String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public int getCount() {
            return count;
        }

        public void incr (){
            count++;
        }
    }
}
