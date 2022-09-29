package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.study;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.monarchinitiative.hpo_case_annotator.forms.StudyResources;
import org.monarchinitiative.hpo_case_annotator.forms.StudyResourcesAware;
import org.monarchinitiative.hpo_case_annotator.forms.pedigree.Pedigree;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.BaseStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.Stepper;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.PedigreeMemberSteps;
import org.monarchinitiative.hpo_case_annotator.observable.v2.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static javafx.beans.binding.Bindings.select;

public class PedigreeStep<T extends ObservableFamilyStudy> extends BaseStep<T> implements StudyResourcesAware {

    private final StudyResources studyResources = new StudyResources();
    private final ListProperty<ObservableCuratedVariant> variants = new SimpleListProperty<>(FXCollections.observableArrayList());

    @FXML
    private Pedigree pedigree;
    @FXML
    private Button add;
    @FXML
    private Button remove;

    public PedigreeStep() {
        super(PedigreeStep.class.getResource("PedigreeStep.fxml"));

        // This indeed is an unusual place for setting up bindings.
        // However, note that we cannot use `initialize` method.
        // The fields of this class are `null` when `initialize` is called in the super constructor.
        pedigree.hpoProperty().bind(studyResources.hpoProperty());
        pedigree.namedEntityFinderProperty().bind(studyResources.namedEntityFinderProperty());
        pedigree.diseaseIdentifierServiceProperty().bind(studyResources.diseaseIdentifierServiceProperty());
        pedigree.variantsProperty().bind(variants);
    }

    public ListProperty<ObservableCuratedVariant> variantsProperty() {
        return variants;
    }

    @FXML
    @Override
    protected void initialize() {
        super.initialize();

        // Should not happen, but let's keep it as an indication of something being wrong
        BooleanBinding pedigreeIsNull = select(data, "pedigree").isNull();
        add.disableProperty().bind(pedigreeIsNull);
        remove.disableProperty().bind(pedigreeIsNull.or(pedigree.getSelectionModel().selectedItemProperty().isNull()));
    }

    @Override
    protected Stream<Observable> dependencies() {
        return Stream.of();
    }

    @Override
    public void invalidated(Observable observable) {
        // no-op
    }

    @Override
    protected void bind(T data) {
        // set flag
        try {
            valueIsBeingSetProgrammatically = true;
            if (data == null)
                return;

            // Pedigree must not null as an invariant of this step. This decision is to simplify the step logic.
            pedigree.dataProperty().bind(data.pedigreeProperty());
            if (data.getPedigree() == null)
                data.setPedigree(new ObservablePedigree());
        } finally {
            valueIsBeingSetProgrammatically = false;
        }
    }

    @Override
    protected void unbind(T data) {
        if (data != null)
            pedigree.dataProperty().unbind();
    }

    @FXML
    private void addButtonAction(ActionEvent e) {
        addPedigreeMember()
                .ifPresent(pedigree.getMembers()::add);
        e.consume();
    }

    @FXML
    private void removeButtonAction(ActionEvent e) {
        int selectedIndex = pedigree.getSelectionModel().getSelectedIndex();
        // The remove button is disabled if pedigree is null, hence the pedigree should not be null here!
        // Contents of membersProperty() should not be null since `Pedigree` initializes the value with an empty list.
        pedigree.getMembers().remove(selectedIndex);

        e.consume();
    }

    private Optional<ObservablePedigreeMember> addPedigreeMember() {
        ObservablePedigreeMember member = new ObservablePedigreeMember();
        Stepper<ObservablePedigreeMember> stepper = new Stepper<>();
        stepper.setData(member);

        PedigreeMemberSteps steps = new PedigreeMemberSteps().configureSteps();
        steps.bindStudyResources(this);
        stepper.stepsProperty().bind(steps.stepsProperty());
        steps.variantsProperty().bind(variants);


        Stage stage = new Stage();
        AtomicBoolean accept = new AtomicBoolean();
        stepper.statusProperty().addListener((obs, old, novel) -> {
            switch (novel) {
                case IN_PROGRESS -> {
                    return; // Do not close the stage
                }
                case FINISH -> accept.set(true);
                case CANCEL -> accept.set(false);
            }
            stage.close();
        });

        stage.initOwner(getParent().getScene().getWindow());
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add pedigree member");
        stage.setResizable(true);
        Scene scene = new Scene(stepper);
        // Bind "this" to "that", not "that" to "this"!
        Bindings.bindContent(scene.getStylesheets(), getScene().getStylesheets());
        stage.setScene(scene);

        stage.showAndWait();

        return accept.get()
                ? Optional.of(member)
                : Optional.empty();
    }

    @Override
    public StudyResources getStudyResources() {
        return studyResources;
    }

}
