package org.monarchinitiative.hpo_case_annotator.forms.publication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import org.monarchinitiative.hpo_case_annotator.forms.base.HBoxBindingObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePublication;

import java.io.IOException;

public class PublicationNullable extends HBoxBindingObservableDataComponent<ObservablePublication> {

    @FXML
    private Label publicationIsUnset;
    @FXML
    private Publication publication;

    public PublicationNullable() {
        FXMLLoader loader = new FXMLLoader(PublicationNullable.class.getResource("PublicationNullable.fxml"));
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
        if (data == null) {
            getChildren().add(publicationIsUnset);
        } else {
            publication.setData(data);
            getChildren().add(publication);
        }
    }

    @Override
    protected void unbind(ObservablePublication data) {
        getChildren().clear();
    }
}
