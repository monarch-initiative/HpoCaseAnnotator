package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.model.PhenotypeDescriptionSimple;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.OntologyTreeController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypeEntryController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypicFeatureController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypicFeaturesTableController;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.time.Period;


public class PhenotypeController {

    @FXML
    private VBox ontologyTree;
    @FXML
    private OntologyTreeController ontologyTreeController;
    @FXML
    private TabPane phenotypeEntry;
    @FXML
    private PhenotypeEntryController phenotypeEntryController;
    @FXML
    private VBox phenotypicFeature;
    @FXML
    private PhenotypicFeatureController phenotypicFeatureController;
    @FXML
    private VBox phenotypicFeaturesTable;
    @FXML
    private PhenotypicFeaturesTableController phenotypicFeaturesTableController;

    public void initialize() {
        phenotypicFeaturesTableController.selectedPhenotypicDescriptionProperty().addListener((obs, old, novel) -> {
            phenotypicFeatureController.setPhenotypicFeature(novel);
        });
        phenotypicFeatureController.disablePhenotypicFeature().setValue(true);
        addSampleValues();
    }

    private void addSampleValues() {
        // TODO: 10/26/21 remove
        phenotypicFeaturesTableController.addFeatures(PhenotypeDescriptionSimple.of(TermId.of("HP:123456"), "ABC", Period.ZERO, Period.of(1, 2, 3), true),
                PhenotypeDescriptionSimple.of(TermId.of("HP:987654"), "DEF", Period.of(12, 0, 1), Period.of(12, 10, 3), false),
                PhenotypeDescriptionSimple.of(TermId.of("HP:456789"), "GHI", Period.of(15, 9, 4), Period.of(20, 7, 6), true),
                PhenotypeDescriptionSimple.of(TermId.of("HP:987654"), "JKL", Period.of(28, 3, 15), Period.of(40, 10, 3), false),
                PhenotypeDescriptionSimple.of(TermId.of("HP:111111"), "MNO", Period.ZERO, Period.of(80, 0, 0), true));
    }

}
