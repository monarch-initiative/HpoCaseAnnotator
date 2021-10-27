package org.monarchinitiative.hpo_case_annotator.forms.util;

import java.util.List;
import java.util.stream.Stream;

public class FormsUtils {

    private FormsUtils(){}

    public static List<Integer> getIntegers(int endInclusive) {
        return Stream.iterate(0, i -> i <= endInclusive, i -> i + 1).toList();
    }
}
