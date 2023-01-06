package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.study;

import org.monarchinitiative.hpo_case_annotator.observable.v2.*;

import java.util.ArrayList;
import java.util.List;

public class IndividualStudyIdStep extends BaseStudyIdStep<ObservableIndividualStudy> {

    public IndividualStudyIdStep() {
        super(IndividualStudyIdStep.class.getResource("IndividualStudyIdStep.fxml"));
    }

    @Override
    protected String generateId(ObservableIndividualStudy data) {
        List<String> parts = new ArrayList<>();

        ObservablePublication publication = data.getPublication();
        if (publication != null) {
            String publicationSummary = summarizePublication(publication);

            if (!publicationSummary.isEmpty()) {
                parts.add(publicationSummary);
            }
        }


        ObservableIndividual individual = data.getIndividual();
        if (individual != null) {
            StringBuilder individualBuilder = new StringBuilder();
            String id = individual.getId();
            if (id != null) {
                individualBuilder.append(id);
            }

            addSeparator(individualBuilder, '_');
            ObservableTimeElement age = individual.getAge();
            if (age != null) {
                String ageStr = summarizeAge(age);
                individualBuilder.append(ageStr);
            }
            if (!individualBuilder.isEmpty())
                parts.add(individualBuilder.toString());
        }

        return String.join("-", parts);
    }

}
