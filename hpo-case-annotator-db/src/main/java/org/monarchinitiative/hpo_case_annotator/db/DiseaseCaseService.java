package org.monarchinitiative.hpo_case_annotator.db;

import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;

import java.util.Collection;
import java.util.Collections;

public class DiseaseCaseService {

    private final DiseaseCaseDao dao;

    public DiseaseCaseService(DiseaseCaseDao dao) {
        this.dao = dao;
    }

    public Collection<DiseaseCase> getAllData() {
        return Collections.emptyList();
    }

    public boolean persistData(Collection<DiseaseCase> data) {

        return false;
    }

}
