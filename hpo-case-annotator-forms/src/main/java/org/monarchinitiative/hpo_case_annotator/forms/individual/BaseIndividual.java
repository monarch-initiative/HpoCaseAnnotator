package org.monarchinitiative.hpo_case_annotator.forms.individual;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.BaseIndividualIdsDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.disease.BaseDiseaseDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.disease.DiseaseTable;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.BasePhenotypeDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypeTable;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.forms.util.Utils;
import org.monarchinitiative.hpo_case_annotator.forms.variants.VariantGenotypeTable;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.IOException;
import java.net.URL;

import static javafx.beans.binding.Bindings.*;

/**
 * <h2>Properties</h2>
 * {@link BaseIndividual} needs the following properties to be set in order to work.
 * <ul>
 *     <li>{@link #hpoProperty()}</li>
 *     <li>{@link #diseaseIdentifierServiceProperty()}</li>
 * </ul>
 * <p>
 * Furthermore, the component tracks {@link #variantsProperty()} to keep variant genotype table in sync with variants
 * in the study.
 */
public abstract class BaseIndividual<T extends BaseObservableIndividual> extends VBoxObservableDataComponent<T> {

    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();
    private final ObjectProperty<DiseaseIdentifierService> diseaseIdentifierService = new SimpleObjectProperty<>();
    private final ListProperty<ObservableCuratedVariant> variants = new SimpleListProperty<>(FXCollections.observableArrayList());

    @FXML
    private StackPane phenotypePane;
    @FXML
    private Button editPhenotypeButton;
    @FXML
    private PhenotypeTable phenotypeTable;
    @FXML
    private StackPane diseasePane;
    @FXML
    private Button editDiseasesButton;
    @FXML
    private DiseaseTable diseaseTable;
    @FXML
    private VariantGenotypeTable variantGenotypeTable;

    protected BaseIndividual(URL location) {
        FXMLLoader loader = new FXMLLoader(location);
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ListProperty<ObservableCuratedVariant> variantsProperty() {
        return variants;
    }

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
    }

    public ObjectProperty<DiseaseIdentifierService> diseaseIdentifierServiceProperty() {
        return diseaseIdentifierService;
    }

    @Override
    protected void initialize() {
        // Phenotypes table view
        phenotypeTable.itemsProperty().bind(select(data, "phenotypicFeatures"));
        editPhenotypeButton.visibleProperty().bind(phenotypePane.hoverProperty().and(data.isNotNull()));

        // Diseases table view
        diseaseTable.itemsProperty().bind(select(data, "diseaseStates"));
        editDiseasesButton.visibleProperty().bind(diseasePane.hoverProperty().and(data.isNotNull()));

        // Genotypes table view
        variantGenotypeTable.dataProperty().bind(data);
        variantGenotypeTable.variantsProperty().bind(variants);
    }

    @FXML
    private void editIdentifiersAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(getParent().getScene().getWindow());
        dialog.titleProperty().bind(concat("Edit identifiers for ", Utils.nullableStringProperty(data, "id")));
        BaseIndividualIdsDataEdit<T> content = getIdsDataEdit();
        content.setInitialData(data.getValue());

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.UPDATE_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));

        dialog.showAndWait()
                .ifPresent(shouldUpdate -> {
                    if (shouldUpdate) content.commit();
                });

        e.consume();
    }

    protected abstract BaseIndividualIdsDataEdit<T> getIdsDataEdit();

    @FXML
    private void editPhenotypeAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(getParent().getScene().getWindow());
        dialog.initStyle(StageStyle.DECORATED);
        dialog.titleProperty().bind(concat("Edit phenotype features for ", Utils.nullableStringProperty(data, "id")));
        dialog.setResizable(true);

        BasePhenotypeDataEdit<T> content = getPhenotypeDataEdit();
        content.hpoProperty().bind(hpo);
        // Data is not null due to triggering button not visible when data is null (see `initialize()`).
        content.setInitialData(data.get());
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.OK_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));
        dialog.showAndWait()
                .ifPresent(shouldCommit -> {
                    if (shouldCommit) content.commit();
                });
        e.consume();
    }

    protected abstract BasePhenotypeDataEdit<T> getPhenotypeDataEdit();

    @FXML
    private void editDiseasesAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(getParent().getScene().getWindow());
        dialog.initStyle(StageStyle.DECORATED);
        dialog.titleProperty().bind(concat("Edit diseases for ", Utils.nullableStringProperty(data, "id")));
        dialog.setResizable(true);

        BaseDiseaseDataEdit<T> content = getDiseaseDataEdit();
        content.diseaseIdentifierServiceProperty().bind(diseaseIdentifierService);
        // Data is not null due to triggering button not visible when data is null (see `initialize()`).
        content.setInitialData(data.get());
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.OK_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));
        dialog.showAndWait()
                .ifPresent(shouldCommit -> {
                    if (shouldCommit) content.commit();
                });
        e.consume();
    }

    protected abstract BaseDiseaseDataEdit<T> getDiseaseDataEdit();

}
