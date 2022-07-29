package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.DataEditController;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;

import java.io.IOException;

public class AgeEditableComponent extends VBox implements DataEditController<ObservableAge> {

    private static final String DEFAULT_STYLECLASS = "age-component";
    private final ObjectProperty<ObservableAge> item = new SimpleObjectProperty<>();

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
    private TitledTextField postnatalYears;
    private final TextFormatter<Integer> postnatalYearsFormatter = Formats.integerFormatter();
    @FXML
    private TitledComboBox<Integer> postnatalMonths;
    @FXML
    private TitledComboBox<Integer> postnatalDays;
    @FXML
    private TitledTextField gestationalWeeks;
    private final TextFormatter<Integer> gestationalWeeksFormatter = Formats.integerFormatter();
    @FXML
    private TitledComboBox<Integer> gestationalDays;

    public AgeEditableComponent() {
        getStyleClass().add(DEFAULT_STYLECLASS);
        FXMLLoader loader = new FXMLLoader(AgeEditableComponent.class.getResource("AgeEditableComponent.fxml"));
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
        box.disableProperty().bind(ageIsUnknown.selectedProperty());

        postnatalYears.setTextFormatter(postnatalYearsFormatter);
        postnatalMonths.getItems().addAll(FormUtils.getIntegers(11));
        postnatalDays.getItems().addAll(FormUtils.getIntegers(30));

        gestationalWeeks.setTextFormatter(gestationalWeeksFormatter);
        gestationalDays.getItems().addAll(FormUtils.getIntegers(6));

        // clear the
        ageIsUnknown.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected){
                item.set(null);
                clearForm();
            } else {
                item.set(new ObservableAge());
            }
        });

        item.addListener((obs, o, novelAge) -> {
            if (novelAge == null)
                ageIsUnknown.setSelected(true);
            else {
                ageIsUnknown.setSelected(false);
                if (novelAge.isGestational()) {
                    tabPane.getSelectionModel().select(gestationalTab);
                    gestationalWeeksFormatter.setValue(novelAge.weeksProperty().getValue());
                    gestationalDays.setValue(novelAge.daysProperty().getValue());
                } else {
                    tabPane.getSelectionModel().select(postnatalTab);
                    postnatalYearsFormatter.setValue(novelAge.yearsProperty().getValue());
                    postnatalMonths.setValue(novelAge.monthsProperty().getValue());
                    postnatalDays.setValue(novelAge.daysProperty().getValue());
                }
            }
        });

    }

    private void clearForm() {
        postnatalYears.setText(null);
        postnatalMonths.setValue(null);
        postnatalDays.setValue(null);

        gestationalWeeks.setText(null);
        gestationalDays.setValue(null);
    }

    @Override
    public void setInitialData(ObservableAge item) {
        this.item.set(item);
    }

    @Override
    public ObservableAge getEditedData() {
        ObservableAge item = this.item.get();
        if (item != null && !ageIsUnknown.isSelected()) {
            if (tabPane.getSelectionModel().getSelectedItem().equals(gestationalTab)) {
                item.setGestational(true);
                item.setYears(null);
                item.setMonths(null);
                item.setWeeks(gestationalWeeksFormatter.getValue());
                item.setDays(gestationalDays.getValue());
            } else {
                item.setGestational(false);
                item.setYears(postnatalYearsFormatter.getValue());
                item.setMonths(postnatalMonths.getValue());
                item.setWeeks(null);
                item.setDays(postnatalDays.getValue());
            }

        }

        return item;
    }
}
