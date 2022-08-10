package org.monarchinitiative.hpo_case_annotator.forms.publication;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePublication;

import java.io.IOException;

public class PublicationBinding extends VBox implements ObservableDataController<ObservablePublication> {

    private final ObjectProperty<ObservablePublication> data = new SimpleObjectProperty<>();

    @FXML
    private TitledTextField title;
    @FXML
    private TitledTextField authors;
    @FXML
    private TitledTextField journal;
    @FXML
    private TitledTextField year;
    private final TextFormatter<Integer> yearTextFormatter = Formats.integerFormatter();
    @FXML
    private TitledTextField volume;
    @FXML
    private TitledTextField pages;
    @FXML
    private TitledTextField pmid;

    public PublicationBinding() {
        FXMLLoader loader = new FXMLLoader(PublicationBinding.class.getResource("PublicationBinding.fxml"));
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

        data.set(new ObservablePublication());
    }

    private void bind(ObservablePublication data) {
        title.textProperty().bindBidirectional(data.titleProperty());
        authors.textProperty().bindBidirectional(data.authorsProperty());
        journal.textProperty().bindBidirectional(data.journalProperty());
        yearTextFormatter.valueProperty().bindBidirectional(data.yearProperty());
        volume.textProperty().bindBidirectional(data.volumeProperty());
        pages.textProperty().bindBidirectional(data.pagesProperty());
        pmid.textProperty().bindBidirectional(data.pmidProperty());
    }

    private void unbind(ObservablePublication data) {
        title.textProperty().unbindBidirectional(data.titleProperty());
        authors.textProperty().unbindBidirectional(data.authorsProperty());
        journal.textProperty().unbindBidirectional(data.journalProperty());
        yearTextFormatter.valueProperty().unbindBidirectional(data.yearProperty());
        volume.textProperty().unbindBidirectional(data.volumeProperty());
        pages.textProperty().unbindBidirectional(data.pagesProperty());
        pmid.textProperty().unbindBidirectional(data.pmidProperty());
    }

    @Override
    public ObjectProperty<ObservablePublication> dataProperty() {
        return data;
    }
}
