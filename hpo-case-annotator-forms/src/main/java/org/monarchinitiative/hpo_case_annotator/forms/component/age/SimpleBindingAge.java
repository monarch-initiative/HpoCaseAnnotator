package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledComboBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;

import java.io.IOException;

public class SimpleBindingAge extends GridPane implements ObservableDataController<ObservableAge>, Observable {

    static final Callback<SimpleBindingAge, Observable[]> EXTRACTOR = sba -> new Observable[]{
            sba.yearsFormatter.valueProperty(),
            sba.months.valueProperty(),
            sba.days.valueProperty()
    };
    private final ObjectProperty<ObservableAge> data = new SimpleObjectProperty<>();

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
    private void initialize() {
        years.setTextFormatter(yearsFormatter);
        months.getItems().addAll(FormUtils.getIntegers(11));
        days.getItems().addAll(FormUtils.getIntegers(30));
        data.addListener((obs, old, novel) -> {
            unbind(old);
            bind(novel);
        });
    }

    private void unbind(ObservableAge data) {
        if (data == null) {
            clear();
        } else {
            yearsFormatter.valueProperty().unbindBidirectional(data.yearsProperty());
            months.valueProperty().unbindBidirectional(data.monthsProperty());
            days.valueProperty().unbindBidirectional(data.daysProperty());
        }
    }

    private void bind(ObservableAge data) {
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

    @Override
    public ObjectProperty<ObservableAge> dataProperty() {
        return data;
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
        for (Observable observable : EXTRACTOR.call(this))
            observable.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        for (Observable observable : EXTRACTOR.call(this))
            observable.removeListener(listener);
    }
}
