package org.monarchinitiative.hpo_case_annotator.forms.nvo;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.forms.individual.Individual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividualStudy;

public class IndividualStudy extends BaseStudy<ObservableIndividualStudy> {

    @FXML
    private Individual individual;

    public IndividualStudy() {
        super(IndividualStudy.class.getResource("IndividualStudy.fxml"));
    }

    @FXML
    @Override
    protected void initialize() {
        super.initialize();

        individual.hpoProperty().bind(hpo);
        individual.diseaseIdentifierServiceProperty().bind(diseaseIdentifierService);
        individual.variantsProperty().bind(variantSummary.variants());
    }

    @Override
    protected void bind(ObservableIndividualStudy data) {
        super.bind(data);
        if (data != null)
            individual.dataProperty().bindBidirectional(data.individualProperty());
    }

    @Override
    protected void unbind(ObservableIndividualStudy data) {
        super.unbind(data);
        if (data != null)
            individual.dataProperty().unbindBidirectional(data.individualProperty());
    }

    @FXML
    private void individualNavMouseClicked(ActionEvent e) {
//        ensureVisible(studyScrollPane, pedigreeLabel);
        System.err.println("Individual NAV item was clicked on");
        e.consume();
    }
}
