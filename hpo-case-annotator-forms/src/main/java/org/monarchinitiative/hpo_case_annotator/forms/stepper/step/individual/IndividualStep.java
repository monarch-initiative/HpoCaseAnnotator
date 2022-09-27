package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.stage.StageStyle;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.core.mining.NamedEntityFinder;
import org.monarchinitiative.hpo_case_annotator.forms.component.IndividualIdsBindingComponent;
import org.monarchinitiative.hpo_case_annotator.forms.disease.IndividualDiseaseDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.IndividualPhenotypeDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.BaseStep;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.forms.variants.IndividualVariantGenotypesObservableData;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividualStudy;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.util.stream.Stream;

/**
 * <h2>Properties</h2>
 * {@link IndividualStep} needs the following properties to be set in order to work.
 * <ul>
 *     <li>{@link #diseaseIdentifierServiceProperty()}</li>
 *     <li>{@link #hpoProperty()}</li>
 * </ul>
 * <p>
 * Furthermore, {@link IndividualStep} keeps track of variants of the study via {@link #variantsProperty()}.
 */
public class IndividualStep<T extends ObservableIndividualStudy> extends BaseStep<T> {

    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();
    private final ObjectProperty<NamedEntityFinder> namedEntityFinder = new SimpleObjectProperty<>();
    private final ObjectProperty<DiseaseIdentifierService> diseaseIdentifierService = new SimpleObjectProperty<>();
    private final ListProperty<ObservableCuratedVariant> variants = new SimpleListProperty<>(FXCollections.observableArrayList());

    @FXML
    private IndividualIdsBindingComponent individualIds;

    public IndividualStep() {
        super(IndividualStep.class.getResource("IndividualStep.fxml"));
    }

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
    }

    public ObjectProperty<NamedEntityFinder> namedEntityFinderProperty() {
        return namedEntityFinder;
    }

    public ListProperty<ObservableCuratedVariant> variantsProperty() {
        return variants;
    }

    public ObjectProperty<DiseaseIdentifierService> diseaseIdentifierServiceProperty() {
        return diseaseIdentifierService;
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected Stream<Observable> dependencies() {
        return Stream.of(individualIds);
    }

    @Override
    public void invalidated(Observable obs) {
        // TODO - implement
    }

    @Override
    protected void bind(T data) {
        // set flag
        try {
            valueIsBeingSetProgrammatically = true;
            if (data != null) {

                if (data.getIndividual() == null)
                    // No point doing this without having a value to bind to.
                    data.setIndividual(new ObservableIndividual());

                individualIds.dataProperty().bindBidirectional(data.individualProperty());
            } else
                individualIds.setData(null);
        } finally {
            valueIsBeingSetProgrammatically = false;
        }
    }

    @Override
    protected void unbind(T data) {
        if (data != null)
            individualIds.dataProperty().unbindBidirectional(data.individualProperty());
    }

    @FXML
    private void addEditPhenotypesAction(ActionEvent e) {
        Dialog<Boolean> dialog = prepareEditDialog();

        dialog.setTitle("Add phenotypic features");

        IndividualPhenotypeDataEdit phenotypeDataEdit = new IndividualPhenotypeDataEdit();
        phenotypeDataEdit.hpoProperty().bind(hpo);
        phenotypeDataEdit.namedEntityFinderProperty().bind(namedEntityFinder);
        phenotypeDataEdit.setInitialData(data.get().getIndividual()); // TODO - check non null?
        dialog.getDialogPane().setContent(phenotypeDataEdit);
        dialog.showAndWait()
                .ifPresent(shouldCommit -> {
                    if (shouldCommit) phenotypeDataEdit.commit();
                });
        e.consume();
    }

    @FXML
    private void addEditDiseasesAction(ActionEvent e) {
        Dialog<Boolean> dialog = prepareEditDialog();
        dialog.setTitle("Set diseases"); // TODO - nicer title

        IndividualDiseaseDataEdit diseaseDataEdit = new IndividualDiseaseDataEdit();
        diseaseDataEdit.diseaseIdentifierServiceProperty().bind(diseaseIdentifierService);
        diseaseDataEdit.setInitialData(data.get().getIndividual());
        dialog.getDialogPane().setContent(diseaseDataEdit);
        dialog.showAndWait()
                .ifPresent(shouldCommit -> {
                    if (shouldCommit) diseaseDataEdit.commit();
                });
        e.consume();
    }

    @FXML
    private void addEditGenotypesAction(ActionEvent e) {
        Dialog<Boolean> dialog = prepareEditDialog();
        dialog.setTitle("Set variant genotypes");

        // We cannot cancel genotype change, hence no Cancel button.
        dialog.getDialogPane().getButtonTypes().setAll(DialogUtil.OK_BUTTONS);

        IndividualVariantGenotypesObservableData content = new IndividualVariantGenotypesObservableData();
        content.setData(data.get().getIndividual()); // TODO - check non null?
        content.variantsProperty().bind(variants);
        dialog.getDialogPane().setContent(content);

        dialog.showAndWait();
        e.consume();
    }

    private Dialog<Boolean> prepareEditDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(individualIds.getParent().getScene().getWindow());
        dialog.initStyle(StageStyle.DECORATED);
        dialog.setResizable(true);
        // Bind "this" to "that", not "that" to "this"!
        Bindings.bindContent(dialog.getDialogPane().getStylesheets(), individualIds.getParent().getStylesheets());

        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.OK_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));

        return dialog;
    }
}
