package org.monarchinitiative.hpo_case_annotator.forms.metadata;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableEditHistory;

import java.io.IOException;

public class EditHistory extends HBox implements ObservableDataController<ObservableEditHistory> {

    private final ObjectProperty<ObservableEditHistory> data = new SimpleObjectProperty<>();

    @FXML
    private TitledLabel curatorId;
    @FXML
    private TitledLabel softwareVersion;
    @FXML
    private TitledLabel timestamp;

    public EditHistory() {
        FXMLLoader loader = new FXMLLoader(EditHistory.class.getResource("EditHistory.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        data.addListener((obs, old, novel) -> {
            if (old != null) unbind(old);
            if (novel != null) bind(novel);
        });
    }

    private void unbind(ObservableEditHistory data) {
        curatorId.textProperty().unbind();
        softwareVersion.textProperty().unbind();
        timestamp.textProperty().unbind();
    }

    private void bind(ObservableEditHistory data) {
        curatorId.textProperty().bind(data.curatorIdProperty());
        softwareVersion.textProperty().bind(data.softwareVersionProperty());
        timestamp.textProperty().bind(data.timestampProperty().asString());
    }

    @Override
    public ObjectProperty<ObservableEditHistory> dataProperty() {
        return data;
    }

    @FXML
    private void initialize() {
        // no-op
    }

}
