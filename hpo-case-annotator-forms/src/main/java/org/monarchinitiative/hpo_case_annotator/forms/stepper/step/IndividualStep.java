package org.monarchinitiative.hpo_case_annotator.forms.stepper.step;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.stage.StageStyle;
import org.monarchinitiative.hpo_case_annotator.forms.component.IndividualIdsBindingComponent;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypeDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.BaseStep;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividualStudy;
import org.monarchinitiative.phenol.ontology.data.Ontology;

public class IndividualStep<T extends ObservableIndividualStudy> extends BaseStep<T> {

    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();

    @FXML
    private IndividualIdsBindingComponent individualIds;

    public IndividualStep() {
        super(IndividualStep.class.getResource("IndividualStep.fxml"));
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    public Parent getContent() {
        return this;
    }

    @Override
    protected void bind(T data) {
        individualIds.dataProperty().bindBidirectional(data.individualProperty());
    }

    @Override
    protected void unbind(T data) {
        individualIds.dataProperty().unbindBidirectional(data.individualProperty());
    }

    public Ontology getHpo() {
        return hpo.get();
    }

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
    }

    public void setHpo(Ontology hpo) {
        this.hpo.set(hpo);
    }

    @FXML
    private void addEditPhenotypesAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(individualIds.getParent().getScene().getWindow());
        dialog.initStyle(StageStyle.DECORATED);
        // TODO - setup title
        dialog.setResizable(true);

        PhenotypeDataEdit phenotypeDataEdit = new PhenotypeDataEdit();
        phenotypeDataEdit.hpoProperty().bind(hpo);
        phenotypeDataEdit.setInitialData(data.get().getIndividual()); // TODO - check non null?
        dialog.getDialogPane().setContent(phenotypeDataEdit);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.OK_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));
        dialog.showAndWait()
                .ifPresent(shouldCommit -> {
                    if (shouldCommit) phenotypeDataEdit.commit();
                });
        e.consume();
    }

    @FXML
    private void addEditDiseasesAction(ActionEvent e) {
        // TODO - implement
        e.consume();
    }

    @FXML
    private void addEditGenotypesAction(ActionEvent e) {
        // TODO - implement
        e.consume();
    }
}
