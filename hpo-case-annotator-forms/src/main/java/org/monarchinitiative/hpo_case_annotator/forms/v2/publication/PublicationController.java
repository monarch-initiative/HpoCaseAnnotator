package org.monarchinitiative.hpo_case_annotator.forms.v2.publication;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePublication;

import static javafx.beans.binding.Bindings.*;

public class PublicationController implements ObservableDataController<ObservablePublication> {

    private final ObjectProperty<ObservablePublication> item = new SimpleObjectProperty<>();

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
    private Label pmid;

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

    @FXML
    private void editPublicationAction(ActionEvent e) {
        PublicationEditable component = new PublicationEditable();
        component.setInitialData(item.get());

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setHeaderText("Edit publication data:");
        dialog.getDialogPane().setContent(component);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.UPDATE_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));

        dialog.showAndWait()
                .filter(i -> i)
                .ifPresent(shouldUpdate -> item.set(component.getEditedData()));

        e.consume();
    }
}
