package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface Pedigree {

    static Pedigree of(Collection<PedigreeMember> members) {
        if (members.isEmpty())
            throw new IllegalArgumentException("Pedigree cannot be empty");

        // check member ID is present only once
        Map<String, Long> idCounts = members.stream()
                .collect(Collectors.groupingBy(Individual::getId, Collectors.counting()));
        for (Map.Entry<String, Long> entry : idCounts.entrySet()) {
            if (entry.getValue() > 1)
                throw new IllegalArgumentException("Pedigree members IDs must be present only once, but `" + entry.getKey() + "` was present " + entry.getValue() + " times");
        }

        return new PedigreeDefault(List.copyOf(members));
    }

    List<? extends PedigreeMember> getMembers();

}
