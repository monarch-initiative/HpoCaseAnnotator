package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class Age extends VBox {

    @FXML
    private SimpleAge simpleAge;

    public Age() {
        FXMLLoader loader = new FXMLLoader(Age.class.getResource("Age.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public StringProperty yearsProperty() {
        return simpleAge.yearsProperty();
    }

    public StringProperty monthsProperty() {
        return simpleAge.monthsProperty();
    }

    public StringProperty daysProperty() {
        return simpleAge.daysProperty();
    }

}
