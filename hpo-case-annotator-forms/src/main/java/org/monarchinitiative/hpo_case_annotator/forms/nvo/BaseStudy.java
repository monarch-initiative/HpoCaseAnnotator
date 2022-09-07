package org.monarchinitiative.hpo_case_annotator.forms.nvo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxBindingObservableDataComponent;

import org.monarchinitiative.hpo_case_annotator.forms.metadata.Metadata;
import org.monarchinitiative.hpo_case_annotator.forms.publication.Publication;
import org.monarchinitiative.hpo_case_annotator.forms.publication.PublicationEditable;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.forms.variants.VariantSummary;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.IOException;
import java.net.URL;

public abstract class BaseStudy<T extends ObservableStudy> extends VBoxBindingObservableDataComponent<T> {

    protected final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();
    protected final ObjectProperty<DiseaseIdentifierService> diseaseIdentifierService = new SimpleObjectProperty<>();

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

    protected BaseStudy(URL location) {
        FXMLLoader loader = new FXMLLoader(location);
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
    }

    public ObjectProperty<DiseaseIdentifierService> diseaseIdentifierServiceProperty() {
        return diseaseIdentifierService;
    }

    @Override
    protected void initialize() {
        super.initialize();
        editPublication.visibleProperty().bind(publicationPane.hoverProperty());
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
        component.setInitialData(publication.getData());
        dialog.getDialogPane().setContent(component);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.UPDATE_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));

        dialog.showAndWait()
                .ifPresent(shouldUpdate -> {if (shouldUpdate) component.commit();});

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
