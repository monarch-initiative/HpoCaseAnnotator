package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

class PedigreeDefault implements Pedigree {

    private final Set<PedigreeMember> members;

    PedigreeDefault(Set<PedigreeMember> members) {
        this.members = members;
    }

    @Override
    public Stream<PedigreeMember> members() {
        return members.stream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PedigreeDefault that = (PedigreeDefault) o;
        return Objects.equals(members, that.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(members);
    }

    @Override
    public String toString() {
        return "PedigreeDefault{" +
                "members=" + members +
                '}';
    }
}
