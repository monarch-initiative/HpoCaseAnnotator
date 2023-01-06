package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import org.monarchinitiative.hpo_case_annotator.forms.base.FlowPaneObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTimeElementSummary;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;

import static javafx.beans.binding.Bindings.*;

public class PhenotypeListCellGraphics extends FlowPaneObservableDataComponent<ObservablePhenotypicFeature> {

    @FXML
    private TitledLabel term;
    @FXML
    private TitledLabel status;
    @FXML
    private TitledTimeElementSummary onset;
    @FXML
    private TitledTimeElementSummary resolution;

    public PhenotypeListCellGraphics() {
        FXMLLoader loader = new FXMLLoader(PhenotypeListCellGraphics.class.getResource("PhenotypeListCellGraphics.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void initialize() {
        ObjectBinding<TermId> termId = select(data, "termId");
        StringBinding termIdValue = createStringBinding(() -> termId.get() == null ? null : termId.get().getValue(), termId);
        term.nameProperty().bind(termIdValue);
        term.textProperty().bind(selectString(data, "label"));
        status.textProperty().bind(when(selectBoolean(data, "excluded")).then("Excluded").otherwise("Present"));
        onset.timeElementProperty().bind(select(data, "onset"));
        resolution.timeElementProperty().bind(select(data, "resolution"));
    }
}
