package org.monarchinitiative.hpo_case_annotator.forms.individual;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.monarchinitiative.hpo_case_annotator.forms.component.BaseIndividualIdsEditableComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.IndividualIdsComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.IndividualIdsEditableComponent;
import org.monarchinitiative.hpo_case_annotator.forms.disease.DiseaseDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.disease.IndividualDiseaseDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.IndividualPhenotypeDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypeDataEdit;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;

public class Individual extends BaseIndividual<ObservableIndividual> {

    @FXML
    private StackPane credentialPane;
    @FXML
    private IndividualIdsComponent individualIds;
    @FXML
    private Button editIdentifiersButton;

    public Individual() {
        super(Individual.class.getResource("Individual.fxml"));
    }

    @FXML
    @Override
    protected void initialize() {
        super.initialize();
        individualIds.dataProperty().bind(data);
        editIdentifiersButton.visibleProperty().bind(credentialPane.hoverProperty());
    }

    @Override
    protected BaseIndividualIdsEditableComponent<ObservableIndividual> getIdsEditableComponent() {
        return new IndividualIdsEditableComponent();
    }

    @Override
    protected PhenotypeDataEdit<ObservableIndividual> getPhenotypeDataEdit() {
        return new IndividualPhenotypeDataEdit();
    }

    @Override
    protected DiseaseDataEdit<ObservableIndividual> getDiseaseDataEdit() {
        return new IndividualDiseaseDataEdit();
    }
}
