package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.DataEditController;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementEditableComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;

import java.io.IOException;
import java.util.Objects;

public class EditTemporalFields extends VBox implements DataEditController<ObservablePhenotypicFeature> {

    private ObservablePhenotypicFeature data;
    private ObservableTimeElement onset;
    private ObservableTimeElement resolution;

    @FXML
    private TitledLabel termId;
    @FXML
    private TitledLabel termLabel;
    @FXML
    private TitledLabel termDefinition;

    @FXML
    private CheckBox onsetIsUnknown;
    @FXML
    private TimeElementEditableComponent onsetComponent;
    @FXML
    private CheckBox resolutionIsUnknown;
    @FXML
    private TimeElementEditableComponent resolutionComponent;

    public EditTemporalFields() {
        FXMLLoader loader = new FXMLLoader(EditTemporalFields.class.getResource("EditTemporalFields.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        onsetComponent.disableProperty().bind(onsetIsUnknown.selectedProperty());
        onsetIsUnknown.selectedProperty().addListener(obs -> {
            if (onsetIsUnknown.isSelected()) {
                onset = null; // clear the onset
            } else {
                if (onset == null) {
                    onset = new ObservableTimeElement();
                    onsetComponent.setInitialData(onset);
                }
            }
        });

        resolutionComponent.disableProperty().bind(resolutionIsUnknown.selectedProperty());
        resolutionIsUnknown.selectedProperty().addListener(obs -> {
            if (resolutionIsUnknown.isSelected()) {
                resolution = null; // clear the resolution
            } else {
                if (resolution == null) {
                    resolution = new ObservableTimeElement();
                    resolutionComponent.setInitialData(resolution);
                }
            }
        });
    }

    @Override
    public void setInitialData(ObservablePhenotypicFeature data) {
        this.data = Objects.requireNonNull(data);

        termId.setText(this.data.getTermId().getValue());
        termLabel.setText("NOT RIGHT NOW");
        termDefinition.setText("NOT RIGHT NOW");

        onset = this.data.getOnset();
        onsetIsUnknown.setSelected(onset == null);
        if (onset != null)
            onsetComponent.setInitialData(onset);

        resolution = this.data.getResolution();
        resolutionIsUnknown.setSelected(resolution == null);
        if (resolution != null)
            resolutionComponent.setInitialData(resolution);
    }

    @Override
    public void commit() {
        if (onsetIsUnknown.isSelected()) {
            data.setOnset(null);
        } else {
            onsetComponent.commit();
            data.setOnset(onset);
        }

        if (resolutionIsUnknown.isSelected()) {
            data.setResolution(null);
        } else {
            resolutionComponent.commit();
            data.setResolution(resolution);
        }
    }
}
