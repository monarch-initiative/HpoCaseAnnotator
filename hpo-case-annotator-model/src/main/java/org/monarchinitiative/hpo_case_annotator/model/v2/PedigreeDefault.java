package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.util.Set;

record PedigreeDefault(Set<PedigreeMember> members) implements Pedigree {
}
