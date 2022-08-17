package org.monarchinitiative.hpo_case_annotator.forms.pedigree;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;
import org.monarchinitiative.hpo_case_annotator.forms.component.PedigreeMemberIdsComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.PedigreeMemberIdsEditableComponent;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PedigreeMemberPhenotypeDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypeTable;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.forms.util.GenotypeStringConverter;
import org.monarchinitiative.hpo_case_annotator.forms.util.TermIdTableCell;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.hpo_case_annotator.observable.v2.*;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.svart.CoordinateSystem;
import org.monarchinitiative.svart.Strand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static javafx.beans.binding.Bindings.*;

public class PedigreeMember extends TitledPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(PedigreeMember.class);
    private static final Map<Sex, Map<Boolean, Image>> SEX_AFFECTED_ICONS = loadSexAffectedIconsMap();
    private final ListProperty<CuratedVariant> variants = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();
    private final ObjectProperty<ObservablePedigreeMember> item = new SimpleObjectProperty<>();

    @FXML
    private PedigreeMemberTitle pedigreeMemberTitle;
    @FXML
    private Button editIdentifiersButton;
    @FXML
    private PedigreeMemberIdsComponent pedigreeMemberIdentifiers;
    @FXML
    private Button editPhenotypeButton;

    @FXML
    private PhenotypeTable phenotypeTable;
    @FXML
    private Button editDiseasesButton;
    @FXML
    private TableView<ObservableDiseaseStatus> diseaseTable;
    @FXML
    private TableColumn<ObservableDiseaseStatus, String> diseaseStatusColumn;
    @FXML
    private TableColumn<ObservableDiseaseStatus, TermId> diseaseIdColumn;
    @FXML
    private TableColumn<ObservableDiseaseStatus, String> diseaseNameColumn;
    @FXML
    private TableView<CuratedVariant> genotypesTable;
    @FXML
    private TableColumn<CuratedVariant, Genotype> genotypeTableColumn;
    @FXML
    private TableColumn<CuratedVariant, String> variantIdTableColumn;
    @FXML
    private TableColumn<CuratedVariant, String> genomicAssemblyTableColumn;
    @FXML
    private TableColumn<CuratedVariant, String> contigTableColumn;
    @FXML
    private TableColumn<CuratedVariant, String> startTableColumn;
    @FXML
    private TableColumn<CuratedVariant, String> endTableColumn;
    @FXML
    private TableColumn<CuratedVariant, String> refTableColumn;
    @FXML
    private TableColumn<CuratedVariant, String> altTableColumn;

    public PedigreeMember() {
        FXMLLoader loader = new FXMLLoader(PedigreeMember.class.getResource("PedigreeMember.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        // Individual credentials
        StringBinding individualId = nullableStringProperty(item, "id");
        pedigreeMemberTitle.probandId.textProperty().bind(individualId);
        pedigreeMemberTitle.icon.imageProperty().bind(createIndividualImageBinding());
        pedigreeMemberTitle.summary.textProperty().bind(individualSummary(item));

        pedigreeMemberIdentifiers.dataProperty().bind(item);

        // Phenotypes table view
        phenotypeTable.itemsProperty().bind(select(item, "phenotypicFeatures"));

        // Diseases table view
        diseaseTable.itemsProperty().bind(select(item, "diseaseStates"));
        diseaseStatusColumn.setCellValueFactory(cdf -> when(cdf.getValue().excludedProperty()).then("Excluded").otherwise("Present"));
        diseaseIdColumn.setCellValueFactory(cdf -> select(cdf.getValue(), "diseaseId", "diseaseId")); // Yeah, twice.
        diseaseIdColumn.setCellFactory(column -> new TermIdTableCell<>());
        diseaseNameColumn.setCellValueFactory(cdf -> select(cdf.getValue(), "diseaseId", "diseaseName"));

        // Genotypes table view
        genotypeTableColumn.setCellValueFactory(cdf -> extractGenotype(cdf.getValue().md5Hex(), item));
        genotypeTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(GenotypeStringConverter.getInstance(), Genotype.values()));

        variantIdTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().id()));
        genomicAssemblyTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getGenomicAssembly()));
        contigTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getVariant().contigName()));
        startTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(Formats.NUMBER_FORMAT.format(cdf.getValue().getVariant().startOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.oneBased()))));
        endTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(Formats.NUMBER_FORMAT.format(cdf.getValue().getVariant().endOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.oneBased()))));
        refTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getVariant().ref()));
        altTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getVariant().alt()));

        variants.addListener(handleAddingAndRemovingVariant(item));
        genotypesTable.itemsProperty().bind(variants);
    }

    private static ObservableValue<Genotype> extractGenotype(String variantMd5Hex, ObjectProperty<ObservablePedigreeMember> member) {
        ObservablePedigreeMember m = member.get();
        if (m == null)
            return null;

        return m.getGenotypes().stream()
                .filter(g -> g.getMd5Hex().equals(variantMd5Hex))
                .findFirst()
                .map(ObservableVariantGenotype::genotypeProperty)
                .orElse(null);
    }

    private static ListChangeListener<? super CuratedVariant> handleAddingAndRemovingVariant(ObjectProperty<ObservablePedigreeMember> item) {
        return change -> {
            ObservablePedigreeMember member = item.get();
            if (member == null)
                // Nothing to do
                return;

            while (change.next()) {
                if (change.wasAdded()) {
                    for (CuratedVariant cv : change.getAddedSubList()) {
                        if (member.getGenotypes().stream().anyMatch(ovg -> ovg.getMd5Hex().equals(cv.md5Hex()))) {
                            LOGGER.warn("Adding variant but the individual already has a genotype: {}", cv.md5Hex());
                        } else {
                            ObservableVariantGenotype vg = new ObservableVariantGenotype();
                            vg.setMd5Hex(cv.md5Hex());
                            vg.setGenotype(Genotype.UNSET);
                            member.getGenotypes().add(vg);
                        }
                    }
                } else if (change.wasRemoved()) {
                    for (CuratedVariant cv : change.getRemoved()) {
                        Optional<ObservableVariantGenotype> opt = member.getGenotypes().stream()
                                .filter(ovg -> ovg.getMd5Hex().equals(cv.md5Hex()))
                                .findFirst();
                        if (opt.isPresent())
                            member.getGenotypes().remove(opt.get());
                        else
                            LOGGER.warn("Removed variant without genotype: {}", cv.md5Hex());
                    }
                } else {
                    LOGGER.info("Variant change: {}", change);
                }
            }
        };
    }

    private ObservableValue<? extends Image> createIndividualImageBinding() {
        ObjectBinding<Sex> sex = select(item, "sex");
        BooleanBinding isProband = selectBoolean(item, "proband");
        return new ObjectBinding<>() {
            {
                bind(sex, isProband);
            }

            @Override
            protected Image computeValue() {
                boolean proband = isProband.get();
                Sex s = sex.get() == null ? Sex.UNKNOWN : sex.get();

                Map<Boolean, Image> sexMap = SEX_AFFECTED_ICONS.get(s);
                return (sexMap == null)
                        ? null
                        : sexMap.get(proband);
            }
        };
    }

    private static StringBinding nullableStringProperty(ObjectProperty<?> property, String propertyId) {
        // 2 select statements but can't be done better.
        return when(select(property, propertyId).isNotNull())
                .then(selectString(property, propertyId))
                .otherwise("N/A");
    }

    private static StringBinding individualSummary(ObjectProperty<ObservablePedigreeMember> item) {
        ObjectBinding<ObservablePhenotypicFeature> features = select(item, "phenotypicFeatures");

        return new StringBinding() {
            {
                bind(item, features);
            }

            @Override
            protected String computeValue() {
                ObservablePedigreeMember member = item.get();
                if (member == null)
                    return "";

                return new StringBuilder()
                        .append(member.getObservablePhenotypicFeatures().size()).append(" phenotype terms, ")
                        .append(member.getDiseaseStates().size()).append(" disease states, ")
                        .append(member.getGenotypes().stream().filter(ovg -> ovg.getGenotype().isKnown()).count()).append(" known genotypes")
                        .toString();
            }
        };
    }

    @FXML
    private void identifiersSectionMouseEntered() {
        editIdentifiersButton.setVisible(true);
    }

    @FXML
    private void identifiersSectionMouseExited() {
        editIdentifiersButton.setVisible(false);
    }

    @FXML
    private void editIdentifiersAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(pedigreeMemberTitle.getParent().getScene().getWindow());
        dialog.titleProperty().bind(concat("Edit identifiers for ", nullableStringProperty(item, "id")));
        PedigreeMemberIdsEditableComponent component = new PedigreeMemberIdsEditableComponent();
        component.setInitialData(item.getValue());

        dialog.getDialogPane().setContent(component);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.UPDATE_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));

        dialog.showAndWait()
                .ifPresent(shouldUpdate -> {
                    if (shouldUpdate) component.commit();
                });

        e.consume();
    }

    @FXML
    private void phenotypesSectionMouseEntered() {
        editPhenotypeButton.setVisible(true);
    }

    @FXML
    private void phenotypesSectionMouseExited() {
        editPhenotypeButton.setVisible(false);
    }

    @FXML
    private void editPhenotypeAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(pedigreeMemberTitle.getParent().getScene().getWindow());
        dialog.initStyle(StageStyle.DECORATED);
        dialog.titleProperty().bind(concat("Edit phenotype features for ", nullableStringProperty(item, "id")));
        dialog.setResizable(true);

        PedigreeMemberPhenotypeDataEdit phenotypeDataEdit = new PedigreeMemberPhenotypeDataEdit();
        phenotypeDataEdit.setInitialData(item.get()); // TODO - check non null?
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
    private void diseasesSectionMouseEntered() {
        editDiseasesButton.setVisible(true);
    }

    @FXML
    private void diseasesSectionMouseExited() {
        editDiseasesButton.setVisible(false);
    }

    @FXML
    private void editDiseasesAction(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Not yet implemented", ButtonType.OK);
        alert.setTitle("Edit diseases");
        alert.setHeaderText("Sorry");
        alert.showAndWait();
        e.consume();
    }

    public ObjectProperty<ObservablePedigreeMember> itemProperty() {
        return item;
    }

    public ListProperty<CuratedVariant> variantsProperty() {
        return variants;
    }

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
    }

    // TODO - move to an Util class with lazy loading.
    private static Map<Sex, Map<Boolean, Image>> loadSexAffectedIconsMap() {
        Map<Boolean, Image> female = new HashMap<>();
        female.put(true, loadImage("female-proband.png"));
        female.put(false, loadImage("female.png"));

        Map<Boolean, Image> male = new HashMap<>();
        male.put(true, loadImage("male-proband.png"));
        male.put(false, loadImage("male.png"));

        return Map.of(Sex.MALE, male, Sex.FEMALE, female);
    }

    private static Image loadImage(String location) {
        try (InputStream is = PedigreeMember.class.getResourceAsStream(location)) {
            return new Image(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
