package org.monarchinitiative.hpo_case_annotator.forms.nvo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.monarchinitiative.hpo_case_annotator.forms.BaseBindingObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.metadata.Metadata;
import org.monarchinitiative.hpo_case_annotator.forms.pedigree.PedigreeController;
import org.monarchinitiative.hpo_case_annotator.forms.publication.Publication;
import org.monarchinitiative.hpo_case_annotator.forms.publication.PublicationEditable;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.forms.variants.VariantSummaryController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller("nvoFamilyStudyController") // TODO - rename
public class FamilyStudyController extends BaseBindingObservableDataController<ObservableFamilyStudy> {

    @FXML
    private ScrollPane studyScrollPane;
    @FXML
    private Label publicationLabel;
    @FXML
    private Publication publication;
    @FXML
    private Button editPublication;
    @FXML
    private Label pedigreeLabel;
    @FXML
    private Parent pedigree;
    @FXML
    private PedigreeController pedigreeController;
    @FXML
    private Label variantsLabel;
    @FXML
    private Parent variantSummary;
    @FXML
    private VariantSummaryController variantSummaryController;
    @FXML
    private Label metadataLabel;
    @FXML
    private Metadata metadataSummary;

    @Override
    protected void initialize() {
        super.initialize();
        pedigreeController.variantsProperty().bind(variantSummaryController.variants());
    }

    @Override
    protected void bind(ObservableFamilyStudy data) {
        publication.dataProperty().bindBidirectional(data.publicationProperty());
        pedigreeController.dataProperty().bindBidirectional(data.pedigreeProperty());
        variantSummaryController.variants().bindBidirectional(data.variants());
        metadataSummary.dataProperty().bind(data.studyMetadataProperty());
    }

    @Override
    protected void unbind(ObservableFamilyStudy data) {
        publication.dataProperty().unbindBidirectional(data.publicationProperty());
        pedigreeController.dataProperty().unbindBidirectional(data.pedigreeProperty());
        variantSummaryController.variants().unbindBidirectional(data.variants());
        metadataSummary.dataProperty().unbind();
    }

    @FXML
    private void editPublicationAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit publication data");
        dialog.initOwner(studyScrollPane.getParent().getScene().getWindow());

        PublicationEditable component = new PublicationEditable();
        component.setInitialData(publication.getData());
        dialog.getDialogPane().setContent(component);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.UPDATE_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));

        dialog.showAndWait()
                .ifPresent(shouldUpdate -> {if (shouldUpdate) component.commit();});

        e.consume();
    }

    @FXML
    private void publicationSectionMouseEntered() {
        editPublication.setVisible(true);
    }

    @FXML
    private void publicationSectionMouseExited() {
        editPublication.setVisible(false);
    }

    @FXML
    private void publicationNavMouseClicked(ActionEvent e) {
//        ensureVisible(studyScrollPane, publicationLabel);
        e.consume();
    }

    @FXML
    private void pedigreeNavMouseClicked(ActionEvent e) {
//        ensureVisible(studyScrollPane, pedigreeLabel);
        e.consume();
    }

    @FXML
    private void variantsNavMouseClicked(ActionEvent e) {
//        ensureVisible(studyScrollPane, variantsLabel);
        e.consume();
    }

    @FXML
    private void metadataNavMouseClicked(ActionEvent e) {
//        ensureVisible(studyScrollPane, metadataLabel);
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
