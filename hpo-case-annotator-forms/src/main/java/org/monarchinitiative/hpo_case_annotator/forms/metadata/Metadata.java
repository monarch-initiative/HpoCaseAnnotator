package org.monarchinitiative.hpo_case_annotator.forms.metadata;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableEditHistory;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudyMetadata;

import java.io.IOException;

import static javafx.beans.binding.Bindings.select;

public class Metadata extends VBox implements ObservableDataController<ObservableStudyMetadata> {

    private final ObjectProperty<ObservableStudyMetadata> item = new SimpleObjectProperty<>();

    @FXML
    private TextArea freeTextArea;
    @FXML
    private EditHistory createdBy;
    @FXML
    private TableView<ObservableEditHistory> editHistory;
    @FXML
    private TableColumn<ObservableEditHistory, String> curatorIdColumn;
    @FXML
    private TableColumn<ObservableEditHistory, String> softwareVersionColumn;
    @FXML
    private TableColumn<ObservableEditHistory, String> timestampColumn;

    public Metadata() {
        FXMLLoader loader = new FXMLLoader(Metadata.class.getResource("Metadata.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        item.addListener((obs, old, novel) -> {
            if (old != null) unbind(old);
            if (novel != null) bind(novel);
        });
    }

    @FXML
    private void initialize() {
        createdBy.dataProperty().bind(select(item, "createdBy"));
        editHistory.itemsProperty().bind(select(item, "modifiedBy"));
        curatorIdColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getCuratorId()));
        softwareVersionColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getSoftwareVersion()));
        timestampColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getTimestamp().toString()));
    }

    private void unbind(ObservableStudyMetadata metadata) {
        freeTextArea.textProperty().unbindBidirectional(metadata.freeTextProperty());
    }

    private void bind(ObservableStudyMetadata metadata) {
        freeTextArea.textProperty().bindBidirectional(metadata.freeTextProperty());
    }


    @Override
    public ObjectProperty<ObservableStudyMetadata> dataProperty() {
        return item;
    }
}
