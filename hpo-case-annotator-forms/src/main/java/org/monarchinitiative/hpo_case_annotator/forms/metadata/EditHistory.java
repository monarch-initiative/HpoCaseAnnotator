package org.monarchinitiative.hpo_case_annotator.forms.metadata;

import javafx.beans.binding.ObjectBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import org.monarchinitiative.hpo_case_annotator.forms.base.HBoxObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableEditHistory;

import java.io.IOException;
import java.time.Instant;

import static javafx.beans.binding.Bindings.*;

public class EditHistory extends HBoxObservableDataComponent<ObservableEditHistory> {

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
    }

    @FXML
    protected void initialize() {
        curatorId.textProperty().bind(selectString(data, "curatorId"));
        softwareVersion.textProperty().bind(selectString(data, "softwareVersion"));
        ObjectBinding<Instant> ts = select(data, "timestamp");
        timestamp.textProperty().bind(when(ts.isNull())
                .then("N/A")
                .otherwise(ts.asString()));
    }

}
