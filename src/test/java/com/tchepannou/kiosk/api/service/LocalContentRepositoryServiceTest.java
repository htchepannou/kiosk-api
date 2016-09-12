package com.tchepannou.kiosk.api.service;

import com.google.common.io.Files;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalContentRepositoryServiceTest {

    @Test
    public void testWrite() throws Exception {
        // Given
        final File home = Files.createTempDir();
        final ContentRepositoryService repo = new LocalContentRepositoryService(home);

        final InputStream in = new ByteArrayInputStream("hello world".getBytes());

        // When
        repo.write("foo/bar.txt", in);

        // Then
        final File fout = new File(home.getAbsolutePath() + "/foo/bar.txt");
        assertThat(fout).exists();
        assertThat(fout).hasContent("hello world");
    }
}
