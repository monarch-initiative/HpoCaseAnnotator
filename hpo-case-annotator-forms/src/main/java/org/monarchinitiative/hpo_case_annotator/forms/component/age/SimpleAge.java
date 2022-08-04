package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;

import java.io.IOException;

public class SimpleAge extends HBox {

    @FXML
    private TitledLabel years;
    @FXML
    private TitledLabel months;
    @FXML
    private TitledLabel days;

    public SimpleAge() {
        FXMLLoader loader = new FXMLLoader(SimpleAge.class.getResource("SimpleAge.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public StringProperty yearsProperty() {
        return years.textProperty();
    }

    public StringProperty monthsProperty() {
        return months.textProperty();
    }

    public StringProperty daysProperty() {
        return days.textProperty();
    }

}
