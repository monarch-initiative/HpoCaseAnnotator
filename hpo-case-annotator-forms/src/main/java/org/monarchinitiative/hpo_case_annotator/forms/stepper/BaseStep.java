package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxBindingObservableDataComponent;

import java.io.IOException;
import java.net.URL;
import java.util.stream.Stream;

public abstract class BaseStep<T> extends VBoxBindingObservableDataComponent<T> implements Step<T> , Observable, InvalidationListener {

    @FXML
    private Label header;

    // Indicate that the value is being changed via `dataProperty` and not by the user using the UI components.
    protected boolean valueIsNotBeingSetByUserInteraction;

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
    @Override
    protected void initialize() {
        super.initialize();
        addListener(this::invalidated);
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

    protected abstract Stream<Observable> dependencies();

    @Override
    public void addListener(InvalidationListener listener) {
        dependencies().forEach(obs -> obs.addListener(listener));
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        dependencies().forEach(obs -> obs.removeListener(listener));
    }
}
