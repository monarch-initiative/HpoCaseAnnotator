package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledComboBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;

import java.io.IOException;

public class SimpleEditableAge extends HBox {

    @FXML
    private TitledTextField years;
    private final TextFormatter<Integer> yearsFormatter = Formats.integerFormatter();
    @FXML
    private TitledComboBox<Integer> months;
    @FXML
    private TitledComboBox<Integer> days;

    public SimpleEditableAge() {
        FXMLLoader loader = new FXMLLoader(SimpleEditableAge.class.getResource("SimpleEditableAge.fxml"));
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
        years.setTextFormatter(yearsFormatter);
        months.getItems().addAll(FormUtils.getIntegers(11));
        days.getItems().addAll(FormUtils.getIntegers(31));
    }

    @FXML
    private void clearYears(ActionEvent e) {
        yearsFormatter.setValue(null);
        e.consume();
    }

    @FXML
    private void clearMonths(ActionEvent e) {
        months.setValue(null);
        e.consume();
    }

    @FXML
    private void clearDays(ActionEvent e) {
        days.setValue(null);
        e.consume();
    }

    public void setAge(ObservableAge age) {
        if (age == null) {
            clean();
        } else {
            yearsFormatter.setValue(age.getYears());
            months.setValue(age.getMonths());
            days.setValue(age.getDays());
        }
    }

    public void clean() {
        yearsFormatter.setValue(null);
        months.setValue(null);
        days.setValue(null);
    }

    public ObservableAge getAge() {
        ObservableAge a = new ObservableAge();
        a.setYears(yearsFormatter.getValue());
        a.setMonths(months.getValue());
        a.setDays(days.getValue());
        return a;
    }

}
