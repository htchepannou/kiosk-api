package com.tchepannou.kiosk.api.controller;

import com.tchepannou.kiosk.api.Starter;
import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.core.service.ContentRepositoryService;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.jayway.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@Sql({"/sql/clean.sql", "/sql/ArticleProcess.sql"})
public class ArticleProcessIT extends RestAssuredSupport {

    @Autowired
    ContentRepositoryService contentRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Test
    public void shouldPublishAnArticle() throws Exception {
        createContent("100", "/html/article100.html");
        createContent("200", "/html/article200.html");

        // @formatter:off
        when()
                .get("/kiosk/v1/articles/process")
        .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
        ;

        // @formatter:on
//        Thread.sleep(5000);

        assertProcessed("100");
        assertProcessed("200");
    }


    private void assertProcessed (final String id){
        final Article article = articleRepository.findOne(id);
        assertThat(article.getStatus()).isEqualTo(Article.Status.processed);

        final OutputStream out = new ByteArrayOutputStream();
        contentRepository.read(article.contentKey(article.getStatus()), out);
        assertThat(out.toString()).isNotEmpty();
    }

    private void createContent(final String id, final String path) throws IOException{
        final Article article = new Article();
        article.setId(id);
        article.setStatus(Article.Status.submitted);

        try (final InputStream in = getClass().getResourceAsStream(path)) {
            contentRepository.write(article.contentKey(article.getStatus()), in);
        }
    }
}
