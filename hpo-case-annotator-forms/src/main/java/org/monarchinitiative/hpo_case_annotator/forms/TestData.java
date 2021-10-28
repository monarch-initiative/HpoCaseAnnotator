package org.monarchinitiative.hpo_case_annotator.forms;

import org.monarchinitiative.hpo_case_annotator.forms.model.PhenotypeDescription;
import org.monarchinitiative.hpo_case_annotator.forms.model.PhenotypeDescriptionSimple;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.time.Period;
import java.util.List;

@Deprecated // TODO: 10/28/21 remove when done with the controller development
public class TestData {

    @Deprecated
    public static List<PhenotypeDescription> sampleValues() {
        return List.of(PhenotypeDescriptionSimple.of(
                        TermId.of("HP:0031972"), "Presyncope", Period.ZERO, Period.of(1, 2, 3), true),
                PhenotypeDescriptionSimple.of(TermId.of("HP:0001166"), "Arachnodactyly", Period.of(12, 0, 1), Period.of(12, 10, 3), false),
                PhenotypeDescriptionSimple.of(TermId.of("HP:0001167"), "Abnormality of finger", Period.of(15, 9, 4), Period.of(20, 7, 6), true),
                PhenotypeDescriptionSimple.of(TermId.of("HP:0000822"), "Hypertension", Period.of(28, 3, 15), Period.of(40, 10, 3), false),
                PhenotypeDescriptionSimple.of(TermId.of("HP:0011025"), "Abnormal cardiovascular system physiology", Period.ZERO, Period.of(80, 0, 0), true)
        );
    }
}
