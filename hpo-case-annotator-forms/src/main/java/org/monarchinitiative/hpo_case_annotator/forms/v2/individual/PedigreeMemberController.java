package org.monarchinitiative.hpo_case_annotator.forms.v2.individual;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

public class PedigreeMemberController extends BaseIndividualController<ObservablePedigreeMember> {

    private final ObjectProperty<ObservablePedigreeMember> individual = new SimpleObjectProperty<>(this, "individual", new ObservablePedigreeMember());

    @FXML
    private TextField paternalIdTextField;
    @FXML
    private TextField maternalIdTextField;
    @FXML
    private CheckBox probandCheckBox;

    @Override
    protected void bind(ObservablePedigreeMember individual) {
        super.bind(individual);

        paternalIdTextField.textProperty().bindBidirectional(individual.paternalIdProperty());
        maternalIdTextField.textProperty().bindBidirectional(individual.maternalIdProperty());
        probandCheckBox.selectedProperty().bindBidirectional(individual.probandProperty());
    }

    @Override
    protected void unbind(ObservablePedigreeMember individual) {
        super.unbind(individual);

        paternalIdTextField.textProperty().unbindBidirectional(individual.paternalIdProperty());
        maternalIdTextField.textProperty().unbindBidirectional(individual.maternalIdProperty());
        probandCheckBox.selectedProperty().unbindBidirectional(individual.probandProperty());
    }


    @Override
    public ObjectProperty<ObservablePedigreeMember> dataProperty() {
        return individual;
    }
}
