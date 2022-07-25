package org.monarchinitiative.hpo_case_annotator.forms.v2.publication;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import org.monarchinitiative.hpo_case_annotator.forms.BindingDataController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePublication;

public class PublicationController extends BindingDataController<ObservablePublication> {

    private final ObjectProperty<ObservablePublication> publication = new SimpleObjectProperty<>(this, "publication", new ObservablePublication());

    @FXML
    private Label title;
    @FXML
    private Label authors;
    @FXML
    private Label journal;
    @FXML
    private Label year;
    @FXML
    private Label volume;
    @FXML
    private Label pages;
    @FXML
    private Hyperlink pmid;

    @Override
    public ObjectProperty<ObservablePublication> dataProperty() {
        return publication;
    }

    @Override
    protected void bind(ObservablePublication data) {
        title.textProperty().bind(data.titleProperty());
        authors.textProperty().bind(data.authorsProperty());
        journal.textProperty().bind(data.journalProperty());
        year.textProperty().bind(data.yearProperty().asString());
        volume.textProperty().bind(data.volumeProperty());
        pages.textProperty().bind(data.pagesProperty());
        pmid.textProperty().bind(data.pmidProperty());
    }

    @Override
    protected void unbind(ObservablePublication data) {
        title.textProperty().unbind();
        authors.textProperty().unbind();
        journal.textProperty().unbind();
        year.textProperty().unbind();
        volume.textProperty().unbind();
        pages.textProperty().unbind();
        pmid.textProperty().unbind();
    }

    @FXML
    private void editPublicationAction(ActionEvent e) {
         // TODO - implement
        e.consume();
    }
}
