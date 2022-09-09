package org.monarchinitiative.hpo_case_annotator.forms.study;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.monarchinitiative.hpo_case_annotator.forms.pedigree.Pedigree;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;

public class FamilyStudyComponent extends BaseStudyComponent<ObservableFamilyStudy> {

    @FXML
    private Label pedigreeLabel;
    @FXML
    private Pedigree pedigree;

    public FamilyStudyComponent() {
        super(FamilyStudyComponent.class.getResource("FamilyStudy.fxml"));
    }

    @Override
    protected void initialize() {
        super.initialize();
        pedigree.hpoProperty().bind(studyResources.hpoProperty());
        pedigree.diseaseIdentifierServiceProperty().bind(studyResources.diseaseIdentifierServiceProperty());
        pedigree.variantsProperty().bind(variantSummary.variantsProperty());
    }

    @Override
    protected void bind(ObservableFamilyStudy data) {
        super.bind(data);
        if (data != null)
            pedigree.dataProperty().bindBidirectional(data.pedigreeProperty());
    }

    @Override
    protected void unbind(ObservableFamilyStudy data) {
        super.unbind(data);
        if (data != null)
            pedigree.dataProperty().unbindBidirectional(data.pedigreeProperty());
    }

    @FXML
    private void pedigreeNavMouseClicked(ActionEvent e) {
//        ensureVisible(studyScrollPane, pedigreeLabel);
        System.err.println("Pedigree NAV item was clicked on");
        e.consume();
    }

    private static void ensureVisible(ScrollPane pane, Node node) {
        double width = pane.getContent().getBoundsInLocal().getWidth();
        double height = pane.getContent().getBoundsInLocal().getHeight();

        double x = node.getBoundsInParent().getMaxX();
        double y = node.getBoundsInParent().getMaxY();

        // scrolling values range from 0 to 1
        pane.setVvalue(y/height);
        pane.setHvalue(x/width);

        // just for usability
        node.requestFocus();
    }
}
