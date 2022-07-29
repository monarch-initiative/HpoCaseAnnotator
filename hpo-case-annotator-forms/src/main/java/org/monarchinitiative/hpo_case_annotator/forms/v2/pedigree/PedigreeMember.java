package org.monarchinitiative.hpo_case_annotator.forms.v2.pedigree;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.IndividualIdsComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.IndividualIdsEditableComponent;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;

import static javafx.beans.binding.Bindings.*;
import static javafx.beans.binding.Bindings.selectString;

public class PedigreeMember extends VBox {

    private static final String DEFAULT_STYLECLASS = "pedigree-member";

    private final ObjectProperty<ObservablePedigreeMember> item = new SimpleObjectProperty<>();

    @FXML
    private IndividualIdsComponent individualIdentifiers;
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
        individualIdentifiers.individualIdProperty()
                .bind(nullableStringProperty(item, "id"));
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

    private static StringBinding nullableStringProperty(ObjectProperty<?> property, String propertyId) {
        // 2 select statements but can't be done better.
        return when(select(property, propertyId).isNotNull())
                .then(selectString(property, propertyId))
                .otherwise("N/A");
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
    private void editPhenotypeAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.titleProperty().bind(concat("Individual ID: ", nullableStringProperty(item, "id")));
        dialog.setHeaderText("Edit phenotype terms");
        dialog.getDialogPane().setContent(new Label("Sorry, not yet implemented"));
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.OK_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));
        dialog.showAndWait()
                .ifPresent(commit -> {});
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

}
