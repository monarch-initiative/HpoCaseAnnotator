package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.BaseBindingObservableDataController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;

public class AgeComponent extends BaseBindingObservableDataController<ObservableAge> {

    private final BooleanProperty isGestational = new SimpleBooleanProperty();

    @FXML
    private VBox box;
    @FXML
    private CheckBox ageIsUnknown;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab postnatalTab;
    @FXML
    private Tab gestationalTab;
    @FXML
    private TitledLabel postnatalYears;
    @FXML
    private TitledLabel postnatalMonths;
    @FXML
    private TitledLabel postnatalDays;
    @FXML
    private TitledLabel gestationalWeeks;
    @FXML
    private TitledLabel gestationalDays;

    @FXML
    protected void initialize() {
        super.initialize();
        box.disableProperty().bind(ageIsUnknown.selectedProperty());

        isGestational.addListener((obs, wasGestational, isGestational) -> {
            if (isGestational)
                tabPane.getSelectionModel().select(gestationalTab);
            else
                tabPane.getSelectionModel().select(postnatalTab);
        });
    }

    @Override
    protected void bind(ObservableAge data) {
        isGestational.bind(data.gestationalProperty());
        postnatalYears.textProperty().bind(data.daysProperty().asString());
        postnatalMonths.textProperty().bind(data.monthsProperty().asString());
        postnatalDays.textProperty().bind(data.daysProperty().asString());

        gestationalWeeks.textProperty().bind(data.weeksProperty().asString());
        gestationalDays.textProperty().bind(data.daysProperty().asString());
    }

    @Override
    protected void unbind(ObservableAge data) {
        isGestational.unbind();
        postnatalYears.textProperty().unbind();
        postnatalMonths.textProperty().unbind();
        postnatalDays.textProperty().unbind();

        gestationalWeeks.textProperty().unbind();
        gestationalDays.textProperty().unbind();
    }
}
