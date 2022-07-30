package org.monarchinitiative.hpo_case_annotator.forms.nvo;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import org.monarchinitiative.hpo_case_annotator.forms.BaseBindingObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.pedigree.PedigreeController;
import org.monarchinitiative.hpo_case_annotator.forms.publication.PublicationController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;


public class FamilyStudyController extends BaseBindingObservableDataController<ObservableFamilyStudy> {

    @FXML
    private Parent publication;
    @FXML
    private PublicationController publicationController;
    @FXML
    private Parent pedigree;
    @FXML
    private PedigreeController pedigreeController;

    @Override
    protected void bind(ObservableFamilyStudy data) {
        publicationController.dataProperty().bindBidirectional(data.publicationProperty());
        pedigreeController.dataProperty().bindBidirectional(data.pedigreeProperty());
    }

    @Override
    protected void unbind(ObservableFamilyStudy data) {
        publicationController.dataProperty().unbindBidirectional(data.publicationProperty());
        pedigreeController.dataProperty().unbindBidirectional(data.pedigreeProperty());
    }

}
