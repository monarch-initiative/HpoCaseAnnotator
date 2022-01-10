package org.monarchinitiative.hpo_case_annotator.forms.individual;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.ComponentController;
import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.InvalidComponentDataException;
import org.monarchinitiative.hpo_case_annotator.model.v2.Pedigree;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Controls {@link Pedigree}, {@link ObservablePedigreeMember}s and their associated data:
 * IDs, family relationships, age, sex, diseases, phenotypes, and genotypes.
 */
public class PedigreeController implements ComponentController<Pedigree> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PedigreeController.class);

    // This list is not supposed to be modified directly. The elements are added/removed in VariantSummaryController
    private final ObservableList<CuratedVariant> variants = FXCollections.observableList(new LinkedList<>());

    private final HCAControllerFactory controllerFactory;

    private final List<ListChangeListener<CuratedVariant>> variantChangeListeners = new LinkedList<>();
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
    private TableColumn<ObservablePedigreeMember, String> ageTableColumn;
    @FXML
    private TableColumn<ObservablePedigreeMember, String> sexTableColumn;
    @FXML
    private TableColumn<ObservablePedigreeMember, String> isProbandTableColumn;
    @FXML
    private Button removeIndividualButton;

    public PedigreeController(HCAControllerFactory controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    @FXML
    private void initialize() {
        // Disable the remove button when the Summary tab is selected.
        familyTabPane.getSelectionModel().selectedIndexProperty()
                .addListener((obs, old, novel) -> removeIndividualButton.setDisable(novel.intValue() == 0));

        idTableColumn.setCellValueFactory(cdf -> cdf.getValue().idProperty());
        paternalIdTableColumn.setCellValueFactory(cdf -> cdf.getValue().paternalIdProperty());
        maternalIdTableColumn.setCellValueFactory(cdf -> cdf.getValue().maternalIdProperty());
        ageTableColumn.setCellValueFactory(cdf -> cdf.getValue().ageProperty().asString());
        sexTableColumn.setCellValueFactory(cdf -> cdf.getValue().sexProperty().asString());
        isProbandTableColumn.setCellValueFactory(cdf -> cdf.getValue().probandCheckMark());

        familyMembersTableView.getItems().addListener(pedigreeMemberListChangeListener());

    }

    private ListChangeListener<ObservablePedigreeMember> pedigreeMemberListChangeListener() {
        return c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (ObservablePedigreeMember added : c.getAddedSubList()) {
                        addPedigreeMember(added);
                    }
                } else if (c.wasRemoved()) {
                    IntStream.range(c.getFrom(), c.getTo())
                            .forEach(this::removePedigreeMember);
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
            VBox parent = loader.load();

            // Setup controller
            PedigreeMemberController controller = loader.getController();
            ListChangeListener<CuratedVariant> variantChangeListener = controller.curatedVariantChangeListener();
            variants.addListener(variantChangeListener);
            controller.setIndividual(pedigreeMember);

            // Setup Tab
            Tab tab = new Tab();
            tab.setClosable(false);
            tab.textProperty().bind(pedigreeMember.idProperty());
            tab.setContent(parent);

            // Store the items in list
            familyTabPane.getTabs().add(tab);
            variantChangeListeners.add(variantChangeListener);
            familyTabPane.getSelectionModel().select(tab);
        } catch (IOException e) {
            LOGGER.warn("Unable to add individual: {}", e.getMessage());
            LOGGER.debug("Unable to add individual: {}", e.getMessage(), e);
        }
    }

    private void removePedigreeMember(int tabIndex) {
        if (tabIndex < 1) return; // we don't remove the Summary tab

        // remove Tab
        familyTabPane.getTabs().remove(tabIndex);

        // remove variant listener
        ListChangeListener<CuratedVariant> toRemove = variantChangeListeners.remove(tabIndex - 1);
        variants.removeListener(toRemove);
    }

    @FXML
    private void addIndividualButtonAction() {
        familyMembersTableView.getItems().add(new ObservablePedigreeMember());
    }

    @FXML
    private void removeIndividualButtonAction() {
        removePedigreeMember(familyTabPane.getSelectionModel().getSelectedIndex());
    }

    public ObservableList<CuratedVariant> curatedVariants() {
        return variants;
    }

    @Override
    public void presentComponent(Pedigree pedigree) {
        pedigree.members()
                .map(Convert::toObservablePedigreeMember)
                .forEach(opm -> familyMembersTableView.getItems().add(opm));

    }

    @Override
    public Pedigree getComponent() throws InvalidComponentDataException {
        return Pedigree.of(familyMembersTableView.getItems().stream()
                .map(Convert::toPedigreeMember)
                .toList());
    }

}
