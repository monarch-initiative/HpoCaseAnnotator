package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.study;

import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextArea;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.BaseStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.*;

import java.net.URL;
import java.util.stream.Stream;

public abstract class BaseStudyIdStep<T extends ObservableStudy> extends BaseStep<T> {

    @FXML
    private TitledTextField studyId;
    @FXML
    private TitledTextArea freeTextMetadata;

    protected BaseStudyIdStep(URL location) {
        super(location);
    }

    protected static String summarizePublication(ObservablePublication publication) {
        StringBuilder publicationBuilder = new StringBuilder();
        String authors = publication.getAuthors();
        if (authors != null) {
            String lastName = authors.substring(0, authors.indexOf(" "));
            publicationBuilder.append(lastName);
        }
        addSeparator(publicationBuilder, '_');
        Integer year = publication.getYear();
        if (year != null) {
            publicationBuilder.append(year);
        }

        addSeparator(publicationBuilder, '_');
        String pmid = publication.getPmid();
        if (pmid != null) {
            publicationBuilder.append(pmid);
        }
        return publicationBuilder.toString();
    }

    protected static void addSeparator(StringBuilder builder, char separator) {
        if (!builder.isEmpty())
            builder.append(separator);
    }

    protected static String summarizeAge(ObservableTimeElement age) {
        return switch (age.getTimeElementCase()) {
            case GESTATIONAL_AGE -> {
                StringBuilder builder = new StringBuilder();
                ObservableGestationalAge ga = age.getGestationalAge();
                if (ga.getWeeks() != null)
                    builder.append(ga.getWeeks()).append('w');
                if (ga.getDays() != null)
                    builder.append(ga.getDays()).append('d');
                yield builder.toString();
            }
            case AGE -> {
                StringBuilder builder = new StringBuilder();
                ObservableAge a = age.getAge();
                if (a.getYears() != null)
                    builder.append(a.getYears()).append('y');
                if (a.getMonths() != null)
                    builder.append(a.getMonths()).append('m');
                if (a.getDays() != null)
                    builder.append(a.getDays()).append('d');

                yield builder.toString();
            }
            case AGE_RANGE, ONTOLOGY_CLASS -> "";
        };
    }

    @Override
    protected Stream<Observable> dependencies() {
        return Stream.of(studyId.textProperty(), freeTextMetadata.textProperty());
    }

    @Override
    public void invalidated(Observable obs) {
        // no-op as of now
    }

    @FXML
    private void generateIdButtonAction(ActionEvent e) {
        studyId.setText(generateId(getData()));
        e.consume();
    }

    protected abstract String generateId(T data);

    @Override
    protected void bind(T data) {
        try {
            valueIsBeingSetProgrammatically = true;

            if (data != null) {
                studyId.textProperty().bindBidirectional(data.idProperty());
                if (data.getStudyMetadata() == null)
                    data.setStudyMetadata(new ObservableStudyMetadata());
                freeTextMetadata.textProperty().bindBidirectional(data.getStudyMetadata().freeTextProperty());
            } else {
                studyId.setText(null);
                freeTextMetadata.setPromptText(null);
            }
        } finally {
            valueIsBeingSetProgrammatically = false;
        }
    }

    @Override
    protected void unbind(T data) {
        if (data != null) {
            studyId.textProperty().unbindBidirectional(data.idProperty());
            if (data.getStudyMetadata() != null)
                freeTextMetadata.textProperty().unbindBidirectional(data.getStudyMetadata().freeTextProperty());
        }
    }
}
