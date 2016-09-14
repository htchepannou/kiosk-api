package com.tchepannou.kiosk.api.exception;

public class ContentNotFoundException extends ArticleException {
    public ContentNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
