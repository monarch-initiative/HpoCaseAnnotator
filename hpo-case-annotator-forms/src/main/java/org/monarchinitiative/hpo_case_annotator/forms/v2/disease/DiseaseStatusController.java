package org.monarchinitiative.hpo_case_annotator.forms.v2.disease;


import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.BindingObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.util.Utils;
import org.monarchinitiative.hpo_case_annotator.forms.v2.DiseaseTableController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.IndividualDetailController;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseIdentifier;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableDiseaseIdentifier;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableDiseaseStatus;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Consumer;


public class DiseaseStatusController<T extends BaseObservableIndividual> extends BindingObservableDataController<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseStatusController.class);
    private final ObjectProperty<T> individual = new SimpleObjectProperty<>(this, "individual");

    private final DiseaseIdentifierService diseaseIdentifierService;

    // ADD DISEASE
    @FXML
    private TextField diseaseIdTextField;
    @FXML
    private TextField diseaseNameTextField;
    @FXML
    private Button addDiseaseButton;

    // Individual ID & present/absent diseases
    @FXML
    private HBox individualDetail;
    @FXML
    private IndividualDetailController individualDetailController;
    @FXML
    private VBox diseaseTable;
    @FXML
    private DiseaseTableController diseaseTableController;

    @FXML
    private Button removeDiseaseButton;

    public DiseaseStatusController(DiseaseIdentifierService diseaseIdentifierService) {
        this.diseaseIdentifierService = diseaseIdentifierService;
    }

    @FXML
    protected void initialize() {
        super.initialize();
        initializeIndividualAndDiseaseTableView();
        initializeAutocompletion();
    }

    private void initializeIndividualAndDiseaseTableView() {
        removeDiseaseButton.disableProperty().bind(diseaseTableController.selectionModel().selectedItemProperty().isNull());
    }

    private void initializeAutocompletion() {
        TextFields.bindAutoCompletion(diseaseIdTextField, diseaseIdentifierService.diseaseIds())
                .setVisibleRowCount(10);
        TextFields.bindAutoCompletion(diseaseNameTextField, diseaseIdentifierService.diseaseNames())
                .setVisibleRowCount(10);
    }

    @Override
    protected void bind(T individual) {
        individualDetailController.idProperty().bind(individual.idProperty());
        individualDetailController.sexProperty().bind(individual.sexProperty().asString());
        // TODO - fix or discard
//        individualDetailController.ageLabel().bind(individual.getObservableAge().period().asString());
        Bindings.bindContentBidirectional(diseaseTableController.diseaseStatuses(), individual.diseaseStatesProperty());
    }

    @Override
    protected void unbind(T individual) {
        individualDetailController.idProperty().unbind();
        individualDetailController.sexProperty().unbind();
        individualDetailController.ageLabel().unbind();
        Bindings.unbindContentBidirectional(diseaseTableController.diseaseStatuses(), individual.diseaseStatesProperty());
    }

    @FXML
    private void addDisease(ActionEvent e) {
        if (e.getSource().equals(diseaseIdTextField)) {
            getDiseaseIdentifierFromDiseaseId()
                    .ifPresent(addDiseaseIdentifier());
        } else if (e.getSource().equals(diseaseNameTextField)) {
            getDiseaseIdentifierFromDiseaseName()
                    .ifPresent(addDiseaseIdentifier());
        } else if (e.getSource().equals(addDiseaseButton)) {
            getDiseaseIdentifierFromDiseaseId()
                    .or(this::getDiseaseIdentifierFromDiseaseName)
                    .ifPresent(addDiseaseIdentifier());
        } else {
            LOGGER.warn("Unknown action event source when adding disease: {}", e);
            return;
        }

        diseaseIdTextField.clear();
        diseaseNameTextField.clear();
        e.consume();
    }

    private Optional<DiseaseIdentifier> getDiseaseIdentifierFromDiseaseId() {
        String text = diseaseIdTextField.getText();
        if (text.isBlank()) return Optional.empty();
        try {
            TermId diseaseId = TermId.of(text);
            return diseaseIdentifierService.diseaseIdentifierForDiseaseId(diseaseId);
        } catch (Exception ex) {
            LOGGER.warn("Unparsable disease ID: {}", ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    private Optional<DiseaseIdentifier> getDiseaseIdentifierFromDiseaseName() {
        return diseaseIdentifierService.diseaseIdentifierForDiseaseName(diseaseNameTextField.getText());
    }

    private Consumer<DiseaseIdentifier> addDiseaseIdentifier() {
        return di -> {
            ObservableDiseaseIdentifier odi = new ObservableDiseaseIdentifier(di);
            ObservableDiseaseStatus ods = new ObservableDiseaseStatus();
            ods.setDiseaseId(odi);
            diseaseTableController.diseaseStatuses().add(ods);
        };
    }

    @FXML
    private void removeDiseaseButtonAction(ActionEvent e) {
        ObservableList<ObservableDiseaseStatus> selectedItems = diseaseTableController.selectionModel().getSelectedItems();
        diseaseTableController.diseaseStatuses().removeAll(selectedItems);
        e.consume();
    }

    @FXML
    private void okButtonAction(ActionEvent e) {
        Utils.closeTheStage(e);
    }

    @Override
    public ObjectProperty<T> dataProperty() {
        return individual;
    }
}
