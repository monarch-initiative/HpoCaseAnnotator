package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.*;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCohortStudy;

public class CohortStudySteps extends BaseStudySteps<ObservableCohortStudy> {

    @Override
    protected void configureSteps() {
        PublicationStep<ObservableCohortStudy> publication = new PublicationStep<>();
        publication.setHeader("Set publication data");
        steps.add(publication);

        VariantsStep<ObservableCohortStudy> variants = new VariantsStep<>();
        variants.setHeader("Add genomic variants identified in the investigated individual.");
        variants.genomicAssemblyRegistryProperty().bind(studyResources.genomicAssemblyRegistryProperty());
        variants.functionalAnnotationRegistryProperty().bind(studyResources.functionalAnnotationRegistryProperty());
        steps.add(variants);

        // TODO - implement adding cohort members
//        IndividualStep<ObservableIndividualStudy> individual = new IndividualStep<>();
//        individual.setHeader("Add data regarding the investigated individual.");
//        individual.hpoProperty().bind(hpo);
//        individual.diseaseIdentifierServiceProperty().bind(diseaseIdentifierService);
//        individual.variantsProperty().bind(variants.variantsProperty());

        BaseStudyIdStep<ObservableCohortStudy> identifiers = new CohortStudyIdStep();
        steps.add(identifiers);
    }

}
