package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.ResourceAwareStudySteps;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.disease.DiseaseStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.genotype.VariantGenotypeStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.id.BaseIndividuaIdStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.phenotype.BasePhenotypeStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;

abstract class BaseIndividualSteps<T extends BaseObservableIndividual> extends ResourceAwareStudySteps<T> {

    private final ListProperty<ObservableCuratedVariant> variants = new SimpleListProperty<>(FXCollections.observableArrayList());

    @Override
    public BaseIndividualSteps<T> configureSteps() {
        BaseIndividuaIdStep<T> credentials = getIdStep();
        credentials.setHeader("Enter credentials");
        steps.add(credentials);

        BasePhenotypeStep<T> phenotype = getPhenotypeStep();
        phenotype.hpoProperty().bind(studyResources.hpoProperty());
        phenotype.setHeader("Add phenotypic features");
        steps.add(phenotype);

        DiseaseStep<T> disease = new DiseaseStep<>();
        disease.diseaseIdentifierServiceProperty().bind(studyResources.diseaseIdentifierServiceProperty());
        disease.setHeader("Add diseases");
        steps.add(disease);

        VariantGenotypeStep<T> genotypes = new VariantGenotypeStep<>();
        genotypes.variantsProperty().bind(variants);
        genotypes.setHeader("Set variant genotypes");
        steps.add(genotypes);

        return this;
    }

    public ListProperty<ObservableCuratedVariant> variantsProperty() {
        return variants;
    }

    protected abstract BasePhenotypeStep<T> getPhenotypeStep();

    protected abstract BaseIndividuaIdStep<T> getIdStep();


}
