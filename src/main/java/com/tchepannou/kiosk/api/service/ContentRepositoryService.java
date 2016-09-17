package com.tchepannou.kiosk.api.service;

import java.io.InputStream;
import java.io.OutputStream;

public interface ContentRepositoryService {
    void write(final String key, final InputStream content) throws ContentRepositoryException;
    void read(final String key, final OutputStream out) throws ContentRepositoryException;
}
