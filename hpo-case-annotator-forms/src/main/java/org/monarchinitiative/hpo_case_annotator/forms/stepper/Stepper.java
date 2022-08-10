package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * {@link Stepper} constructs {@link T} in one or more steps.
 *
 * @param <T> type of the data under construction
 */
public class Stepper<T> implements ObservableDataController<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Stepper.class);

    /**
     * Steps of the stepper.
     */
    private final ListProperty<Step<T>> steps = new SimpleListProperty<>();
    /**
     * Notify the outer app that we're done here.
     * The consumer receives {@code true} or {@code false} if the user clicked on the finish or cancel buttons.
     */
    private final ObjectProperty<Consumer<Boolean>> conclude = new SimpleObjectProperty<>();
    /**
     * Index of the current step or {@code -1} if no step is shown. The upper bound is the number of steps minus one.
     */
    private final IntegerProperty currentStep = new SimpleIntegerProperty(-1);
    /**
     * Data to create in the stepper
     */
    private final ObjectProperty<T> data = new SimpleObjectProperty<>();

    @FXML
    private StackPane content;
    @FXML
    private Button previous;
    @FXML
    private Button next;

    @FXML
    private void initialize() {
        content.getChildren().add(new Label("No steps to display")); // always at the bottom of the stack.
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
            LOGGER.info("We have {} steps", list.size());
            currentStep.set(list.isEmpty() ? -1 : 0);

        });

        currentStep.addListener((obs, old, novel) -> {
            LOGGER.info("Setting to step {}/{}", novel, steps.size());
            Step<T> step = novel.intValue() < 0
                    ? null
                    : steps.get(novel.intValue());
            loadStep(step);
        });
    }

    private void loadStep(Step<T> step) {
        int currentContentSize = content.getChildren().size();
        if (currentContentSize > 1)
            // Remove the previous step, if any. There is always the placeholder at the bottom.
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

    public Consumer<Boolean> getConclude() {
        return conclude.get();
    }

    public void setConclude(Consumer<Boolean> conclude) {
        this.conclude.set(conclude);
    }

    public ObjectProperty<Consumer<Boolean>> concludeProperty() {
        return conclude;
    }

    @Override
    public ObjectProperty<T> dataProperty() {
        return data;
    }

    @FXML
    private void cancelAction(ActionEvent e) {
        concludeStepper(false);
        e.consume();
    }

    private void concludeStepper(boolean shouldCommit) {
        Consumer<Boolean> consumer = conclude.get();
        if (consumer != null)
            consumer.accept(shouldCommit);
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
        concludeStepper(true);
        e.consume();
    }

}
