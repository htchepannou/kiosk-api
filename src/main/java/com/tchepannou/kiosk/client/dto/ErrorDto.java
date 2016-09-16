package com.tchepannou.kiosk.client.dto;

public class ErrorDto {
    private final String code;
    private final String message;

    public ErrorDto(final String code) {
        this(code, null);
    }
    public ErrorDto(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
