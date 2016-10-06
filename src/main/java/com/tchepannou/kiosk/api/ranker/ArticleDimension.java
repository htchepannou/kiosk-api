package com.tchepannou.kiosk.api.ranker;

import com.tchepannou.kiosk.core.ranker.RankDimension;

import java.util.Comparator;

public class ArticleDimension implements RankDimension<Rankable> {
    private final String name;
    private final float weight;
    private final Comparator<Rankable> comparator;

    public ArticleDimension(final String name, final float weight, final Comparator<Rankable> comparator) {
        this.name = name;
        this.weight = weight;
        this.comparator = comparator;
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public Comparator<Rankable> getComparator() {
        return comparator;
    }

    public String getName() {
        return name;
    }
}
