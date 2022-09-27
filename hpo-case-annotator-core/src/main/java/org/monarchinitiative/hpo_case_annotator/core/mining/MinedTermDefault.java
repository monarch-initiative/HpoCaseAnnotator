package org.monarchinitiative.hpo_case_annotator.core.mining;

import org.monarchinitiative.phenol.ontology.data.TermId;

record MinedTermDefault(TermId id, int start, int end, boolean isNegated) implements MinedTerm {
}
