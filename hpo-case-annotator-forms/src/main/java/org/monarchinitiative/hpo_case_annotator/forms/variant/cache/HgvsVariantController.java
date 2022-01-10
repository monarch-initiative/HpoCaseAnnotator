package org.monarchinitiative.hpo_case_annotator.forms.variant.cache;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifier;
import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.variant.unified.ObservableSeqSymVariant;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class HgvsVariantController {

    private final GeneIdentifierService geneService;

    @FXML
    private TextField geneSymbolTextField;
    @FXML
    private ComboBox<String> transcriptAccessionComboBox;
    @FXML
    private TextField variantTextField;

    public HgvsVariantController(GeneIdentifierService geneService) {
        this.geneService = geneService;
    }

    @FXML
    private void initialize() {
        Map<String, GeneIdentifier> symbolToGene = geneService.genes().stream()
                .collect(Collectors.toMap(GeneIdentifier::symbol, Function.identity()));

        AutoCompletionBinding<String> symbolAutoCompletion = TextFields.bindAutoCompletion(geneSymbolTextField, symbolToGene.keySet());
        symbolAutoCompletion.minWidthProperty().bind(geneSymbolTextField.widthProperty());

        geneSymbolTextField.textProperty().addListener((obs, old, novel) -> {
            transcriptAccessionComboBox.getItems().clear();
            if (novel != null) {
                GeneIdentifier geneIdentifier = symbolToGene.get(novel);
                if (geneIdentifier != null) {
                    List<String> transcripts = geneService.transcriptIdsForGene(geneIdentifier);
                    transcriptAccessionComboBox.getItems().addAll(transcripts);
                }
            }
        });
    }

    public ChangeListener<ObservableSeqSymVariant> variantChangeListener() {
        return (obs, old, novel) -> {
            // TODO - implement
        };
    }
}
