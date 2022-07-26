package org.monarchinitiative.hpo_case_annotator.forms.nvo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import org.monarchinitiative.hpo_case_annotator.forms.BindingDataController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.publication.PublicationController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;

public class FamilyStudyController extends BindingDataController<ObservableFamilyStudy> {

    private final ObjectProperty<ObservableFamilyStudy> study = new SimpleObjectProperty<>(this, "study", new ObservableFamilyStudy());

    @FXML
    private Parent publication;

    @FXML
    private PublicationController publicationController;

    @Override
    protected void bind(ObservableFamilyStudy data) {
        publicationController.dataProperty().bindBidirectional(data.publicationProperty());
        // TODO - implement
    }

    @Override
    protected void unbind(ObservableFamilyStudy data) {
        publicationController.dataProperty().unbindBidirectional(data.publicationProperty());
         // TODO - implement
    }

    @Override
    public ObjectProperty<ObservableFamilyStudy> dataProperty() {
        return study;
    }
}
