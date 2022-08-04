package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AgeRange extends VBox {

    @FXML
    private SimpleAge start;
    @FXML
    private SimpleAge end;

    public AgeRange() {
        FXMLLoader loader = new FXMLLoader(AgeRange.class.getResource("AgeRange.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public StringProperty startYearsProperty() {
        return start.yearsProperty();
    }

    public StringProperty startMonthsProperty() {
        return start.monthsProperty();
    }

    public StringProperty startDaysProperty() {
        return start.daysProperty();
    }

    public StringProperty endYearsProperty() {
        return end.yearsProperty();
    }

    public StringProperty endMonthsProperty() {
        return end.monthsProperty();
    }

    public StringProperty endDaysProperty() {
        return end.daysProperty();
    }


}
