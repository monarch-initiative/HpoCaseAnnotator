package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.monarchinitiative.hpo_case_annotator.forms.DataEditController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Objects;
import java.util.function.Function;

public class PhenotypeView implements DataEditController<ObservablePedigreeMember> {

    private final Function<TermId, Term> termSource;

    private ObservablePedigreeMember item;

    @FXML
    private TableView<ObservablePhenotypicFeature> phenotypeTerms;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, TermId> termIdColumn;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, String> termLabel;
    @FXML
    private PhenotypicFeature phenotypicFeature;

    /**
     * @param termSource a function to get ahold of {@link TermId} details. The function returns {@code null}
     *                   for unknown {@link TermId}s.
     */
    public PhenotypeView(Function<TermId, Term> termSource) {
        this.termSource = Objects.requireNonNull(termSource);
    }

    @FXML
    private void initialize() {
        phenotypeTerms.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        termIdColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().id()));
        termLabel.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(getTermLabel(cdf)));

        phenotypicFeature.dataProperty().bind(phenotypeTerms.getSelectionModel().selectedItemProperty());
    }

    private String getTermLabel(TableColumn.CellDataFeatures<ObservablePhenotypicFeature, String> cdf) {
        Term term = termSource.apply(cdf.getValue().id());
        return term == null ? null : term.getName();
    }

    @Override
    public void setInitialData(ObservablePedigreeMember data) {
        item = Objects.requireNonNull(data);
        phenotypeTerms.getItems().addAll(data.getObservablePhenotypicFeatures());
    }

    @Override
    public void commit() {
        // TODO - implement
    }
}