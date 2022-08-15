package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementBindingComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;

import java.io.IOException;

public class PhenotypicFeatureBinding extends PhenotypicFeatureBase {

    @FXML
    private CheckBox onsetIsUnknown;
    @FXML
    private TimeElementBindingComponent onsetComponent;
    @FXML
    private CheckBox resolutionIsUnknown;
    @FXML
    private TimeElementBindingComponent resolutionComponent;

    public PhenotypicFeatureBinding() {
        FXMLLoader loader = new FXMLLoader(PhenotypicFeatureBinding.class.getResource("PhenotypicFeatureEditable.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void initialize() {
        super.initialize();

        onsetComponent.disableProperty().bind(onsetIsUnknown.selectedProperty());
        onsetIsUnknown.selectedProperty().addListener((obs, old, isUnknown) -> {
            if (isUnknown) {
                onsetComponent.setData(null);
            } else {
                if (onsetComponent.getData() == null)
                    onsetComponent.setData(ObservableTimeElement.defaultInstance());
            }
        });
        resolutionComponent.disableProperty().bind(resolutionIsUnknown.selectedProperty());
        resolutionIsUnknown.selectedProperty().addListener((obs, old, isUnknown) -> {
            if (isUnknown) {
                resolutionComponent.setData(null);
            } else {
                if (resolutionComponent.getData() == null)
                    resolutionComponent.setData(ObservableTimeElement.defaultInstance());
            }
        });
    }

    @Override
    protected void bind(ObservablePhenotypicFeature data) {
        super.bind(data);
        onsetIsUnknown.setSelected(data.getOnset() == null);
        onsetComponent.dataProperty().bindBidirectional(data.onsetProperty());
        resolutionIsUnknown.setSelected(data.getResolution() == null);
        resolutionComponent.dataProperty().bindBidirectional(data.resolutionProperty());
    }

    @Override
    protected void unbind(ObservablePhenotypicFeature data) {
        super.unbind(data);
        onsetComponent.dataProperty().unbindBidirectional(data.onsetProperty());
        resolutionComponent.dataProperty().unbindBidirectional(data.resolutionProperty());
    }
}
