package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.v2.ontotree.OntologyTreeBrowserController;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype")
public class BrowseHpoController {

    @FXML
    private TitledTextField searchField;
    @FXML
    private VBox ontologyTreeBrowser;
    @FXML
    private OntologyTreeBrowserController ontologyTreeBrowserController;
    @FXML
    private VBox phenotypicFeature;
    @FXML
    private PhenotypicFeatureController phenotypicFeatureController;


    @FXML
    private void initialize() {

    }
}

