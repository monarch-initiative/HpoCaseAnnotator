package org.monarchinitiative.hpo_case_annotator.app;

import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseIdentifier;

import java.util.stream.Stream;

public class DiseaseIdentifierServiceImpl implements DiseaseIdentifierService {

    private final ObservableList<DiseaseIdentifier> diseaseIdentifiers;

    public DiseaseIdentifierServiceImpl(ObservableList<DiseaseIdentifier> diseaseIdentifiers) {
        this.diseaseIdentifiers = diseaseIdentifiers;
    }

    // TODO - override the default methods if performance is an issue

    @Override
    public Stream<DiseaseIdentifier> diseaseIdentifiers() {
        return diseaseIdentifiers.stream();
    }
}
