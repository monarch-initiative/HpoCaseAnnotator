package org.monarchinitiative.hpo_case_annotator.forms.v2;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.v2.individual.PedigreeMemberController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.monarchinitiative.hpo_case_annotator.model.v2.Pedigree;

import java.net.URL;


/**
 * Controls {@link Pedigree}, {@link ObservablePedigreeMember}s and their associated data:
 * IDs, family relationships, age, sex, diseases, phenotypes, and genotypes.
 */
public class PedigreeController extends BaseIndividualCollectionController<ObservablePedigreeMember, PedigreeMemberController> {

    @FXML
    private TableColumn<ObservablePedigreeMember, String> paternalIdTableColumn;
    @FXML
    private TableColumn<ObservablePedigreeMember, String> maternalIdTableColumn;
    @FXML
    private TableColumn<ObservablePedigreeMember, Boolean> isProbandTableColumn;

    public PedigreeController(HCAControllerFactory controllerFactory) {
        super(controllerFactory);
    }

    @FXML
    protected void initialize() {
        super.initialize();

        paternalIdTableColumn.setCellValueFactory(cdf -> cdf.getValue().paternalIdProperty());
        maternalIdTableColumn.setCellValueFactory(cdf -> cdf.getValue().maternalIdProperty());
        isProbandTableColumn.setCellValueFactory(cdf -> cdf.getValue().probandProperty());
        isProbandTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isProbandTableColumn));
    }

    @Override
    protected ObservablePedigreeMember newInstance() {
        return new ObservablePedigreeMember();
    }

    @Override
    protected URL getResourceUrl() {
        return PedigreeMemberController.class.getResource("PedigreeMember.fxml");
    }

}
