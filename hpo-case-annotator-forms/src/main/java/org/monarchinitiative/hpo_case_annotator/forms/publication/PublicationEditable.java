package org.monarchinitiative.hpo_case_annotator.forms.publication;

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

    private ObservablePublication item;

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
        year.setTextFormatter(yearTextFormatter);
    }

    @Override
    public void setInitialData(ObservablePublication data) {
        item = Objects.requireNonNull(data);
        publicationController.setData(item);
        title.setText(data.getTitle());
        authors.setText(data.getAuthors());
        journal.setText(data.getJournal());
        yearTextFormatter.setValue(data.getYear());
        volume.setText(data.getVolume());
        pages.setText(data.getPages());
        pmid.setText(data.getPmid());
    }

    @Override
    public void commit() {
        item.setTitle(title.getText());
        item.setAuthors(authors.getText());
        item.setJournal(journal.getText());
        item.setYear(yearTextFormatter.getValue());
        item.setVolume(volume.getText());
        item.setPages(pages.getText());
        item.setPmid(pmid.getText());
    }
}