package org.monarchinitiative.hpo_case_annotator.observable.v2;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.model.v2.OntologyClass;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.phenol.ontology.data.TermId;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@Disabled
public class ObservableTimeElementTest {

    @Test
    public void name() {
        ObservableTimeElement ote = new ObservableTimeElement();
        ote.addListener(obs -> System.err.println("Something changed"));
        System.err.println("Setting ontology class");
        ote.setTimeElementCase(TimeElement.TimeElementCase.ONTOLOGY_CLASS);
        OntologyClass adultOnset = OntologyClass.of(TermId.of("HP:0003581"), "Adult onset");
        ote.setOntologyClass(adultOnset);

        System.err.println("Setting age");
        ote.setTimeElementCase(TimeElement.TimeElementCase.AGE);
        ObservableAge age = new ObservableAge();
        ote.setAge(age);
        System.err.println("Setting years, months, and days");
        age.setYears(5);
        age.setMonths(4);
        age.setDays(3);

        // We should stop receiving notifications for Age changes.
        System.err.println("Clearing age");
        ote.setAge(null);

        // We should start receiving notifications for AgeRange changes.
        System.err.println("Setting age range");
        ote.setTimeElementCase(TimeElement.TimeElementCase.AGE_RANGE);
        ObservableAgeRange ageRange = new ObservableAgeRange();
        ote.setAgeRange(ageRange);
        ObservableAge start = new ObservableAge();
        ObservableAge end = new ObservableAge();
        ageRange.setStart(start);
        ageRange.setEnd(end);
        System.err.println("Setting start years");
        start.setYears(10);
        System.err.println("Setting start months");
        start.setMonths(9);
        System.err.println("Setting start days");
        start.setDays(8);

        System.err.println("Setting end years");
        end.setYears(10);
        System.err.println("Setting end months");
        end.setMonths(9);
        System.err.println("Setting end days");
        end.setDays(8);


        // We should stop receiving notifications for AgeRange changes.
        System.err.println("Removing age range ----------------------");
        ote.setAgeRange(null);

        System.err.println("Setting start years");
        start.setYears(11);
        System.err.println("Setting start months");
        start.setMonths(10);
        System.err.println("Setting start days");
        start.setDays(9);

        System.err.println("Setting end years");
        end.setYears(11);
        System.err.println("Setting end months");
        end.setMonths(10);
        System.err.println("Setting end days");
        end.setDays(9);

    }
}