package org.monarchinitiative.hpo_case_annotator.forms.v2;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.forms.BindingDataController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.ObservablePublication;

public class PublicationController extends BindingDataController<ObservablePublication> {

    private final ObjectProperty<ObservablePublication> publication = new SimpleObjectProperty<>(this, "publication", new ObservablePublication());
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

    }

    private StringBinding createPublicationSummary() {
        return Bindings.createStringBinding(() -> {
                    ObservablePublication op = publication.get();
                    if (op == null) {
                        return null;
                    }
                    return String.format("%s\n\n%s\n\n%s, %s, %s", op.getTitle(), String.join(", ", op.authors()), op.getJournal(), op.getYear(), op.getPmid());
                },
                publication);
    }

    @Override
    protected void bind(ObservablePublication publication) {
        titleTextField.textProperty().bindBidirectional(publication.titleProperty());
//        authorsTextField.textProperty().bindBidirectional(publication.authors());
        journalTextField.textProperty().bindBidirectional(publication.journalProperty());
        yearTextField.textProperty().bind(publication.yearProperty().asString());
        volumeTextField.textProperty().bindBidirectional(publication.volumeProperty());
        pagesTextField.textProperty().bindBidirectional(publication.pagesProperty());
        pmidTextField.textProperty().bindBidirectional(publication.pmidProperty());
    }

    @Override
    protected void unbind(ObservablePublication publication) {
        titleTextField.textProperty().unbindBidirectional(publication.titleProperty());
//        authorsTextField.textProperty().bindBidirectional(publication.authors());
        journalTextField.textProperty().unbindBidirectional(publication.journalProperty());
        yearTextField.textProperty().unbind();
        volumeTextField.textProperty().unbindBidirectional(publication.volumeProperty());
        pagesTextField.textProperty().unbindBidirectional(publication.pagesProperty());
        pmidTextField.textProperty().unbindBidirectional(publication.pmidProperty());
    }

    @Override
    public ObjectProperty<ObservablePublication> dataProperty() {
        return publication;
    }
}
