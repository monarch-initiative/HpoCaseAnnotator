package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.FamilyStudyIdStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.PublicationStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.VariantsStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;

public class FamilyStudySteps extends BaseStudySteps<ObservableFamilyStudy> {

    @Override
    protected void configureSteps() {
        PublicationStep<ObservableFamilyStudy> publication = new PublicationStep<>();
        publication.setHeader("Set publication data");
        steps.add(publication);

        VariantsStep<ObservableFamilyStudy> variants = new VariantsStep<>();
        variants.setHeader("Add genomic variants identified in the investigated individual.");
        variants.genomicAssemblyRegistryProperty().bind(studyResources.genomicAssemblyRegistryProperty());
        variants.functionalAnnotationRegistryProperty().bind(studyResources.functionalAnnotationRegistryProperty());
        steps.add(variants);

        // TODO - implement adding family members members
//        IndividualStep<ObservableIndividualStudy> individual = new IndividualStep<>();
//        individual.setHeader("Add data regarding the investigated individual.");
//        individual.hpoProperty().bind(hpo);
//        individual.diseaseIdentifierServiceProperty().bind(diseaseIdentifierService);
//        individual.variantsProperty().bind(variants.variantsProperty());

        FamilyStudyIdStep identifiers = new FamilyStudyIdStep();
        steps.add(identifiers);
    }

}
