package org.monarchinitiative.hpo_case_annotator.forms.nvo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import org.monarchinitiative.hpo_case_annotator.forms.BaseBindingObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.pedigree.Pedigree;
import org.monarchinitiative.hpo_case_annotator.forms.publication.PublicationController;
import org.monarchinitiative.hpo_case_annotator.forms.publication.PublicationEditable;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.forms.variants.VariantSummary;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;


public class FamilyStudyController extends BaseBindingObservableDataController<ObservableFamilyStudy> {

    @FXML
    private Parent publication;
    @FXML
    private PublicationController publicationController;
    @FXML
    private Button editPublication;
    @FXML
    private Parent pedigree;
    @FXML
    private Pedigree pedigreeController;
    @FXML
    private Parent variantSummary;
    @FXML
    private VariantSummary variantSummaryController;

    @Override
    protected void initialize() {
        super.initialize();
        pedigreeController.variantsProperty().bind(variantSummaryController.variants());
    }

    @Override
    protected void bind(ObservableFamilyStudy data) {
        publicationController.dataProperty().bindBidirectional(data.publicationProperty());
        pedigreeController.dataProperty().bindBidirectional(data.pedigreeProperty());
        variantSummaryController.variants().bindBidirectional(data.variants());
    }

    @Override
    protected void unbind(ObservableFamilyStudy data) {
        publicationController.dataProperty().unbindBidirectional(data.publicationProperty());
        pedigreeController.dataProperty().unbindBidirectional(data.pedigreeProperty());
        variantSummaryController.variants().unbindBidirectional(data.variants());
    }

    @FXML
    private void editPublicationAction(ActionEvent e) {
        PublicationEditable component = new PublicationEditable();
        component.setInitialData(publicationController.getData());

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setHeaderText("Edit publication data:");
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
}
