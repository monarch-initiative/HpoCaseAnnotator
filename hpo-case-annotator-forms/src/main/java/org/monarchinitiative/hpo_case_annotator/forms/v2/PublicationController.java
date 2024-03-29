package org.monarchinitiative.hpo_case_annotator.forms.v2;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.monarchinitiative.hpo_case_annotator.forms.base.BindingObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.util.TextFormatters;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePublication;

public class PublicationController extends BindingObservableDataComponent<ObservablePublication> {

    private final ObjectProperty<ObservablePublication> publication = new SimpleObjectProperty<>(this, "publication", new ObservablePublication());
    private final TextFormatter<Integer> yearFormatter = TextFormatters.integerFormatter();
    @FXML
    private Label summaryLabel;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextField authorsTextField;
    @FXML
    private TextField journalTextField;
    @FXML
    private TextField yearTextField;
    @FXML
    private TextField volumeTextField;
    @FXML
    private TextField pagesTextField;
    @FXML
    private TextField pmidTextField;

    @FXML
    protected void initialize() {
        super.initialize();
        summaryLabel.textProperty().bind(createPublicationSummary());
        yearTextField.setTextFormatter(yearFormatter);
    }

    private StringBinding createPublicationSummary() {
        return Bindings.createStringBinding(() -> {
                    String text = titleTextField.getText();

                    String authors = authorsTextField.getText();
                    String journal = journalTextField.getText();
                    String year = yearTextField.getText();
                    String pmid = pmidTextField.getText();
                    return String.format("%s\n\n%s\n\n%s, %s, %s",
                            text == null ? "" : text,
                            authors == null ? "" : authors, journal, year, pmid);
                },
                titleTextField.textProperty(), authorsTextField.textProperty(), journalTextField.textProperty(), yearTextField.textProperty(), pmidTextField.textProperty());
    }

    @Override
    protected void bind(ObservablePublication publication) {
        titleTextField.textProperty().bindBidirectional(publication.titleProperty());
        authorsTextField.textProperty().bindBidirectional(publication.authorsProperty());
        journalTextField.textProperty().bindBidirectional(publication.journalProperty());
        yearFormatter.valueProperty().bindBidirectional(publication.yearProperty());
        volumeTextField.textProperty().bindBidirectional(publication.volumeProperty());
        pagesTextField.textProperty().bindBidirectional(publication.pagesProperty());
        pmidTextField.textProperty().bindBidirectional(publication.pmidProperty());
    }

    @Override
    protected void unbind(ObservablePublication publication) {
        titleTextField.textProperty().unbindBidirectional(publication.titleProperty());
        authorsTextField.textProperty().unbindBidirectional(publication.authorsProperty());
        journalTextField.textProperty().unbindBidirectional(publication.journalProperty());
        yearFormatter.valueProperty().unbindBidirectional(publication.yearProperty());
        volumeTextField.textProperty().unbindBidirectional(publication.volumeProperty());
        pagesTextField.textProperty().unbindBidirectional(publication.pagesProperty());
        pmidTextField.textProperty().unbindBidirectional(publication.pmidProperty());
    }

    @Override
    public ObjectProperty<ObservablePublication> dataProperty() {
        return publication;
    }
}
