package org.monarchinitiative.hpo_case_annotator.forms.individual;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.monarchinitiative.hpo_case_annotator.forms.component.BaseIndividualIdsDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.component.PedigreeMemberIdsComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.PedigreeMemberIdsDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.disease.BaseDiseaseDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.disease.PedigreeMemberDiseaseDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PedigreeMemberPhenotypeDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.BasePhenotypeDataEdit;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

public class PedigreeMember extends BaseIndividual<ObservablePedigreeMember> {

    @FXML
    private StackPane credentialPane;
    @FXML
    private PedigreeMemberIdsComponent pedigreeMemberIdentifiers;
    @FXML
    private Button editIdentifiersButton;

    public PedigreeMember() {
        super(PedigreeMember.class.getResource("PedigreeMember.fxml"));
    }

    @Override
    protected void initialize() {
        super.initialize();
        pedigreeMemberIdentifiers.dataProperty().bind(data);
        editIdentifiersButton.visibleProperty().bind(credentialPane.hoverProperty());
    }

    @Override
    protected BaseIndividualIdsDataEdit<ObservablePedigreeMember> getIdsDataEdit() {
        return new PedigreeMemberIdsDataEdit();
    }

    @Override
    protected BasePhenotypeDataEdit<ObservablePedigreeMember> getPhenotypeDataEdit() {
        return new PedigreeMemberPhenotypeDataEdit();
    }

    @Override
    protected BaseDiseaseDataEdit<ObservablePedigreeMember> getDiseaseDataEdit() {
        return new PedigreeMemberDiseaseDataEdit();
    }
}
