package com.tchepannou.kiosk.api.pipeline;

public interface PipelineConstants {
    String EVENT_CREATE_ARTICLE = "CREATE_ARTICLE";
    String EVENT_EXTRACT_CONTENT = "EXTRACT_CONTENT";
    String EVENT_EXTRACT_IMAGE = "EXTRACT_IMAGE";
    String EVENT_EXTRACT_LANGUAGE = "EXTRACT_LANGUAGE";
    String EVENT_VALIDATE = "VALIDATE";
    String EVENT_EXTRACT_KEYWORDS = "EXTRACT_KEYWORDS";
    String EVENT_RANK = "RANK";
    String EVENT_END = "END";
}
