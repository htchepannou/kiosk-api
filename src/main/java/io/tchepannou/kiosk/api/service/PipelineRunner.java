package io.tchepannou.kiosk.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class PipelineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineRunner.class);
    private String accessToken;
    private String url;


    @Scheduled(cron = "0 0 * * * *")
    public void run() throws IOException {
        final HttpsURLConnection con = (HttpsURLConnection) new URL(url).openConnection();
        try {
            LOGGER.info("POST url");
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Travis-API-Version", "3");
            con.setRequestProperty("Authorization", "token " + accessToken);

            // Write
            con.setDoOutput(true);
            try (final DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                final String json = "{\"request\":{\"branch\":\"master\"}}";
                wr.writeBytes(json);
                wr.flush();
            }

            // Response
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    LOGGER.info(inputLine);
                }
            }

        } finally {
            con.disconnect();
        }
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
}
