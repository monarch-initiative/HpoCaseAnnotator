package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.time.Period;

public interface AgeRange {

    static AgeRange point(Period age) {
        return new AgeRangeSingular(age);
    }

    static AgeRange of(Period startAge, Period endAge) {
        if (startAge.getYears() == endAge.getYears()
                && startAge.getMonths() == endAge.getMonths()
                && startAge.getDays() == endAge.getDays())
            return point(startAge);

        if (startAge.getYears() < endAge.getYears()
                || startAge.getMonths() < endAge.getMonths()
                || startAge.getDays() < endAge.getDays())
            return new AgeRangeDefault(startAge, endAge);

        throw new IllegalArgumentException("End age must be after start! Start: " + startAge + ", End: " + endAge);
    }

    /**
     * @return start age (inclusive)
     */
    Period startAge();

    /**
     * @return end age (exclusive)
     */
    Period endAge();

}
