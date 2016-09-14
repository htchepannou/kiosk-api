package com.tchepannou.kiosk.api.exception;

public class ArticleException extends RuntimeException {
    public ArticleException(final String message) {
        super(message);
    }

    public ArticleException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
