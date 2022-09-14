package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.IndividualStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.study.BaseStudyIdStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.study.IndividualStudyIdStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.study.PublicationStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.study.VariantsStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividualStudy;

public class IndividualStudySteps extends BaseStudySteps<ObservableIndividualStudy> {

    @Override
    public IndividualStudySteps configureSteps() {
        PublicationStep<ObservableIndividualStudy> publication = new PublicationStep<>();
        steps.add(publication);

        VariantsStep<ObservableIndividualStudy> variants = new VariantsStep<>();
        variants.genomicAssemblyRegistryProperty().bind(studyResources.genomicAssemblyRegistryProperty());
        variants.functionalAnnotationRegistryProperty().bind(studyResources.functionalAnnotationRegistryProperty());
        steps.add(variants);

        IndividualStep<ObservableIndividualStudy> individual = new IndividualStep<>();
        individual.hpoProperty().bind(studyResources.hpoProperty());
        individual.diseaseIdentifierServiceProperty().bind(studyResources.diseaseIdentifierServiceProperty());
        individual.variantsProperty().bind(variants.variantsProperty());
        steps.add(individual);

        BaseStudyIdStep<ObservableIndividualStudy> identifiers = new IndividualStudyIdStep();
        steps.add(identifiers);

        return this;
    }

}
