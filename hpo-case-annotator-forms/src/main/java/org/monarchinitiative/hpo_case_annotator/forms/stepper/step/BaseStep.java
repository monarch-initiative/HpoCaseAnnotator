package org.monarchinitiative.hpo_case_annotator.forms.stepper.step;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxBindingObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.Step;

import java.io.IOException;
import java.net.URL;
import java.util.stream.Stream;

public abstract class BaseStep<T> extends VBoxBindingObservableDataComponent<T> implements Step<T>, Observable, InvalidationListener {

    // Indicate that the value is being changed via `dataProperty` and not by the user using the UI components.
    protected boolean valueIsBeingSetProgrammatically;

    protected BaseStep(URL location) {
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
        addListener(this);
    }

    @Override
    public Parent getContent() {
        return this;
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
