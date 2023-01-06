package org.monarchinitiative.hpo_case_annotator.core.data;

import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseIdentifier;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface DiseaseIdentifierService {

    static DiseaseIdentifierService of(List<DiseaseIdentifier> diseaseIdentifiers) {
        return new DiseaseIdentifierServiceDefault(diseaseIdentifiers);
    }

    Stream<DiseaseIdentifier> diseaseIdentifiers();

    default List<String> diseaseIds() {
        return diseaseIdentifiers()
                .map(DiseaseIdentifier::id)
                .map(TermId::getValue)
                .distinct()
                .toList();
    }

    default List<String> diseaseNames() {
        return diseaseIdentifiers()
                .map(DiseaseIdentifier::getDiseaseName)
                .distinct()
                .toList();
    }

    default Optional<DiseaseIdentifier> diseaseIdentifierForDiseaseId(TermId diseaseId) {
        return diseaseIdentifiers()
                .filter(di -> di.id().equals(diseaseId))
                .findAny();
    }

    default Optional<DiseaseIdentifier> diseaseIdentifierForDiseaseName(String name) {
        return diseaseIdentifiers()
                .filter(di -> di.getDiseaseName().equals(name))
                .findAny();
    }
}
