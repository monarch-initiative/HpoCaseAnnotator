package org.monarchinitiative.hpo_case_annotator.forms.mining;

import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicFeature;

public interface ReviewedPhenotypicFeature extends PhenotypicFeature {

    ReviewStatus getReviewStatus();

}
