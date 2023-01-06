package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.util.List;

record PedigreeDefault(List<PedigreeMember> members) implements Pedigree {

    @Override
    public List<? extends PedigreeMember> getMembers() {
        return members;
    }
}
