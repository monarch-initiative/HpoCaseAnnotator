package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.study;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.publication.PublicationBinding;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.BaseStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.util.PubmedIO;
import org.monarchinitiative.hpo_case_annotator.model.v2.Publication;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePublication;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;

import java.util.stream.Stream;

public class PublicationStep<T extends ObservableStudy> extends BaseStep<T> {

    @FXML
    private CheckBox publicationIsUnknown;
    @FXML
    private VBox publicationDataBox;
    @FXML
    private TitledTextField pmidTextField;
    @FXML
    private Button fetchFromPubmed;
    @FXML
    private PublicationBinding publicationBinding;

    public PublicationStep() {
        super(PublicationStep.class.getResource("PublicationStep.fxml"));
    }

    @Override
    protected void initialize() {
        super.initialize();
        disableProperty().bind(data.isNull());
        publicationDataBox.disableProperty().bind(publicationIsUnknown.selectedProperty());
    }

    @Override
    protected Stream<Observable> dependencies() {
        return Stream.of(publicationIsUnknown.selectedProperty(), publicationBinding);
    }

    @Override
    public void invalidated(Observable obs) {
        if (valueIsBeingSetProgrammatically || data.get() == null)
            return;

        T data = this.data.get();

        if (obs.equals(publicationIsUnknown.selectedProperty())) {
            if (publicationIsUnknown.isSelected())
                data.setPublication(null);
            else {
                if (data.getPublication() == null)
                    data.setPublication(new ObservablePublication());
            }
        }
        // Otherwise, publication must have changed and that is handled elsewhere.

    }

    @Override
    protected void bind(T data) {
        try {
            valueIsBeingSetProgrammatically = true;

            publicationIsUnknown.setSelected(data == null || data.getPublication() == null);
            if (data != null) {
                publicationBinding.dataProperty().bind(data.publicationProperty());
            } else {
                publicationBinding.setData(null);
            }
        } finally {
            valueIsBeingSetProgrammatically = false;
        }
    }

    @Override
    protected void unbind(T data) {
        if (data != null)
            publicationBinding.dataProperty().unbind();
    }

    @FXML
    private void fetchFromPubmedAction(ActionEvent e) {
        String val = pmidTextField.getText();
        // TODO - add executor service, handle wrong ID, unable to reach PubMed etc.

        Task<Publication> task = PubmedIO.v2publication(val);

        // trip to PubMed was successful
        task.setOnSucceeded(we -> Platform.runLater(() -> {
            // Result of `data.get()` must be non-null because the entire UI is enabled only iff `data.get() != null`.
            data.get().setPublication(new ObservablePublication(task.getValue()));
            pmidTextField.setText(null);
        }));

        task.run();

        e.consume();
    }

}
