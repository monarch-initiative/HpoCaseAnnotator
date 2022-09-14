package org.monarchinitiative.hpo_case_annotator.forms;

import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseIdentifier;

import java.util.List;
import java.util.stream.Stream;

public class TestDiseaseIdentifierService implements DiseaseIdentifierService {

    private final List<DiseaseIdentifier> diseaseIdentifiers;

    public TestDiseaseIdentifierService(List<DiseaseIdentifier> diseaseIdentifiers) {
        this.diseaseIdentifiers = diseaseIdentifiers;
    }

    @Override
    public Stream<DiseaseIdentifier> diseaseIdentifiers() {
        return diseaseIdentifiers.stream();
    }
}
