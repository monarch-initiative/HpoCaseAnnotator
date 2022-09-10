package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.study.*;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;

public class FamilyStudySteps extends BaseStudySteps<ObservableFamilyStudy> {

    @Override
    public FamilyStudySteps configureSteps() {
        PublicationStep<ObservableFamilyStudy> publication = new PublicationStep<>();
        steps.add(publication);

        VariantsStep<ObservableFamilyStudy> variants = new VariantsStep<>();
        variants.setHeader("Add genomic variants identified in the investigated individual.");
        variants.genomicAssemblyRegistryProperty().bind(studyResources.genomicAssemblyRegistryProperty());
        variants.functionalAnnotationRegistryProperty().bind(studyResources.functionalAnnotationRegistryProperty());
        steps.add(variants);

        PedigreeStep<ObservableFamilyStudy> pedigree = new PedigreeStep<>();
        pedigree.setHeader("Add data regarding the investigated family members.");
        pedigree.bindStudyResources(this);
        pedigree.variantsProperty().bind(variants.variantsProperty());
        steps.add(pedigree);

        FamilyStudyIdStep identifiers = new FamilyStudyIdStep();
        steps.add(identifiers);

        return this;
    }

}
