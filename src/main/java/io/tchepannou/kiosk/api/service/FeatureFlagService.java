package io.tchepannou.kiosk.api.service;

public class FeatureFlagService {
    private boolean videoEnabled;

    public boolean isVideoEnabled() {
        return videoEnabled;
    }

    public void setVideoEnabled(final boolean videoEnabled) {
        this.videoEnabled = videoEnabled;
    }
}
