package com.tchepannou.kiosk.api.exception;

public class ArticleNotFoundException extends ArticleException {
    public ArticleNotFoundException(final String message) {
        super(message);
    }
}
