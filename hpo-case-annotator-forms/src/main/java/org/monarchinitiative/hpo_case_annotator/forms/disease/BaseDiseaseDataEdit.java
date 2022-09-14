package org.monarchinitiative.hpo_case_annotator.forms.disease;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.component.BaseIndividualIdsComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.DiseaseIdentifier;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableDiseaseStatus;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * <h2>Properties</h2>
 * {@link BaseDiseaseDataEdit} needs the following properties to be set in order to work.
 * <ul>
 *     <li>{@link #diseaseIdentifierServiceProperty()}</li>
 * </ul>
 */
public abstract class BaseDiseaseDataEdit<T extends BaseObservableIndividual> extends VBoxDataEdit<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDiseaseDataEdit.class);

    private final ObjectProperty<DiseaseIdentifierService> diseaseIdentifierService = new SimpleObjectProperty<>();
    private final ListProperty<ObservableDiseaseStatus> diseaseStates = new SimpleListProperty<>(FXCollections.observableArrayList());

    private BaseObservableIndividual item;

    @FXML
    private TitledTextField diseaseIdTextField;
    @FXML
    private TitledTextField diseaseNameTextField;
    @FXML
    private Button addDiseaseButton;
    @FXML
    private BaseIndividualIdsComponent<T> individualIds;
    @FXML
    private DiseaseTable diseaseTable;

    private AutoCompletionBinding<String> diseaseIdCompletion;
    private AutoCompletionBinding<String> diseaseNameCompletion;

    protected BaseDiseaseDataEdit(URL location) {
        FXMLLoader loader = new FXMLLoader(location);
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectProperty<DiseaseIdentifierService> diseaseIdentifierServiceProperty() {
        return diseaseIdentifierService;
    }

    @Override
    public void setInitialData(T data) {
        item = Objects.requireNonNull(data);

        individualIds.setData(data);

        for (ObservableDiseaseStatus diseaseState : item.getDiseaseStates())
            diseaseStates.add(new ObservableDiseaseStatus(diseaseState));
    }

    @Override
    public void commit() {
        item.diseaseStatesProperty().setAll(diseaseStates);
    }

    @FXML
    protected void initialize() {
        diseaseIdTextField.disableProperty().bind(diseaseIdentifierService.isNull());
        diseaseNameTextField.disableProperty().bind(diseaseIdentifierService.isNull());
        addDiseaseButton.disableProperty().bind(diseaseIdentifierService.isNull());

        diseaseTable.diseaseStatesProperty().bind(diseaseStates);

        diseaseIdentifierService.addListener((obs, old, novel) -> {
            if (novel == null) {
                diseaseIdCompletion = null;
                diseaseNameCompletion = null;
            } else {
                diseaseIdCompletion = TextFields.bindAutoCompletion(diseaseIdTextField.getItem(), novel.diseaseIds());
                diseaseIdCompletion.setHideOnEscape(true);
                diseaseIdCompletion.setVisibleRowCount(10);
                diseaseIdCompletion.minWidthProperty().bind(diseaseIdTextField.widthProperty());

                diseaseNameCompletion = TextFields.bindAutoCompletion(diseaseNameTextField.getItem(), novel.diseaseNames());
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

        diseaseIdTextField.getItem().clear();
        diseaseNameTextField.getItem().clear();
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

    private Consumer<DiseaseIdentifier> addDiseaseIdentifier() {
        return di -> {
            ObservableDiseaseStatus ods = new ObservableDiseaseStatus();
            ods.setDiseaseId(di);
            diseaseTable.getDiseaseStates().add(ods);
        };
    }
}
