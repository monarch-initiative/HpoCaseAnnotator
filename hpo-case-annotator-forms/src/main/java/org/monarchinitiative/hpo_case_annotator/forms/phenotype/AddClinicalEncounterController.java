package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.IndividualIdsComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementEditableComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("singleton")
@Controller
public class AddClinicalEncounterController {

    @FXML
    private IndividualIdsComponent individualIds;
    @FXML
    private TimeElementEditableComponent encounterTime;
    @FXML
    private VBox browseHpo;
    @FXML
    private BrowseHpoController browseHpoController;
    @FXML
    private VBox textMining;
    @FXML
    private TextMiningController textMiningController;
    @FXML
    private PhenotypeTable phenotypeTable;

    @FXML
    private void initialize() {
        
    }

    public ObjectProperty<ObservableList<ObservablePhenotypicFeature>> itemsProperty() {
        return phenotypeTable.itemsProperty();
    }

}
