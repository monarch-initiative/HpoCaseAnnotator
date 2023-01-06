package org.monarchinitiative.hpo_case_annotator.forms.variants.input;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.core.reference.functional.FunctionalAnnotation;

import java.io.IOException;

/**
 * A component for presenting a list of {@link FunctionalAnnotation} for a {@link org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant}.
 */
public class FunctionalAnnotationTable extends VBox {

    @FXML
    private TableView<FunctionalAnnotation> functionalAnnotationTableView;
    @FXML
    private TableColumn<FunctionalAnnotation, String> geneTableColumn;
    @FXML
    private TableColumn<FunctionalAnnotation, String> transcriptTableColumn;
    @FXML
    private TableColumn<FunctionalAnnotation, String> effectsTableColumn;
    @FXML
    private TableColumn<FunctionalAnnotation, String> hgvsAnnotationTableColumn;

    public FunctionalAnnotationTable() {
        FXMLLoader loader = new FXMLLoader(FunctionalAnnotationTable.class.getResource("FunctionalAnnotationTable.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        geneTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().geneSymbol()));
        transcriptTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().txAccession()));
        hgvsAnnotationTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().nucleotideAnnotation() + " " + cdf.getValue().proteinAnnotation()));
        effectsTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(String.join(", ", cdf.getValue().effect())));
    }

    public ObjectProperty<ObservableList<FunctionalAnnotation>> functionalAnnotations() {
        return functionalAnnotationTableView.itemsProperty();
    }
}
