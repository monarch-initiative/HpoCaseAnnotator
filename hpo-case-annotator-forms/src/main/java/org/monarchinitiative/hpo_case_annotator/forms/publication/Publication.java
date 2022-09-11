package org.monarchinitiative.hpo_case_annotator.forms.publication;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePublication;

import java.io.IOException;

import static javafx.beans.binding.Bindings.*;

public class Publication extends VBox implements ObservableDataComponent<ObservablePublication> {

    private final ObjectProperty<ObservablePublication> item = new SimpleObjectProperty<>();

    @FXML
    private Label title;
    @FXML
    private Label authors;
    @FXML
    private TitledLabel journal;
    @FXML
    private TitledLabel year;
    @FXML
    private TitledLabel volume;
    @FXML
    private TitledLabel pages;
    @FXML
    private TitledLabel pmid;

    public Publication() {
        FXMLLoader loader = new FXMLLoader(Publication.class.getResource("Publication.fxml"));
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
        title.textProperty().bind(selectString(item, "title"));
        authors.textProperty().bind(selectString(item, "authors"));
        journal.textProperty().bind(selectString(item, "journal"));
        year.textProperty().bind(selectInteger(item, "year").asString());
        volume.textProperty().bind(selectString(item, "volume"));
        pages.textProperty().bind(selectString(item, "pages"));
        pmid.textProperty().bind(selectString(item, "pmid"));
    }

    @Override
    public ObjectProperty<ObservablePublication> dataProperty() {
        return item;
    }

}
