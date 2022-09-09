package org.monarchinitiative.hpo_case_annotator.forms.study;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.monarchinitiative.hpo_case_annotator.forms.StudyResources;
import org.monarchinitiative.hpo_case_annotator.forms.StudyResourcesAware;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxBindingObservableDataComponent;

import org.monarchinitiative.hpo_case_annotator.forms.metadata.Metadata;
import org.monarchinitiative.hpo_case_annotator.forms.publication.Publication;
import org.monarchinitiative.hpo_case_annotator.forms.publication.PublicationEditable;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.forms.variants.VariantSummary;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePublication;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;

import java.io.IOException;
import java.net.URL;

import static javafx.beans.binding.Bindings.select;

/**
 * This controller is the base for all {@link org.monarchinitiative.hpo_case_annotator.model.v2.Study} types.
 * {@link BaseStudyComponent} needs the properties specified by {@link StudyResources} to work.
 * <p>
 */
public abstract class BaseStudyComponent<T extends ObservableStudy>
        extends VBoxBindingObservableDataComponent<T>
        implements StudyResourcesAware {

    protected final StudyResources studyResources = new StudyResources();

    @FXML
    private ScrollPane studyScrollPane;
    // ------------------------------------ PUBLICATION SECTION ----------------------------------------------------- //
    @FXML
    private StackPane publicationPane;
    @FXML
    private Publication publication;
    @FXML
    private Button editPublication;

    // ------------------------------------ VARIANTS SECTION -------------------------------------------------------- //
    @FXML
    protected VariantSummary variantSummary;

    // ------------------------------------ METADATA SECTION -------------------------------------------------------- //
    @FXML
    private Metadata metadataSummary;

    protected BaseStudyComponent(URL location) {
        FXMLLoader loader = new FXMLLoader(location);
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StudyResources getStudyResources() {
        return studyResources;
    }

    @Override
    protected void initialize() {
        super.initialize();
        editPublication.visibleProperty().bind(publicationPane.hoverProperty());
        publication.disableProperty().bind(select(data, "publication").isNotNull());

        variantSummary.functionalAnnotationRegistryProperty().bind(studyResources.functionalAnnotationRegistryProperty());
        variantSummary.genomicAssemblyRegistryProperty().bind(studyResources.genomicAssemblyRegistryProperty());
    }

    @Override
    protected void bind(T data) {
        if (data != null) {
            publication.dataProperty().bindBidirectional(data.publicationProperty());

            variantSummary.variants().bindBidirectional(data.variantsProperty());
            metadataSummary.dataProperty().bind(data.studyMetadataProperty());
        }
    }

    @Override
    protected void unbind(T data) {
        if (data != null) {
            publication.dataProperty().unbindBidirectional(data.publicationProperty());

            variantSummary.variants().unbindBidirectional(data.variantsProperty());
            metadataSummary.dataProperty().unbind();
        }
    }
    @FXML
    private void editPublicationAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit publication data");
        dialog.initOwner(studyScrollPane.getParent().getScene().getWindow());

        PublicationEditable component = new PublicationEditable();
        ObservablePublication pub = publication.getData() == null
                ? new ObservablePublication()
                : publication.getData();
        component.setInitialData(pub);
        dialog.getDialogPane().setContent(component);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.UPDATE_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));

        dialog.showAndWait()
                .ifPresent(shouldUpdate -> {
                    if (shouldUpdate) {
                        component.commit();
                        if (publication.getData() == null)
                            publication.setData(pub);
                    }
                });

        e.consume();
    }

    @FXML
    private void publicationNavMouseClicked(ActionEvent e) {
//        ensureVisible(studyScrollPane, publicationLabel);
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

}
