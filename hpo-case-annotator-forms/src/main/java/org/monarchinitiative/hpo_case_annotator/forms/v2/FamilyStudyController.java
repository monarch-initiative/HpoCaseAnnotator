package org.monarchinitiative.hpo_case_annotator.forms.v2;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigree;

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
    protected void bind(ObservableFamilyStudy study) {
        super.bind(study);

        Bindings.bindContent(pedigreeController.curatedVariants(), variantSummaryController.curatedVariants());
        ObservablePedigree pedigree = study.getPedigree();
        if (pedigree != null)
            Bindings.bindContentBidirectional(pedigreeController.members(), pedigree.membersProperty());
    }

    @Override
    protected void unbind(ObservableFamilyStudy study) {
        super.unbind(study);

        Bindings.unbindContent(pedigreeController.curatedVariants(), variantSummaryController.curatedVariants());
        ObservablePedigree pedigree = study.getPedigree();
        if (pedigree != null)
            Bindings.unbindContentBidirectional(pedigreeController.members(), pedigree.membersProperty());
    }

    @Override
    public ObjectProperty<ObservableFamilyStudy> dataProperty() {
        return study;
    }

}
