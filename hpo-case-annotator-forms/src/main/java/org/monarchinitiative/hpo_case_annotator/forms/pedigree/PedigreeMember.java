package org.monarchinitiative.hpo_case_annotator.forms.pedigree;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import org.monarchinitiative.hpo_case_annotator.forms.component.IndividualIdsComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.IndividualIdsEditableComponent;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static javafx.beans.binding.Bindings.*;

public class PedigreeMember extends TitledPane {

    private static final String DEFAULT_STYLECLASS = "pedigree-member";
    private static final Map<Sex, Map<Boolean, Image>> SEX_AFFECTED_ICONS = loadSexAffectedIconsMap();
    private final ObjectProperty<ObservablePedigreeMember> item = new SimpleObjectProperty<>();
    @FXML
    private PedigreeMemberTitle pedigreeMemberTitle;
    @FXML
    private Button editIdentifiersButton;
    @FXML
    private IndividualIdsComponent individualIdentifiers;
    @FXML
    private Button editPhenotypeButton;
    @FXML
    private TableView<ObservablePhenotypicFeature> phenotypes;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, TermId> idColumn;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, String> labelColumn;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, String> statusColumn;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, ObservableAge> onsetColumn;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, ObservableAge> resolutionColumn;
    public PedigreeMember() {
        getStyleClass().add(DEFAULT_STYLECLASS);
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
        StringBinding individualId = nullableStringProperty(item, "id");
        pedigreeMemberTitle.probandId.textProperty().bind(individualId);
        pedigreeMemberTitle.icon.imageProperty().bind(createIndividualImageBinding());
        pedigreeMemberTitle.summary.textProperty().bind(individualSummary());
        individualIdentifiers.individualIdProperty()
                .bind(individualId);
        individualIdentifiers.paternalIdProperty()
                .bind(nullableStringProperty(item, "paternalId"));
        individualIdentifiers.maternalIdProperty()
                .bind(nullableStringProperty(item, "maternalId"));
        individualIdentifiers.sexProperty()
                .bind(select(item, "sex").asString());
        individualIdentifiers.ageProperty()
                .bind(select(item, "observableAge"));
        individualIdentifiers.probandProperty()
                .bind(when(selectBoolean(item, "proband"))
                        .then("Yes")
                        .otherwise("No"));

        idColumn.setCellValueFactory(cdf -> cdf.getValue().termIdProperty());
        idColumn.setCellFactory(TermIdTableCell::new);
        statusColumn.setCellValueFactory(cdf -> when(cdf.getValue().excludedProperty()).then("Excluded").otherwise("Present"));


        // TODO - bind phenotype terms, diseases, genotypes
        phenotypes.itemsProperty().bind(select(item, "phenotypicFeatures"));
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

    private StringBinding individualSummary() {
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
                        .append(member.getGenotypes().size()).append(" genotypes")
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
        dialog.titleProperty().bind(concat("Individual ID: ", nullableStringProperty(item, "id")));
        dialog.setHeaderText("Edit identifiers");
        IndividualIdsEditableComponent component = new IndividualIdsEditableComponent();
        component.setInitialData(item.getValue());

        dialog.getDialogPane().setContent(component);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.UPDATE_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));

        dialog.showAndWait()
                .filter(i -> i)
                .ifPresent(shouldUpdate -> item.set(component.getEditedData()));

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
        dialog.titleProperty().bind(concat("Individual ID: ", nullableStringProperty(item, "id")));
        dialog.setHeaderText("Edit phenotype terms");
        dialog.getDialogPane().setContent(new Label("Sorry, not yet implemented"));
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.OK_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));
        dialog.showAndWait()
                .ifPresent(commit -> {
                });
        e.consume();
    }

    public ObjectProperty<ObservablePedigreeMember> itemProperty() {
        return item;
    }

    public ObservablePedigreeMember getItem() {
        return item.get();
    }

    public void setItem(ObservablePedigreeMember item) {
        this.item.set(item);
    }

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
