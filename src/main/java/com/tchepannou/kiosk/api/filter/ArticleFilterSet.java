package com.tchepannou.kiosk.api.filter;

import com.tchepannou.kiosk.api.domain.Article;
import com.tchepannou.kiosk.core.filter.Filter;
import com.tchepannou.kiosk.core.filter.FilterSet;

import java.util.List;

public class ArticleFilterSet extends FilterSet<Article>{
    public ArticleFilterSet(final List<Filter<Article>> filters) {
        super(filters);
    }
}
