package org.monarchinitiative.hpo_case_annotator.util.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.monarchinitiative.hpo_case_annotator.model.HPO;

import java.net.URL;
import java.util.*;

/**
 * This is the controller class for the HPO PopUp window. It handles adding and removal of
 * {@link HPO} beans to/from
 * {@link org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel} subclasses.
 *
 * @author Daniel Danis
 */
@Deprecated
public class HPOWindowController extends PopUpWindow implements Initializable {

    /**
     * The list contain terms that were added before. They are returned if
     * the cancel button is clicked.
     */
    private Set<HPO> oldHPOTerms, result;

    @FXML
    private TableView<HPO> hpoTableView;

    @FXML
    private TableColumn<HPO, String> hpoIdTableColumn;

    @FXML
    private TableColumn<HPO, String> hpoNameTableColumn;

    @FXML
    private TableColumn<HPO, String> observedTableColumn;


    @FXML
    void cancelButtonClicked() {
        result = oldHPOTerms;
        window.close();
    }


    @FXML
    void okButtonClicked() {
        result = new HashSet<>(hpoTableView.getItems());
        window.close();
    }


    /**
     * Remove selected HPO term from TableView.
     */
    @FXML
    void removeButtonClicked() {
        int selected = hpoTableView.getSelectionModel().getSelectedIndex();
        if (selected >= 0) {
            hpoTableView.getItems().remove(selected);
        }
    }


    /**
     * Return unmodified list of HPO terms if cancel button was clicked or modified if OK button was clicked.
     */
    public List<HPO> getTerms() {
        return new ArrayList<>(result);
    }


    /**
     * Initialize TableView with provided list of HPO beans that represent terms already present in model.
     *
     * @param givenHPO List of HPO beans which are already present in model.
     */
    public void setTerms(List<HPO> givenHPO) {
        hpoTableView.getItems().addAll(givenHPO);
        oldHPOTerms = new HashSet<>(givenHPO); // store old terms for the case that user clicks Cancel
    }


    /**
     * Initialize TableView columns & cell value factories.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        hpoIdTableColumn.setCellValueFactory(param -> param.getValue().hpoIdProperty());
        hpoNameTableColumn.setCellValueFactory(param -> param.getValue().hpoNameProperty());
        observedTableColumn.setCellValueFactory(param -> param.getValue().observedProperty());
    }


    @Override
    public void setWindow(Stage window) {
        this.window = window;
        window.setOnCloseRequest(e -> result = oldHPOTerms);
    }

}
