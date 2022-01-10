package org.monarchinitiative.hpo_case_annotator.forms.study;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class PedigreeMemberController extends BaseIndividualController<ObservablePedigreeMember> {

    @FXML
    private TextField paternalIdTextField;
    @FXML
    private TextField maternalIdTextField;
    @FXML
    private CheckBox probandCheckBox;

    @Override
    protected ChangeListener<ObservablePedigreeMember> individualChangeListener() {
        return (obs, old, novel) -> {
            // unbind
            if (old != null) {
                unbind(old);

                paternalIdTextField.textProperty().unbindBidirectional(old.paternalIdProperty());
                maternalIdTextField.textProperty().unbindBidirectional(old.maternalIdProperty());
                probandCheckBox.selectedProperty().unbindBidirectional(old.probandProperty());
            }

            // bind
            if (novel != null) {
                bind(novel);

                paternalIdTextField.textProperty().bindBidirectional(novel.paternalIdProperty());
                maternalIdTextField.textProperty().bindBidirectional(novel.maternalIdProperty());
                probandCheckBox.selectedProperty().bindBidirectional(novel.probandProperty());
            }
        };
    }

}
