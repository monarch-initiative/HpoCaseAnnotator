package org.monarchinitiative.hpo_case_annotator.forms.disease;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.DataEditController;
import org.monarchinitiative.hpo_case_annotator.forms.component.BaseIndividualIdsComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.DiseaseIdentifier;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableDiseaseStatus;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class DiseaseDataEdit<T extends BaseObservableIndividual> extends VBox implements DataEditController<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseDataEdit.class);


    private final ObjectProperty<DiseaseIdentifierService> diseaseIdentifierService = new SimpleObjectProperty<>();

    private BaseObservableIndividual item;

    @FXML
    private TextField diseaseIdTextField;
    @FXML
    private TextField diseaseNameTextField;
    @FXML
    private Button addDiseaseButton;
    @FXML
    private BaseIndividualIdsComponent<T> individualIds;
    @FXML
    private DiseaseTable diseaseTable;

    private AutoCompletionBinding<String> diseaseIdCompletion;
    private AutoCompletionBinding<String> diseaseNameCompletion;

    @FXML
    protected void initialize() {
        diseaseIdTextField.disableProperty().bind(diseaseIdentifierService.isNull());
        diseaseNameTextField.disableProperty().bind(diseaseIdentifierService.isNull());
        addDiseaseButton.disableProperty().bind(diseaseIdentifierService.isNull());

        diseaseIdentifierService.addListener((obs, old, novel) -> {
            if (novel == null) {
                diseaseIdCompletion = null;
                diseaseNameCompletion = null;
            } else {
                diseaseIdCompletion = TextFields.bindAutoCompletion(diseaseIdTextField, novel.diseaseIds());
                diseaseIdCompletion.setHideOnEscape(true);
                diseaseIdCompletion.setVisibleRowCount(10);
                diseaseIdCompletion.minWidthProperty().bind(diseaseIdTextField.widthProperty());

                diseaseNameCompletion = TextFields.bindAutoCompletion(diseaseNameTextField, novel.diseaseNames());
                diseaseNameCompletion.setHideOnEscape(true);
                diseaseNameCompletion.setVisibleRowCount(10);
                diseaseNameCompletion.minWidthProperty().bind(diseaseNameTextField.widthProperty());
            }
        });
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

    private Runnable reportMissing(Object source) {
        return () -> {
            // TODO - show popup and report invalid disease ID/name
        };
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

    private Consumer< DiseaseIdentifier> addDiseaseIdentifier() {
        return di -> {
            ObservableDiseaseStatus ods = new ObservableDiseaseStatus();
            ods.setDiseaseId(di);
            diseaseTable.getItems().add(ods);
        };
    }

    @Override
    public void setInitialData(T data) {
        item = Objects.requireNonNull(data);

        individualIds.setData(data);

        diseaseTable.getItems().setAll(item.getDiseaseStates());
    }

    @Override
    public void commit() {
        item.getDiseaseStates().setAll(diseaseTable.getItems());
    }

    public DiseaseIdentifierService getDiseaseIdentifierService() {
        return diseaseIdentifierService.get();
    }

    public ObjectProperty<DiseaseIdentifierService> diseaseIdentifierServiceProperty() {
        return diseaseIdentifierService;
    }

    public void setDiseaseIdentifierService(DiseaseIdentifierService diseaseIdentifierService) {
        this.diseaseIdentifierService.set(diseaseIdentifierService);
    }
}
