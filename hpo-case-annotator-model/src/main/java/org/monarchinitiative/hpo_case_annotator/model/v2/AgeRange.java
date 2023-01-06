package org.monarchinitiative.hpo_case_annotator.model.v2;

public interface AgeRange {

    /**
     * @param age age of the individual
     * @return range to represent a point on the timeline including given <code>age</code>
     */
    static AgeRange point(Age age) {
        return new AgeRangeSingular(age);
    }

    /**
     * @param startAge start age (inclusive)
     * @param endAge   end age (exclusive)
     * @return range to represent timespan starting at <code>startAge</code> and ending at <code>endAge</code>
     * @throws IllegalArgumentException if <code>endAge</code> is after <code>startAge</code>
     */
    static AgeRange of(Age startAge, Age endAge) {
        if (startAge.equals(endAge))
            return point(startAge);

        return new AgeRangeDefault(startAge, endAge);
    }

    /**
     * @return onset age (inclusive)
     */
    Age getStart();

    /**
     * @return resolution age (exclusive)
     */
    Age getEnd();

}
