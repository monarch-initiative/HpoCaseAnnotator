package org.monarchinitiative.hpo_case_annotator.forms.stepper.step;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.publication.PublicationBinding;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.BaseStep;
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
        publicationDataBox.disableProperty().bind(publicationIsUnknown.selectedProperty());
    }

    @Override
    protected Stream<Observable> dependencies() {
        return Stream.of(publicationIsUnknown.selectedProperty(), publicationBinding);
    }

    @Override
    public void invalidated(Observable obs) {
        // check flag
        if (valueIsNotBeingSetByUserInteraction)
            return;

        T data = this.data.get();
        if (data == null)
            return;

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
        if (data != null) {
            try {
                valueIsNotBeingSetByUserInteraction = true;
                publicationIsUnknown.setSelected(data.getPublication() == null);
                publicationBinding.dataProperty().bind(data.publicationProperty());
            } finally {
                valueIsNotBeingSetByUserInteraction = false;
            }
        }
    }

    @Override
    protected void unbind(T data) {
        if (data != null)
            publicationBinding.dataProperty().unbindBidirectional(data.publicationProperty());
    }

    @FXML
    private void fetchFromPubmedAction(ActionEvent e) {
        String val = pmidTextField.getText();
        // TODO - add executor service, handle wrong ID, unable to reach PubMed etc.

        Task<Publication> task = PubmedIO.v2publication(val);

        // trip to PubMed was successful
        task.setOnSucceeded(we -> Platform.runLater(() -> {
            // 18023225
            Publication publication = task.getValue();
            publicationBinding.setData(new ObservablePublication(publication));
        }));

        task.run();

        e.consume();
    }

    @Override
    public Parent getContent() {
        return this;
    }

}
