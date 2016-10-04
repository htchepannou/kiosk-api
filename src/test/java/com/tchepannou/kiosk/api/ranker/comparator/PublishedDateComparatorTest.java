package com.tchepannou.kiosk.api.ranker.comparator;

import com.tchepannou.kiosk.api.ranker.Rankable;
import com.tchepannou.kiosk.core.service.TimeService;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class PublishedDateComparatorTest {

    @Test
    public void testCompare() throws Exception {
        // Given
        final TimeService ts = new TimeService();
        final Rankable r1 = createRankable(ts.now());
        final Rankable r2 = createRankable(DateUtils.addHours(r1.getPublishedDate(), 1));

        // when
        final int result = new PublishedDateComparator().compare(r1, r2);

        // Then
        assertThat(result).isEqualTo(-3600);
    }

    private Rankable createRankable(final Date date){
        final Rankable r = new Rankable();
        r.setPublishedDate(date);
        return r;
    }
}
