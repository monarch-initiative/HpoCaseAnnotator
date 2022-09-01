package org.monarchinitiative.hpo_case_annotator.forms.individual;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.VBoxObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.disease.DiseaseDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.disease.DiseaseTable;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypeDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypeTable;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.forms.util.Utils;
import org.monarchinitiative.hpo_case_annotator.forms.variants.VariantGenotypeTable;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.IOException;
import java.net.URL;

import static javafx.beans.binding.Bindings.*;

public abstract class BaseIndividual<T extends BaseObservableIndividual> extends VBoxObservableDataController<T> {

    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();
    private final ObjectProperty<DiseaseIdentifierService> diseaseIdentifierService = new SimpleObjectProperty<>();
    private final ListProperty<CuratedVariant> variants = new SimpleListProperty<>(FXCollections.observableArrayList());

//    @FXML
//    private Label summary;
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

    protected BaseIndividual(URL url) {
        FXMLLoader loader = new FXMLLoader(url);
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
        // Phenotypes table view
        phenotypeTable.itemsProperty().bind(select(data, "phenotypicFeatures"));
        editPhenotypeButton.visibleProperty().bind(phenotypePane.hoverProperty());

        // Diseases table view
        diseaseTable.itemsProperty().bind(select(data, "diseaseStates"));
        editDiseasesButton.visibleProperty().bind(diseasePane.hoverProperty());

        // Genotypes table view
        variantGenotypeTable.dataProperty().bind(data);
        variantGenotypeTable.variantsProperty().bind(variants);
    }

    public ObservableList<CuratedVariant> getVariants() {
        return variants.get();
    }

    public ListProperty<CuratedVariant> variantsProperty() {
        return variants;
    }

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
    }

    public ObjectProperty<DiseaseIdentifierService> diseaseIdentifierServiceProperty() {
        return diseaseIdentifierService;
    }

    @FXML
    protected abstract void editIdentifiersAction(ActionEvent e);

    @FXML
    private void editPhenotypeAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(getParent().getScene().getWindow());
        dialog.initStyle(StageStyle.DECORATED);
        dialog.titleProperty().bind(concat("Edit phenotype features for ", Utils.nullableStringProperty(data, "id")));
        dialog.setResizable(true);

        PhenotypeDataEdit<T> phenotypeDataEdit = getPhenotypeDataEdit();
        phenotypeDataEdit.hpoProperty().bind(hpo);
        phenotypeDataEdit.setInitialData(data.get()); // TODO - check non null?
        dialog.getDialogPane().setContent(phenotypeDataEdit);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.OK_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));
        dialog.showAndWait()
                .ifPresent(shouldCommit -> {
                    if (shouldCommit) phenotypeDataEdit.commit();
                });
        e.consume();
    }

    protected abstract PhenotypeDataEdit<T> getPhenotypeDataEdit();

    @FXML
    private void editDiseasesAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(getParent().getScene().getWindow());
        dialog.initStyle(StageStyle.DECORATED);
        // TODO - setup title
        dialog.setResizable(true);

        DiseaseDataEdit<T> diseaseDataEdit = getDiseaseDataEdit();
        diseaseDataEdit.diseaseIdentifierServiceProperty().bind(diseaseIdentifierService);
        diseaseDataEdit.setInitialData(data.get()); // TODO - check non null?
        dialog.getDialogPane().setContent(diseaseDataEdit);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.OK_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));
        dialog.showAndWait()
                .ifPresent(shouldCommit -> {
                    if (shouldCommit) diseaseDataEdit.commit();
                });
        e.consume();
    }

    protected abstract DiseaseDataEdit<T> getDiseaseDataEdit();

}
