package com.tchepannou.kiosk.api.service;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.api.jpa.ArticleRepository;
import com.tchepannou.kiosk.api.mapper.ArticleMapper;
import com.tchepannou.kiosk.client.dto.ArticleDto;
import com.tchepannou.kiosk.client.dto.ErrorConstants;
import com.tchepannou.kiosk.client.dto.ErrorDto;
import com.tchepannou.kiosk.client.dto.GetArticleResponse;
import com.tchepannou.kiosk.core.service.LogService;
import com.tchepannou.kiosk.core.service.TransactionIdProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;

public class ArticleService {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ContentRepositoryService contentRepository;

    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    TransactionIdProvider transactionIdProvider;

    @Autowired
    LogService logService;


    public GetArticleResponse get (final String id) {
        /* article */
        final Article article = articleRepository.findOne(id);
        if (article == null) {
            return createResponse(null, ErrorConstants.ARTICLE_NOT_FOUND);
        }
        final ArticleDto dto = articleMapper.toArticleDto(article);

        /* Get the content */
        String html = fetchContent(article);
        if (html == null){
            return createResponse(null, ErrorConstants.CONTENT_NOT_FOUND);
        }
        dto.getData().setContent(html);

        /* result */
        return createResponse(dto, null);
    }

    private String fetchContent(final Article article) {
        try {

            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final String key = article.contentKey(article.getStatus());
            contentRepository.read(key, out);
            return out.toString();

        } catch (ContentRepositoryException e){
            logService.add("Exception", e.getClass().getName());
            logService.add("ExceptionMessage", e.getMessage());
            return null;
        }
    }


    private GetArticleResponse createResponse(final ArticleDto article, final String code){
        final GetArticleResponse response = new GetArticleResponse();
        response.setArticle(article);
        response.setTransactionId(transactionIdProvider.get());
        if (code != null){
            response.setError(new ErrorDto(code));
        }
        return response;
    }
}
