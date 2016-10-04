package com.tchepannou.kiosk.api.ranker.comparator;

import com.tchepannou.kiosk.api.ranker.Rankable;

import java.util.Comparator;

public class ImageComparator implements Comparator<Rankable> {
    @Override
    public int compare(final Rankable o1, final Rankable o2) {
        final int img1 = o1.isWithImage() ? 1 : 0;
        final int img2 = o2.isWithImage() ? 1 : 0;
        return img1 - img2;
    }
}
