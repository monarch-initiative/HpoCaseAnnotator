package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.study.*;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;

public class FamilyStudySteps extends BaseStudySteps<ObservableFamilyStudy> {

    @Override
    public FamilyStudySteps configureSteps() {
        PublicationStep<ObservableFamilyStudy> publication = new PublicationStep<>();
        steps.add(publication);

        VariantsStep<ObservableFamilyStudy> variants = new VariantsStep<>();
        variants.genomicAssemblyRegistryProperty().bind(studyResources.genomicAssemblyRegistryProperty());
        variants.functionalAnnotationRegistryProperty().bind(studyResources.functionalAnnotationRegistryProperty());
        variants.liftoverServiceProperty().bind(studyResources.liftoverServiceProperty());
        steps.add(variants);

        PedigreeStep<ObservableFamilyStudy> pedigree = new PedigreeStep<>();
        pedigree.bindStudyResources(this);
        pedigree.variantsProperty().bind(variants.variantsProperty());
        steps.add(pedigree);

        FamilyStudyIdStep identifiers = new FamilyStudyIdStep();
        steps.add(identifiers);

        return this;
    }

}
