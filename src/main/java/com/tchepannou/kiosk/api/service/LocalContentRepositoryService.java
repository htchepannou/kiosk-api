package com.tchepannou.kiosk.api.service;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LocalContentRepositoryService implements ContentRepositoryService {
    private final File home;

    public LocalContentRepositoryService(final File directory) {
        this.home = directory;
    }

    @Override
    public void write(final String key, final InputStream content) throws IOException {
        final File file = new File(home.getAbsolutePath() + "/" + key);
        final File dir = file.getParentFile();
        if (!dir.exists()){
            dir.mkdirs();
        }

        try (OutputStream out = new FileOutputStream(file)){
            IOUtils.copy(content, out);
        }
    }
}
