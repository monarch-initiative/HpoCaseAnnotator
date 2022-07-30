package org.monarchinitiative.hpo_case_annotator.forms.publication;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.DataEditController;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePublication;

import java.io.IOException;
import java.util.Objects;

public class PublicationEditable extends VBox implements DataEditController<ObservablePublication> {

    private static final String DEFAULT_STYLECLASS = "publication-editable";

    private final ObjectProperty<ObservablePublication> item = new SimpleObjectProperty<>();

    @FXML
    private Parent publication;
    @FXML
    private PublicationController publicationController;
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

    public PublicationEditable() {
        getStyleClass().add(DEFAULT_STYLECLASS);
        FXMLLoader loader = new FXMLLoader(PublicationEditable.class.getResource("PublicationEditable.fxml"));
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
        publicationController.dataProperty().bind(item);
        year.setTextFormatter(yearTextFormatter);
        item.addListener((obs, old, novel) -> {
            if (novel == null) {
                title.setText(null);
                authors.setText(null);
                journal.setText(null);
                year.setText(null);
                volume.setText(null);
                pages.setText(null);
                pmid.setText(null);
            } else {
                title.setText(novel.getTitle());
                authors.setText(novel.getAuthors());
                journal.setText(novel.getJournal());
                yearTextFormatter.setValue(novel.getYear());
                volume.setText(novel.getVolume());
                pages.setText(novel.getPages());
                pmid.setText(novel.getPmid());
            }
        });
    }

    @Override
    public void setInitialData(ObservablePublication data) {
        item.set(Objects.requireNonNullElseGet(data, ObservablePublication::new));
    }

    @Override
    public ObservablePublication getEditedData() {
        ObservablePublication op = item.get();

        op.setTitle(title.getText());
        op.setAuthors(authors.getText());
        op.setJournal(journal.getText());
        op.setYear(yearTextFormatter.getValue());
        op.setVolume(volume.getText());
        op.setPages(pages.getText());
        op.setPmid(pmid.getText());

        return op;
    }
}
