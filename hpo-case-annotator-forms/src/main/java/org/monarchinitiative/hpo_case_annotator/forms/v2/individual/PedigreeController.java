package org.monarchinitiative.hpo_case_annotator.forms.v2.individual;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import org.monarchinitiative.hpo_case_annotator.forms.BindingDataController;
import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.util.PeriodTableCell;
import org.monarchinitiative.hpo_case_annotator.forms.util.SexTableCell;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigree;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.monarchinitiative.hpo_case_annotator.model.v2.Pedigree;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Period;
import java.util.LinkedList;
import java.util.List;

/**
 * Controls {@link Pedigree}, {@link ObservablePedigreeMember}s and their associated data:
 * IDs, family relationships, age, sex, diseases, phenotypes, and genotypes.
 */
public class PedigreeController extends BindingDataController<ObservablePedigree> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PedigreeController.class);

    private final ObjectProperty<ObservablePedigree> pedigree = new SimpleObjectProperty<>(this, "pedigree", new ObservablePedigree());

    private final HCAControllerFactory controllerFactory;

    // This list is not supposed to be modified directly. The elements are added/removed in VariantSummaryController
    private final ObservableList<CuratedVariant> variants = FXCollections.observableList(new LinkedList<>());

    private final List<PedigreeMemberController> controllers = new LinkedList<>();

    @FXML
    private TabPane familyTabPane;
    @FXML
    private TableView<ObservablePedigreeMember> familyMembersTableView;
    @FXML
    private TableColumn<ObservablePedigreeMember, String> idTableColumn;
    @FXML
    private TableColumn<ObservablePedigreeMember, String> paternalIdTableColumn;
    @FXML
    private TableColumn<ObservablePedigreeMember, String> maternalIdTableColumn;
    @FXML
    private TableColumn<ObservablePedigreeMember, Period> ageTableColumn;
    @FXML
    private TableColumn<ObservablePedigreeMember, Sex> sexTableColumn;
    @FXML
    private TableColumn<ObservablePedigreeMember, Boolean> isProbandTableColumn;
    @FXML
    private Button removeIndividualButton;

    public PedigreeController(HCAControllerFactory controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    @FXML
    protected void initialize() {
        super.initialize();

        idTableColumn.setCellValueFactory(cdf -> cdf.getValue().idProperty());
        paternalIdTableColumn.setCellValueFactory(cdf -> cdf.getValue().paternalIdProperty());
        maternalIdTableColumn.setCellValueFactory(cdf -> cdf.getValue().maternalIdProperty());
        ageTableColumn.setCellValueFactory(cdf -> cdf.getValue().getAge().period());
        ageTableColumn.setCellFactory(PeriodTableCell.of());
        sexTableColumn.setCellValueFactory(cdf -> cdf.getValue().sexProperty());
        sexTableColumn.setCellFactory(SexTableCell.of());
        isProbandTableColumn.setCellValueFactory(cdf -> cdf.getValue().probandProperty());
        isProbandTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isProbandTableColumn));

        familyMembersTableView.getItems().addListener(pedigreeMemberListChangeListener());

        // Disable the remove button when the Summary tab is selected.
        familyTabPane.getSelectionModel().selectedIndexProperty()
                .addListener((obs, old, novel) -> removeIndividualButton.setDisable(novel.intValue() == 0));
    }

    private ListChangeListener<ObservablePedigreeMember> pedigreeMemberListChangeListener() {
        return c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (ObservablePedigreeMember added : c.getAddedSubList()) {
                        addPedigreeMember(added);
                    }
                } else if (c.wasRemoved()) {
                    for (int i = c.getFrom(); i <= c.getTo(); i++) {
                        removePedigreeMember(i);
                    }
                } else {
                    LOGGER.warn("Unexpected list change: {}", c);
                }
            }
        };
    }

    private void addPedigreeMember(ObservablePedigreeMember pedigreeMember) {
        try {
            FXMLLoader loader = new FXMLLoader(PedigreeMemberController.class.getResource("PedigreeMember.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent parent = loader.load();

            // Setup controller
            PedigreeMemberController controller = loader.getController();
            controller.setData(pedigreeMember);

            Bindings.bindContentBidirectional(controller.variants(), variants);

            // Setup Tab
            Tab tab = new Tab();
            tab.setClosable(false);
            tab.textProperty().bind(pedigreeMember.idProperty());
            tab.setContent(parent);

            // Store the Tab & the controller
            familyTabPane.getTabs().add(tab);
            controllers.add(controller);

            // Select the added tab
            familyTabPane.getSelectionModel().select(tab);
        } catch (IOException e) {
            LOGGER.warn("Unable to add individual: {}", e.getMessage());
            LOGGER.debug("Unable to add individual: {}", e.getMessage(), e);
        }
    }

    private void removePedigreeMember(int tableIdx) {
        // Remove the Tab
        int tabIdx = tableIdx + 1;
        familyTabPane.getTabs().remove(tabIdx);

        // Remove the controller
        PedigreeMemberController controller = controllers.remove(tableIdx);
        Bindings.unbindContentBidirectional(controller.variants(), variants);
    }

    @Override
    public ObjectProperty<ObservablePedigree> dataProperty() {
        return pedigree;
    }

    @Override
    public void bind(ObservablePedigree pedigree) {
        Bindings.bindContentBidirectional(familyMembersTableView.getItems(), pedigree.members());
    }

    @Override
    public void unbind(ObservablePedigree pedigree) {
        Bindings.unbindContentBidirectional(familyMembersTableView.getItems(), pedigree.members());
    }

    @FXML
    private void addIndividualButtonAction() {
        familyMembersTableView.getItems().add(new ObservablePedigreeMember());
    }

    @FXML
    private void removeIndividualButtonAction() {
        int tabIdx = familyTabPane.getSelectionModel().getSelectedIndex();
        int tableIdx = tabIdx - 1;
        familyMembersTableView.getItems().remove(tableIdx);
    }

    public ObservableList<CuratedVariant> curatedVariants() {
        return variants;
    }

}
