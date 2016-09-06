package com.tchepannou.kiosk.api.service;

import java.io.IOException;
import java.io.InputStream;

public interface ContentRepositoryService {
    void write(final String key, final InputStream content) throws IOException;
}
