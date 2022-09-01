package org.monarchinitiative.hpo_case_annotator.forms.individual;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.layout.StackPane;
import org.monarchinitiative.hpo_case_annotator.forms.component.PedigreeMemberIdsComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.PedigreeMemberIdsEditableComponent;
import org.monarchinitiative.hpo_case_annotator.forms.disease.DiseaseDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.disease.PedigreeMemberDiseaseDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PedigreeMemberPhenotypeDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypeDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.forms.util.Utils;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

import static javafx.beans.binding.Bindings.concat;

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

    @FXML
    protected void editIdentifiersAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(getParent().getScene().getWindow());
        dialog.titleProperty().bind(concat("Edit identifiers for ", Utils.nullableStringProperty(data, "id")));
        PedigreeMemberIdsEditableComponent component = new PedigreeMemberIdsEditableComponent();
        component.setInitialData(data.getValue());

        dialog.getDialogPane().setContent(component);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.UPDATE_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));

        dialog.showAndWait()
                .ifPresent(shouldUpdate -> {
                    if (shouldUpdate) component.commit();
                });

        e.consume();
    }

    @Override
    protected PhenotypeDataEdit<ObservablePedigreeMember> getPhenotypeDataEdit() {
        return new PedigreeMemberPhenotypeDataEdit();
    }

    @Override
    protected DiseaseDataEdit<ObservablePedigreeMember> getDiseaseDataEdit() {
        return new PedigreeMemberDiseaseDataEdit();
    }
}
