package org.monarchinitiative.hpo_case_annotator.forms.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.monarchinitiative.hpo_case_annotator.forms.base.BindingObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.hpo_case_annotator.forms.util.TextFormatters;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;

public class AgeController extends BindingObservableDataComponent<ObservableAge> {

    private final ObjectProperty<ObservableAge> age = new SimpleObjectProperty<>(this, "age", new ObservableAge());
    @FXML
    private TextField yearsTextField;
    private final TextFormatter<Integer> yearsFormatter = TextFormatters.integerFormatter();
    @FXML
    private ComboBox<Integer> monthsComboBox;
    @FXML
    private ComboBox<Integer> daysComboBox;

    @FXML
    protected void initialize() {
        super.initialize();

        yearsTextField.setTextFormatter(yearsFormatter);

        monthsComboBox.getItems().addAll(FormUtils.getIntegers(11));
        monthsComboBox.getSelectionModel().selectFirst();
        daysComboBox.getItems().addAll(FormUtils.getIntegers(30));
        daysComboBox.getSelectionModel().selectFirst();
    }

    @Override
    protected void bind(ObservableAge age) {
        yearsFormatter.valueProperty().bind(age.yearsProperty());
        monthsComboBox.valueProperty().bindBidirectional(age.monthsProperty());
        daysComboBox.valueProperty().bindBidirectional(age.daysProperty());
    }

    @Override
    protected void unbind(ObservableAge age) {
        yearsFormatter.valueProperty().unbindBidirectional(age.yearsProperty());
        monthsComboBox.valueProperty().unbindBidirectional(age.monthsProperty());
        daysComboBox.valueProperty().unbindBidirectional(age.daysProperty());
    }

    @Override
    public ObjectProperty<ObservableAge> dataProperty() {
        return age;
    }
}
