package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;

public class OntologyClassAge extends VBox {

    private final ObjectProperty<TermId> termId = new SimpleObjectProperty<>();

    @FXML
    private TitledLabel ontologyClass;

    public OntologyClassAge() {
        FXMLLoader loader = new FXMLLoader(OntologyClassAge.class.getResource("OntologyClassAge.fxml"));
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
        termId.addListener((obs, old, novel) -> ontologyClass.setText(novel == null ? null : novel.getValue()));
    }

    public ObjectProperty<TermId> termIdProperty() {
        return termId;
    }
}
