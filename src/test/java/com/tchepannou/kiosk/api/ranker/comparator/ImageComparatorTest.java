package com.tchepannou.kiosk.api.ranker.comparator;

import com.tchepannou.kiosk.api.ranker.Rankable;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ImageComparatorTest {

    @Test
    public void testCompare() throws Exception {
        // Given
        final Rankable r1 = createRankable(true);
        final Rankable r2 = createRankable(false);

        // when
        final int result = new ImageComparator().compare(r1, r2);

        // Then
        assertThat(result).isEqualTo(1);

    }

    private Rankable createRankable(final boolean withImage){
        final Rankable r = new Rankable();
        r.setWithImage(withImage);
        return r;
    }

}
