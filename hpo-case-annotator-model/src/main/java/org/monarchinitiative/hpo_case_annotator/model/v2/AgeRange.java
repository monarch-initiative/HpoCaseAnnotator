package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.time.Period;

public interface AgeRange {

    /**
     * @param age age of the individual
     * @return range to represent a point on the timeline including given <code>age</code>
     */
    static AgeRange point(Period age) {
        return new AgeRangeSingular(age);
    }

    /**
     * @param reportedAge reported age of the individual
     * @return range to represent timespan starting at birth and ending at the <code>reportedAge</code>
     */
    static AgeRange sinceBirthUntilAge(Period reportedAge) {
        return of(Period.ZERO, reportedAge);
    }

    /**
     * @param startAge start age (inclusive)
     * @param endAge   end age (exclusive)
     * @return range to represent timespan starting at <code>startAge</code> and ending at <code>endAge</code>
     * @throws IllegalArgumentException if <code>endAge</code> is after <code>startAge</code>
     */
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

    default Period length() {
        return endAge().minus(startAge()).normalized();
    }
}
