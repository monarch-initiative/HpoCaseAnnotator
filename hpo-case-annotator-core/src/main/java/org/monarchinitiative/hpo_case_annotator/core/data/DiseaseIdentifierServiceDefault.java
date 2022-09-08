package org.monarchinitiative.hpo_case_annotator.core.data;

import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseIdentifier;

import java.util.List;
import java.util.stream.Stream;

// TODO - override the default methods if performance is an issue
class DiseaseIdentifierServiceDefault implements DiseaseIdentifierService {

    private final List<DiseaseIdentifier> diseaseIdentifiers;

    DiseaseIdentifierServiceDefault(List<DiseaseIdentifier> diseaseIdentifiers) {
        this.diseaseIdentifiers = List.copyOf(diseaseIdentifiers);
    }

    @Override
    public Stream<DiseaseIdentifier> diseaseIdentifiers() {
        return diseaseIdentifiers.stream();
    }
}
