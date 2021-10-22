package org.monarchinitiative.hpo_case_annotator.forms.variant;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifier;
import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifierService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class HgvsVariantController {

    private final GeneIdentifierService geneService;

    public TextField geneSymbolTextField;
    public ComboBox<String> transcriptAccessionComboBox;
    public TextField variantTextField;

    public HgvsVariantController(GeneIdentifierService geneService) {
        this.geneService = geneService;
    }

    public void initialize() {
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

}
