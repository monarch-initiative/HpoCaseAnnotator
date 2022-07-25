package org.monarchinitiative.hpo_case_annotator.model.v2;

record AgeRangeSingular(Age onset) implements AgeRange {

    @Override
    public Age getOnset() {
        return onset;
    }

    @Override
    public Age getResolution() {
        return onset;
    }

}
