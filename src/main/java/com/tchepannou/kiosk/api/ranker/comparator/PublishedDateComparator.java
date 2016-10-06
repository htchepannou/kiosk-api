package com.tchepannou.kiosk.api.ranker.comparator;

import com.tchepannou.kiosk.api.ranker.Rankable;

import java.util.Comparator;

public class PublishedDateComparator implements Comparator<Rankable>{
    @Override
    public int compare(final Rankable o1, final Rankable o2) {
        final int s1 = (int)o1.getPublishedDate().getTime();
        final int s2 = (int)o2.getPublishedDate().getTime();
        return (s1-s2)/1000;
    }
}
