package com.galenframework.tests.speclang2;

import com.galenframework.speclang2.AlphanumericComparator;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AlphanumericComparatorTest {


    @Test
    public void shouldSortProperly() {
        List<String> list = asList(
                "abc 123 edf2",
                "abc 123 edf",
                "abc 2 edf",
                "abc 13 edf",
                "abc 12 edf",
                "abd 2 edf",
                "abb 2 edf"
                );

        Collections.sort(list, new AlphanumericComparator());

        assertThat(list, is(asList(
                "abb 2 edf",
                "abc 2 edf",
                "abc 12 edf",
                "abc 13 edf",
                "abc 123 edf",
                "abc 123 edf2",
                "abd 2 edf"
        )));
    }
}
