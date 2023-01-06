package org.monarchinitiative.hpo_case_annotator.forms.publication;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextFormatter;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxBindingObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.util.TextFormatters;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePublication;

import java.io.IOException;
import java.util.stream.Stream;

public class PublicationBinding extends VBoxBindingObservableDataComponent<ObservablePublication> implements Observable {

    static final Callback<PublicationBinding, Stream<Observable>> EXTRACTOR = pb -> Stream.of(
            pb.title.textProperty(),
            pb.authors.textProperty(),
            pb.journal.textProperty(),
            pb.year.textProperty(),
            pb.volume.textProperty(),
            pb.pages.textProperty(),
            pb.pmid.textProperty()
    );

    @FXML
    private TitledTextField title;
    @FXML
    private TitledTextField authors;
    @FXML
    private TitledTextField journal;
    @FXML
    private TitledTextField year;
    private final TextFormatter<Integer> yearTextFormatter = TextFormatters.integerFormatter();
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
    }

    @Override
    protected void bind(ObservablePublication data) {
        if (data != null) {
            title.textProperty().bindBidirectional(data.titleProperty());
            authors.textProperty().bindBidirectional(data.authorsProperty());
            journal.textProperty().bindBidirectional(data.journalProperty());
            yearTextFormatter.valueProperty().bindBidirectional(data.yearProperty());
            volume.textProperty().bindBidirectional(data.volumeProperty());
            pages.textProperty().bindBidirectional(data.pagesProperty());
            pmid.textProperty().bindBidirectional(data.pmidProperty());
        } else {
            clear();
        }
    }

    private void clear() {
        title.setText(null);
        authors.setText(null);
        journal.setText(null);
        yearTextFormatter.setValue(null);
        volume.setText(null);
        pages.setText(null);
        pmid.setText(null);
    }

    @Override
    protected void unbind(ObservablePublication data) {
        if (data != null) {
            title.textProperty().unbindBidirectional(data.titleProperty());
            authors.textProperty().unbindBidirectional(data.authorsProperty());
            journal.textProperty().unbindBidirectional(data.journalProperty());
            yearTextFormatter.valueProperty().unbindBidirectional(data.yearProperty());
            volume.textProperty().unbindBidirectional(data.volumeProperty());
            pages.textProperty().unbindBidirectional(data.pagesProperty());
            pmid.textProperty().unbindBidirectional(data.pmidProperty());
        }
    }

    @Override
    public void addListener(InvalidationListener listener) {
        EXTRACTOR.call(this).forEach(obs -> obs.addListener(listener));
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        EXTRACTOR.call(this).forEach(obs -> obs.removeListener(listener));
    }
}
