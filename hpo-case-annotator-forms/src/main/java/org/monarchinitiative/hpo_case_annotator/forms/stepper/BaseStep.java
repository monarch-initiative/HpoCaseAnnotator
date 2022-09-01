package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;

import java.io.IOException;
import java.net.URL;

public abstract class BaseStep<T> extends VBox implements Step<T> {

    protected final ObjectProperty<T> data = new SimpleObjectProperty<>();

    @FXML
    private Label header;

    public BaseStep(URL location) {
        FXMLLoader loader = new FXMLLoader(location);
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
        data.addListener(onDataChange());
    }


    private ChangeListener<T> onDataChange() {
        return (obs, old, novel) -> {
            if (old != null) unbind(old);
            if (novel != null) bind(novel);
        };
    }

    @Override
    public ObjectProperty<T> dataProperty() {
        return data;
    }

    public String getHeader() {
        return header.textProperty().get();
    }

    public StringProperty headerProperty() {
        return header.textProperty();
    }

    public void setHeader(String header) {
        this.header.textProperty().set(header);
    }

    protected abstract void bind(T data);

    protected abstract void unbind(T data);

}
