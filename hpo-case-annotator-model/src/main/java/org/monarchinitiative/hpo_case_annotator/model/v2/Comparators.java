package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.time.Period;
import java.util.Comparator;

public final class Comparators {

    private static final Comparator<Period> PERIOD_COMPARATOR = Comparator.comparingInt(Period::getYears)
            .thenComparingInt(Period::getMonths)
            .thenComparingInt(Period::getDays);

    private Comparators() {/* static utility class */}

    public static Comparator<Period> periodComparator() {
        return PERIOD_COMPARATOR;
    }
}
