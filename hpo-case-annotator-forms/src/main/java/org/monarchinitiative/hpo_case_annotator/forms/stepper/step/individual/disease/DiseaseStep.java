package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.disease;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.disease.DiseaseSummary;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.BaseStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.DiseaseIdentifier;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableDiseaseStatus;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class DiseaseStep<T extends BaseObservableIndividual> extends BaseStep<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseStep.class);

    private final ObjectProperty<DiseaseIdentifierService> diseaseIdentifierService = new SimpleObjectProperty<>();

    @FXML
    private TextField diseaseIdTextField;
    @FXML
    private TextField diseaseNameTextField;
    @FXML
    private Button addDiseaseButton;
    @FXML
    private DiseaseSummary diseaseSummary;

    public DiseaseStep() {
        super(DiseaseStep.class.getResource("DiseaseStep.fxml"));
    }

    public ObjectProperty<DiseaseIdentifierService> diseaseIdentifierServiceProperty() {
        return diseaseIdentifierService;
    }

    @Override
    protected Stream<Observable> dependencies() {
        return Stream.of();
    }

    @Override
    public void invalidated(Observable observable) {
        // no-op
    }

    @Override
    protected void bind(T data) {
        if (data != null)
            diseaseSummary.diseaseStatesProperty().bindBidirectional(data.diseaseStatesProperty());
        else
            diseaseSummary.getDiseaseStates().clear();
    }

    @Override
    protected void unbind(T data) {
        if (data != null)
            diseaseSummary.diseaseStatesProperty().unbindBidirectional(data.diseaseStatesProperty());
    }

    @FXML
    private void addDisease(ActionEvent e) {
        if (e.getSource().equals(diseaseIdTextField)) {
            getDiseaseIdentifierFromDiseaseId()
                    .ifPresentOrElse(addDiseaseIdentifier(), reportMissing(e.getSource()));
        } else if (e.getSource().equals(diseaseNameTextField)) {
            getDiseaseIdentifierFromDiseaseName()
                    .ifPresentOrElse(addDiseaseIdentifier(), reportMissing(e.getSource()));
        } else if (e.getSource().equals(addDiseaseButton)) {
            getDiseaseIdentifierFromDiseaseId()
                    .or(this::getDiseaseIdentifierFromDiseaseName)
                    .ifPresentOrElse(addDiseaseIdentifier(), reportMissing(e.getSource()));
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
            return diseaseIdentifierService.get().diseaseIdentifierForDiseaseId(diseaseId)
                    .map(DiseaseIdentifier::new);
        } catch (Exception ex) {
            // TODO - show as popup dialog
            LOGGER.warn("Unparsable disease ID: {}", ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    private Optional<DiseaseIdentifier> getDiseaseIdentifierFromDiseaseName() {
        return diseaseIdentifierService.get()
                .diseaseIdentifierForDiseaseName(diseaseNameTextField.getText())
                .map(DiseaseIdentifier::new);
    }

    private Consumer<DiseaseIdentifier> addDiseaseIdentifier() {
        return di -> {
            ObservableDiseaseStatus ods = new ObservableDiseaseStatus();
            ods.setDiseaseId(di);
            diseaseSummary.getDiseaseStates().add(ods);
        };
    }

    private Runnable reportMissing(Object source) {
        return () -> {
            // TODO - show popup and report invalid disease ID/name
        };
    }
}
