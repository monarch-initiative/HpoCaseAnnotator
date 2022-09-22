package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.study.*;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCohortStudy;

public class CohortStudySteps extends BaseStudySteps<ObservableCohortStudy> {

    @Override
    public CohortStudySteps configureSteps() {
        PublicationStep<ObservableCohortStudy> publication = new PublicationStep<>();
        steps.add(publication);

        VariantsStep<ObservableCohortStudy> variants = new VariantsStep<>();
        variants.genomicAssemblyRegistryProperty().bind(studyResources.genomicAssemblyRegistryProperty());
        variants.functionalAnnotationRegistryProperty().bind(studyResources.functionalAnnotationRegistryProperty());
        variants.liftoverServiceProperty().bind(studyResources.liftoverServiceProperty());
        steps.add(variants);

        // TODO - implement adding cohort members
//        IndividualStep<ObservableIndividualStudy> individual = new IndividualStep<>();
//        individual.setHeader("Add data regarding the investigated individual.");
//        individual.hpoProperty().bind(hpo);
//        individual.diseaseIdentifierServiceProperty().bind(diseaseIdentifierService);
//        individual.variantsProperty().bind(variants.variantsProperty());

        BaseStudyIdStep<ObservableCohortStudy> identifiers = new CohortStudyIdStep();
        steps.add(identifiers);

        return this;
    }

}
