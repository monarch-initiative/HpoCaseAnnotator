package org.monarchinitiative.hpo_case_annotator.forms.nvo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.monarchinitiative.hpo_case_annotator.forms.pedigree.Pedigree;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;

public class FamilyStudy extends BaseStudy<ObservableFamilyStudy> {

    @FXML
    private Label pedigreeLabel;
    @FXML
    private Pedigree pedigree;

    public FamilyStudy() {
        super(FamilyStudy.class.getResource("FamilyStudy.fxml"));
    }

    @FXML
    @Override
    protected void initialize() {
        super.initialize();
        pedigree.hpoProperty().bind(hpo);
        pedigree.diseaseIdentifierServiceProperty().bind(diseaseIdentifierService);
        pedigree.variantsProperty().bind(variantSummary.variants());
    }

    @Override
    protected void bind(ObservableFamilyStudy data) {
        super.bind(data);
        pedigree.dataProperty().bindBidirectional(data.pedigreeProperty());
    }

    @Override
    protected void unbind(ObservableFamilyStudy data) {
        super.unbind(data);
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
