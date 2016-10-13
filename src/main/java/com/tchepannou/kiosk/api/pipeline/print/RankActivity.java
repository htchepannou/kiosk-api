package com.tchepannou.kiosk.api.pipeline.print;

import com.tchepannou.kiosk.api.pipeline.Activity;
import com.tchepannou.kiosk.api.pipeline.Event;
import com.tchepannou.kiosk.api.pipeline.support.ArticleSet;

public class RankActivity extends Activity{
    @Override
    protected String getTopic() {
        return null;
    }

    @Override
    protected void doHandleEvent(final Event event) {
        ArticleSet articleSet = (ArticleSet)event.getPayload();

    }
}
