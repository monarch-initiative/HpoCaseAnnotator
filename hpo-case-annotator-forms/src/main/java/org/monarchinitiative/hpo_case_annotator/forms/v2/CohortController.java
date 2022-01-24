package org.monarchinitiative.hpo_case_annotator.forms.v2;

import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.v2.individual.IndividualController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;

import java.net.URL;

public class CohortController extends BaseIndividualCollectionController<ObservableIndividual, IndividualController> {


    public CohortController(HCAControllerFactory controllerFactory) {
        super(controllerFactory);
    }

    @FXML
    protected void initialize() {
        super.initialize();

    }

    @Override
    protected ObservableIndividual newInstance() {
        return new ObservableIndividual();
    }

    @Override
    protected URL getResourceUrl() {
        return IndividualController.class.getResource("Individual.fxml");
    }

}
