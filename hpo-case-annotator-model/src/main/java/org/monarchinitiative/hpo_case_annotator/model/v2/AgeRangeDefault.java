package org.monarchinitiative.hpo_case_annotator.model.v2;

public record AgeRangeDefault(Age onset, Age resolution) implements AgeRange {
    @Override
    public Age getOnset() {
        return onset;
    }

    @Override
    public Age getResolution() {
        return resolution;
    }

}
