package org.monarchinitiative.hpo_case_annotator.forms.v2.variant;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.monarchinitiative.hpo_case_annotator.core.reference.functional.FunctionalAnnotation;

public class FunctionalAnnotationController {

    @FXML
    private TableView<FunctionalAnnotation> functionalAnnotationTableView;
    @FXML
    private TableColumn<FunctionalAnnotation, String> geneTableColumn;
    @FXML
    private TableColumn<FunctionalAnnotation, String> transcriptTableColumn;
    @FXML
    private TableColumn<FunctionalAnnotation, String> annotationTableColumn;
    @FXML
    private TableColumn<FunctionalAnnotation, String> effectsTableColumn;

    @FXML
    private void initialize() {
        geneTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().geneSymbol()));
        transcriptTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().txAccession()));
        annotationTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().nucleotideAnnotation() + " " + cdf.getValue().proteinAnnotation()));
        effectsTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(String.join(", ", cdf.getValue().effect())));
    }

    public ObservableList<FunctionalAnnotation> functionalAnnotations() {
        return functionalAnnotationTableView.getItems();
    }
}
