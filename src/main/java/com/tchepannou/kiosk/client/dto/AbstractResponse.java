package com.tchepannou.kiosk.client.dto;

public abstract class AbstractResponse {
    private String transactionId;
    private ErrorDto error;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isSuccess() {
        return error == null;
    }

    public ErrorDto getError() {
        return error;
    }

    public void setError(final ErrorDto error) {
        this.error = error;
    }
}
