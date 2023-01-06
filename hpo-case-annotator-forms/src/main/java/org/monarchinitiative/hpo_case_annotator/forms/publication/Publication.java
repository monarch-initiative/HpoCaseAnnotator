package org.monarchinitiative.hpo_case_annotator.forms.publication;

import javafx.beans.binding.ObjectBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePublication;

import java.io.IOException;

import static javafx.beans.binding.Bindings.*;

public class Publication extends VBoxObservableDataComponent<ObservablePublication> {

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
        title.textProperty().bind(selectString(data, "title"));
        authors.textProperty().bind(selectString(data, "authors"));
        journal.textProperty().bind(selectString(data, "journal"));
        ObjectBinding<Integer> y = select(data, "year");
        year.textProperty().bind(when(y.isNotNull()).then(y.asString()).otherwise("N/A"));
        volume.textProperty().bind(selectString(data, "volume"));
        pages.textProperty().bind(selectString(data, "pages"));
        pmid.textProperty().bind(selectString(data, "pmid"));
    }

}
