package org.monarchinitiative.hpo_case_annotator.forms.v2;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.util.PeriodTableCell;
import org.monarchinitiative.hpo_case_annotator.forms.util.SexTableCell;
import org.monarchinitiative.hpo_case_annotator.forms.v2.individual.IndividualController;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Period;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CohortController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CohortController.class);

    // TODO - there is a similarity between PedigreeController and CohortController
    // This list is not supposed to be modified directly. The elements are added/removed in VariantSummaryController
    private final ObservableList<CuratedVariant> curatedVariants = FXCollections.observableList(new LinkedList<>());

    private final HCAControllerFactory controllerFactory;
    private final List<IndividualController> controllers = new LinkedList<>();

    @FXML
    private TabPane membersTabPane;
    @FXML
    private TableView<ObservableIndividual> membersTableView;
    @FXML
    private TableColumn<ObservableIndividual, String> idTableColumn;
    @FXML
    private TableColumn<ObservableIndividual, Period> ageTableColumn;
    @FXML
    private TableColumn<ObservableIndividual, Sex> sexTableColumn;
    @FXML
    private Button removeIndividualButton;

    public CohortController(HCAControllerFactory controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    @FXML
    private void initialize() {
        idTableColumn.setCellValueFactory(cdf -> cdf.getValue().idProperty());
        ageTableColumn.setCellValueFactory(cdf -> cdf.getValue().getAge().period());
        ageTableColumn.setCellFactory(PeriodTableCell.of());
        sexTableColumn.setCellValueFactory(cdf -> cdf.getValue().sexProperty());
        sexTableColumn.setCellFactory(SexTableCell.of());

        removeIndividualButton.disableProperty().bind(membersTableView.getSelectionModel().selectedItemProperty().isNull());

        membersTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void addIndividualButtonAction(ActionEvent e) {
        // add new individual and open the last item
        membersTableView.getItems().add(new ObservableIndividual());
        openMemberTabOrSelectPreviouslyOpenTab(membersTableView.getItems().size() - 1);
        e.consume();
    }

    private void openMemberTabOrSelectPreviouslyOpenTab(Integer memberIdx) {
        ObservableIndividual individual = membersTableView.getItems().get(memberIdx);
        int tabIdx = -1;
        for (int controllerIdx = 0; controllerIdx < controllers.size(); controllerIdx++) {
            IndividualController controller = controllers.get(controllerIdx);
            ObservableIndividual controlled = controller.getData();
            if (individual.equals(controlled)) {
                tabIdx = controllerIdx + 1;
                break;
            }
        }

        if (tabIdx == -1) {
            // the model is not open
            openMemberTab(Bindings.valueAt(membersTableView.getItems(), memberIdx));
        } else {
            // the model is already open, select the tab
            membersTabPane.getSelectionModel().select(tabIdx);
        }
    }

    private void openMemberTab(ObjectBinding<ObservableIndividual> individual) {
        // the single place where we add a Tab
        try {
            FXMLLoader loader = new FXMLLoader(IndividualController.class.getResource("Individual.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent parent = loader.load();

            // Setup controller
            IndividualController controller = loader.getController();
            controller.dataProperty().bind(individual);
            Bindings.bindContentBidirectional(controller.variants(), curatedVariants);

            // Setup Tab
            Tab tab = new Tab();
            tab.setClosable(true);
            tab.textProperty().bind(individual.get().idProperty());
            tab.setContent(parent);
            tab.setOnCloseRequest(e -> closeTab(tab, controller));

            // Store & select the new Tab
            controllers.add(controller);
            membersTabPane.getTabs().add(tab);
            membersTabPane.getSelectionModel().select(tab);
        } catch (IOException e) {
            LOGGER.warn("Unable to add individual: {}", e.getMessage(), e);
        }
    }

    private void closeTab(Tab tab, IndividualController controller) {
        // Remove the Tab
        membersTabPane.getTabs().remove(tab);
        // Remove & unbind the controller
        controller.dataProperty().unbind();
        Bindings.unbindContentBidirectional(controller.variants(), curatedVariants);
        controllers.remove(controller);
    }

    @FXML
    private void removeIndividualButtonAction(ActionEvent e) {
        // remove row
        int memberRowIdx = membersTableView.getSelectionModel().getSelectedIndex();
        ObservableIndividual removed = membersTableView.getItems().remove(memberRowIdx);

        int nTabs = membersTabPane.getTabs().size();

        //
        List<Integer> indicesOfTabsToRemove = new ArrayList<>(nTabs - 1);

        for (int tabIdx = 1; tabIdx < nTabs; tabIdx++) { // tabIdx=1 because the first tab is always the summary tab
            int controllerIdx = tabIdx - 1;
            IndividualController controller = controllers.get(controllerIdx);
            ObservableIndividual data = controller.getData();
            if (data.equals(removed))
                indicesOfTabsToRemove.add(tabIdx);
        }

        // now remove the tabs & controllers
        for (int tabIdx = indicesOfTabsToRemove.size() - 1; tabIdx >= 0; tabIdx--) {
            membersTabPane.getTabs().remove(tabIdx);
            IndividualController controllerToRemove = controllers.remove(tabIdx - 1);

            controllerToRemove.dataProperty().unbind();
            Bindings.unbindContentBidirectional(controllerToRemove.variants(), curatedVariants);
        }

        e.consume();
    }

    @FXML
    private void membersTableViewOnMouseClicked(MouseEvent e) {
        if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
            int memberIdx = membersTableView.getSelectionModel().getSelectedIndex();
            openMemberTabOrSelectPreviouslyOpenTab(memberIdx);
        }
        e.consume();
    }

    @FXML
    private void membersTableViewKeyPressed(KeyEvent e) {
        if (e.getCode().equals(KeyCode.ENTER)) {
            // open tabs with all selected individuals
            ObservableList<Integer> indices = membersTableView.getSelectionModel().getSelectedIndices();
            for (Integer memberIdx : indices) {
                openMemberTabOrSelectPreviouslyOpenTab(memberIdx);
            }
            e.consume();
        }
    }

    public ObservableList<ObservableIndividual> members() {
        return membersTableView.getItems();
    }

    public ObservableList<CuratedVariant> curatedVariants() {
        return curatedVariants;
    }


    @FXML
    private void membersTabPaneKeyPressed(KeyEvent e) {
        if (e.isControlDown() && e.getCode().equals(KeyCode.W)) {
            // close the tab
            int selectedTabIdx = membersTabPane.getSelectionModel().getSelectedIndex();
            if (selectedTabIdx != 0) {
                Tab tab = membersTabPane.getTabs().get(selectedTabIdx);
                IndividualController controller = controllers.get(selectedTabIdx - 1);
                closeTab(tab, controller);
            }
        }
    }
}
