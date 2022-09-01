package org.monarchinitiative.hpo_case_annotator.forms.individual;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.layout.StackPane;
import org.monarchinitiative.hpo_case_annotator.forms.component.IndividualIdsBindingComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.IndividualIdsComponent;
import org.monarchinitiative.hpo_case_annotator.forms.disease.DiseaseDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.disease.IndividualDiseaseDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.IndividualPhenotypeDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypeDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.forms.util.Utils;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;

import static javafx.beans.binding.Bindings.concat;

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

    @FXML
    @Override
    protected void editIdentifiersAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(getParent().getScene().getWindow());
        dialog.titleProperty().bind(concat("Edit identifiers for ", Utils.nullableStringProperty(data, "id")));
        IndividualIdsBindingComponent component = new IndividualIdsBindingComponent();
        component.setData(data.getValue());

        dialog.getDialogPane().setContent(component);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.UPDATE_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));

        dialog.showAndWait()
//                .ifPresent(shouldUpdate -> {
//                    if (shouldUpdate) component.commit();
//                })
        ;

        e.consume();
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
