package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextFormatter;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.forms.VBoxBindingObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledComboBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;

import java.io.IOException;
import java.util.stream.Stream;

public class SimpleBindingAge extends VBoxBindingObservableDataController<ObservableAge> implements Observable {

    static final Callback<SimpleBindingAge, Stream<Observable>> EXTRACTOR = sba -> Stream.of(
            sba.yearsFormatter.valueProperty(),
            sba.months.valueProperty(),
            sba.days.valueProperty());

    @FXML
    private TitledTextField years;
    private final TextFormatter<Integer> yearsFormatter = Formats.integerFormatter();
    @FXML
    private TitledComboBox<Integer> months;
    @FXML
    private TitledComboBox<Integer> days;

    public SimpleBindingAge() {
        FXMLLoader loader = new FXMLLoader(SimpleBindingAge.class.getResource("SimpleBindingAge.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void initialize() {
        super.initialize();
        years.setTextFormatter(yearsFormatter);
        months.getItems().addAll(FormUtils.getIntegers(11));
        days.getItems().addAll(FormUtils.getIntegers(31));
    }

    @Override
    protected void unbind(ObservableAge data) {
        if (data != null) {
            yearsFormatter.valueProperty().unbindBidirectional(data.yearsProperty());
            months.valueProperty().unbindBidirectional(data.monthsProperty());
            days.valueProperty().unbindBidirectional(data.daysProperty());
        }
    }

    @Override
    protected void bind(ObservableAge data) {
        if (data == null) {
            clear();
        } else {
            yearsFormatter.valueProperty().bindBidirectional(data.yearsProperty());
            months.valueProperty().bindBidirectional(data.monthsProperty());
            days.valueProperty().bindBidirectional(data.daysProperty());
        }
    }

    private void clear() {
        yearsFormatter.setValue(null);
        months.setValue(null);
        days.setValue(null);
    }

    void setYears(Integer value) {
        yearsFormatter.setValue(value);
    }

    Integer getYears() {
        return yearsFormatter.getValue();
    }

    void setMonths(Integer value) {
        months.setValue(value);
    }

    Integer getMonths() {
        return months.getValue();
    }

    void setDays(Integer value) {
        days.setValue(value);
    }

    Integer getDays() {
        return days.getValue();
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

    @Override
    public void addListener(InvalidationListener listener) {
        EXTRACTOR.call(this).forEach(obs -> obs.addListener(listener));
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        EXTRACTOR.call(this).forEach(obs -> obs.removeListener(listener));
    }
}
