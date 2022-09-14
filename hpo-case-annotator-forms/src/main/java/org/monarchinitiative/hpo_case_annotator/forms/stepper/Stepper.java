package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxObservableDataComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * {@link Stepper} constructs {@link T} in one or more steps.
 *
 * @param <T> type of the data under construction.
 */
public class Stepper<T> extends VBoxObservableDataComponent<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Stepper.class);

    public enum Status {
        /**
         * The stepper is still being worked on.
         */
        IN_PROGRESS,
        /**
         * The user indicated that the work has been finished and the result should be accepted.
         */
        FINISH,
        /**
         * The user indicated that the work has been finished but the result should be discarded.
         */
        CANCEL
    }

    /**
     * Steps of the stepper.
     */
    private final ListProperty<Step<T>> steps = new SimpleListProperty<>();
    /**
     * Notify the calling code that we're done here.
     * The app should handle the changes of the {@link #statusProperty()} accordingly.
     */
    private final ObjectProperty<Status> status = new SimpleObjectProperty<>(Status.IN_PROGRESS);
    /**
     * Index of the current step or {@code -1} if no step is shown. The upper bound is the number of steps minus one.
     */
    private final IntegerProperty currentStep = new SimpleIntegerProperty(-1);

    @FXML
    private StackPane content;
    @FXML
    private Button previous;
    @FXML
    private Button next;

    public Stepper() {
        FXMLLoader loader = new FXMLLoader(Stepper.class.getResource("Stepper.fxml"));
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
        previous.disableProperty().bind(currentStep.lessThan(0)
                .or(currentStep.isEqualTo(0)));
        next.disableProperty().bind(currentStep.lessThan(0)
                .or(currentStep.isEqualTo(steps.sizeProperty().subtract(1))));

        steps.addListener((ListChangeListener<? super Step<T>>) change -> {
            // (un)bind data to removed/added steps.
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Step<T> step : change.getAddedSubList())
                        step.dataProperty().bindBidirectional(data);

                } else if (change.wasRemoved()) {
                    for (Step<T> step : change.getRemoved())
                        step.dataProperty().unbindBidirectional(data);

                } else {
                    LOGGER.info("Not handling step change: {}", change);
                }
            }

            // Set the current step
            ObservableList<? extends Step<T>> list = change.getList();
            currentStep.set(list.isEmpty() ? -1 : 0);

        });

        currentStep.addListener((obs, old, novel) -> {
            Step<T> step = novel.intValue() < 0
                    ? null
                    : steps.get(novel.intValue());
            loadStep(step);
        });
    }

    private void loadStep(Step<T> step) {
        int currentContentSize = content.getChildren().size();
        if (currentContentSize > 0)
            // Remove the previous step, if any.
            content.getChildren().remove(currentContentSize - 1);

        if (step != null)
            // Show step if required.
            content.getChildren().add(step.getContent());
    }

    public ObservableList<Step<T>> getSteps() {
        return steps.get();
    }

    public void setSteps(ObservableList<Step<T>> steps) {
        this.steps.set(steps);
    }

    public ListProperty<Step<T>> stepsProperty() {
        return steps;
    }

    public ObjectProperty<Status> statusProperty() {
        return status;
    }

    @Override
    public ObjectProperty<T> dataProperty() {
        return data;
    }

    @FXML
    private void cancelAction(ActionEvent e) {
        status.set(Status.CANCEL);
        e.consume();
    }

    @FXML
    private void nextStep(ActionEvent e) {
        currentStep.set(currentStep.get() + 1);
        e.consume();
    }

    @FXML
    private void previousStep(ActionEvent event) {
        currentStep.set(currentStep.get() - 1);
        event.consume();
    }

    @FXML
    private void finishAction(ActionEvent e) {
        status.set(Status.FINISH);
        e.consume();
    }

}
