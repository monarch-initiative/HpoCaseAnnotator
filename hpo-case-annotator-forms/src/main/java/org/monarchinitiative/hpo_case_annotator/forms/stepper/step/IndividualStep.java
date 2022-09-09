package org.monarchinitiative.hpo_case_annotator.forms.stepper.step;

import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.stage.StageStyle;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.component.IndividualIdsBindingComponent;
import org.monarchinitiative.hpo_case_annotator.forms.disease.IndividualDiseaseDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.IndividualPhenotypeDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.BaseStep;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.forms.variants.IndividualVariantGenotypesObservableData;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;
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
    private final ListProperty<ObservableCuratedVariant> variants = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<DiseaseIdentifierService> diseaseIdentifierService = new SimpleObjectProperty<>();

    @FXML
    private IndividualIdsBindingComponent individualIds;

    public IndividualStep() {
        super(IndividualStep.class.getResource("IndividualStep.fxml"));
    }

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
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
        // TODO - implement
        return Stream.of();
    }

    @Override
    public void invalidated(Observable obs) {
        // TODO - implement
    }

    @Override
    public Parent getContent() {
        return this;
    }

    @Override
    protected void bind(T data) {
        if (data != null)
            individualIds.dataProperty().bindBidirectional(data.individualProperty());
        else
            individualIds.setData(null);
    }

    @Override
    protected void unbind(T data) {
        if (data != null)
            individualIds.dataProperty().unbindBidirectional(data.individualProperty());
    }

    @FXML
    private void addEditPhenotypesAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(individualIds.getParent().getScene().getWindow());
        dialog.initStyle(StageStyle.DECORATED);
        // TODO - setup title
        dialog.setResizable(true);

        IndividualPhenotypeDataEdit phenotypeDataEdit = new IndividualPhenotypeDataEdit();
        phenotypeDataEdit.hpoProperty().bind(hpo);
        phenotypeDataEdit.setInitialData(data.get().getIndividual()); // TODO - check non null?
        dialog.getDialogPane().setContent(phenotypeDataEdit);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.OK_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));
        dialog.showAndWait()
                .ifPresent(shouldCommit -> {
                    if (shouldCommit) phenotypeDataEdit.commit();
                });
        e.consume();
    }

    @FXML
    private void addEditDiseasesAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(individualIds.getParent().getScene().getWindow());
        dialog.initStyle(StageStyle.DECORATED);
        // TODO - setup title
        dialog.setResizable(true);

        IndividualDiseaseDataEdit diseaseDataEdit = new IndividualDiseaseDataEdit();
        diseaseDataEdit.diseaseIdentifierServiceProperty().bind(diseaseIdentifierService);
        diseaseDataEdit.setInitialData(data.get().getIndividual()); // TODO - check non null?
        dialog.getDialogPane().setContent(diseaseDataEdit);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.OK_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));
        dialog.showAndWait()
                .ifPresent(shouldCommit -> {
                    if (shouldCommit) diseaseDataEdit.commit();
                });
        e.consume();
    }

    @FXML
    private void addEditGenotypesAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(individualIds.getParent().getScene().getWindow());
        dialog.initStyle(StageStyle.DECORATED);
        // TODO - setup title
        dialog.setResizable(true);

        IndividualVariantGenotypesObservableData content = new IndividualVariantGenotypesObservableData();
        content.setData(data.get().getIndividual()); // TODO - check non null?
        content.variantsProperty().bind(variants);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.OK_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));
        dialog.showAndWait()
//                .ifPresent(shouldCommit -> {
//                    if (shouldCommit) variantDataEdit.commit();
//                });
        ;
        e.consume();
    }
}
