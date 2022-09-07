package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementComponent;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;

public class PhenotypicFeature extends PhenotypicFeatureBase {
    @FXML
    private Button editTemporalFields;
    @FXML
    private TimeElementComponent onsetComponent;
    @FXML
    private TimeElementComponent resolutionComponent;

    public PhenotypicFeature() {
        super(PhenotypicFeature.class.getResource("PhenotypicFeature.fxml"));
    }

    @FXML
    protected void initialize() {
        super.initialize();

        editTemporalFields.disableProperty().bind(data.isNull());
    }


    @Override
    protected void bind(ObservablePhenotypicFeature feature) {
        super.bind(feature);
        // observation onset & resolution
        onsetComponent.dataProperty().bind(feature.onsetProperty());
        resolutionComponent.dataProperty().bind(feature.resolutionProperty());
    }

    @Override
    protected void unbind(ObservablePhenotypicFeature feature) {
        super.unbind(feature);

        onsetComponent.dataProperty().unbind();
        resolutionComponent.dataProperty().unbind();
    }


    @FXML
    private void editTemporalFieldsAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setResizable(true);
        dialog.setTitle("Edit onset and resolution of %s".formatted(data.get().id().getValue()));
        TemporalFieldsDataEdit content = new TemporalFieldsDataEdit();
        content.setInitialData(data.get());

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.UPDATE_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));

        dialog.showAndWait()
                .ifPresent(shouldUpdate -> {
                    if (shouldUpdate)
                        content.commit();
                });

        e.consume();
    }
}
