package io.tchepannou.kiosk.api.model;

public class EventModel {
    private String name;
    private String page;
    private long articleId;
    private long timestamp;
    private String param1;
    private String param2;
    private DeviceModel device;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPage() {
        return page;
    }

    public void setPage(final String page) {
        this.page = page;
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(final long articleId) {
        this.articleId = articleId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(final String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(final String param2) {
        this.param2 = param2;
    }

    public DeviceModel getDevice() {
        return device;
    }

    public void setDevice(final DeviceModel device) {
        this.device = device;
    }
}
