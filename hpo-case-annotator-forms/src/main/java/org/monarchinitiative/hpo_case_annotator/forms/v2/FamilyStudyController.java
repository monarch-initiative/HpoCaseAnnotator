package org.monarchinitiative.hpo_case_annotator.forms.v2;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;

public class FamilyStudyController extends StudyController<ObservableFamilyStudy> {

    private final ObjectProperty<ObservableFamilyStudy> study = new SimpleObjectProperty<>(this, "study", new ObservableFamilyStudy());

    @FXML
    private VBox pedigree;
    @FXML
    private PedigreeController pedigreeController;

    @FXML
    protected void initialize() {
        super.initialize();
    }

    @Override
    public ObjectProperty<ObservableFamilyStudy> dataProperty() {
        return study;
    }

    @Override
    protected void bind(ObservableFamilyStudy study) {
        super.bind(study);

        // pedigreeController.variants() must not be modified directly from now on!
        Bindings.bindContentBidirectional(variantSummaryController.curatedVariants(), study.variants());
        Bindings.bindContent(pedigreeController.curatedVariants(), variantSummaryController.curatedVariants());

        pedigreeController.dataProperty().bindBidirectional(study.pedigreeProperty());
    }

    @Override
    protected void unbind(ObservableFamilyStudy study) {
        super.unbind(study);
        Bindings.unbindContent(pedigreeController.curatedVariants(), variantSummaryController.curatedVariants());

        pedigreeController.dataProperty().unbindBidirectional(study.pedigreeProperty());
    }

}
