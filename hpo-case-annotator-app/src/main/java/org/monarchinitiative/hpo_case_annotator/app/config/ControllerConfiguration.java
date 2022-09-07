package org.monarchinitiative.hpo_case_annotator.app.config;

import org.monarchinitiative.hpo_case_annotator.app.controller.StatusBarController;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.*;
import org.monarchinitiative.hpo_case_annotator.forms.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.liftover.LiftoverController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.*;
import org.monarchinitiative.hpo_case_annotator.forms.v2.disease.DiseaseStatusController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.individual.IndividualController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.IndividualVariantSummaryController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.PedigreeController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.individual.PedigreeMemberController;
import org.monarchinitiative.hpo_case_annotator.forms.variants.VariantSummary;
import org.monarchinitiative.hpo_case_annotator.forms.variants.input.*;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.PhenotypeBrowserController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.PhenotypeEntryController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.PhenotypicFeatureController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.PhenotypicFeaturesTableController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * All beans of this configuration have <em>prototype</em> scope.
 */
@Configuration
//@ComponentScan(basePackages = {
//        "org.monarchinitiative.hpo_case_annotator.forms",
//})
public class ControllerConfiguration {

    /* *****************************************     Study controllers     ***************************************** */


    @Bean
    @Scope("prototype")
    public FamilyStudyController familyStudyController() {
        return new FamilyStudyController();
    }

    @Bean
    @Scope("prototype")
    public CohortStudyController cohortStudyController() {
        return new CohortStudyController();
    }

    /* **************************************** Study component controllers **************************************** */

    @Bean
    @Scope("prototype")
    public PublicationController publicationController() {
        return new PublicationController();
    }

    @Bean
    @Scope("prototype")
    public VariantSummary variantSummaryController(HCAControllerFactory hcaControllerFactory) {
        return new VariantSummary();
    }

    @Bean
    @Scope("prototype")
    public PedigreeController pedigreeController(HCAControllerFactory hcaControllerFactory) {
        return new PedigreeController(hcaControllerFactory);
    }

    @Bean
    @Scope("prototype")
    public CohortController cohortController(HCAControllerFactory hcaControllerFactory) {
        return new CohortController(hcaControllerFactory);
    }

    @Bean
    @Scope("prototype")
    public StudyMetadataController studyMetadataController() {
        return new StudyMetadataController();
    }

    /* ***************************************** Sub-component controllers ***************************************** */
    /* *****************************************    Variant controllers    ***************************************** */

    @Bean
    @Scope("prototype")
    public VcfSequenceVariantDataEdit vcfSequenceVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry,
                                                                   FunctionalAnnotationRegistry functionalAnnotationRegistry) {
        VcfSequenceVariantDataEdit controller = new VcfSequenceVariantDataEdit();
        controller.genomicAssemblyRegistryProperty().set(genomicAssemblyRegistry);
        controller.functionalAnnotationRegistryProperty().set(functionalAnnotationRegistry);
        return controller;
    }

    @Bean
    @Scope("prototype")
    public VcfSymbolicVariantDataEdit vcfSymbolicVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry,
                                                                   FunctionalAnnotationRegistry functionalAnnotationRegistry) {
        VcfSymbolicVariantDataEdit controller = new VcfSymbolicVariantDataEdit();
        controller.genomicAssemblyRegistryProperty().set(genomicAssemblyRegistry);
        controller.functionalAnnotationRegistryProperty().set(functionalAnnotationRegistry);
        return controller;
    }

    @Bean
    @Scope("prototype")
    public VcfBreakendVariantDataEdit vcfBreakendVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry,
                                                                   FunctionalAnnotationRegistry functionalAnnotationRegistry) {
        VcfBreakendVariantDataEdit controller = new VcfBreakendVariantDataEdit();
        controller.genomicAssemblyRegistryProperty().set(genomicAssemblyRegistry);
        controller.functionalAnnotationRegistryProperty().set(functionalAnnotationRegistry);
        return controller;
    }

    @Bean
    @Scope("prototype")
    public GenomicBreakendDataEdit breakendController() {
        return new GenomicBreakendDataEdit();
    }

    @Bean
    @Scope("prototype")
    public FunctionalAnnotationTable functionalAnnotationController() {
        return new FunctionalAnnotationTable();
    }

    /* *****************************************  Individual controllers   ***************************************** */

    @Bean
    @Scope("prototype")
    public PedigreeMemberController pedigreeMemberController() {
        return new PedigreeMemberController();
    }

    @Bean
    @Scope("prototype")
    public IndividualController individualController() {
        return new IndividualController();
    }

    @Bean
    @Scope("prototype")
    public IndividualVariantSummaryController individualVariantSummaryController() {
        return new IndividualVariantSummaryController();
    }

    /* *****************************************  Phenotype controllers   ****************************************** */

    @Bean
    @Scope("prototype")
    public PhenotypeBrowserController<ObservablePedigreeMember> phenotypeBrowserController(HCAControllerFactory hcaControllerFactory) {
        return new PhenotypeBrowserController<>(hcaControllerFactory);
    }

    @Bean
    @Scope("prototype")
    public PhenotypeEntryController phenotypeEntryController() {
        return new PhenotypeEntryController();
    }

    @Bean
    @Scope("prototype")
    public PhenotypicFeatureController phenotypicFeatureController() {
        return new PhenotypicFeatureController();
    }

    @Bean
    @Scope("prototype")
    public PhenotypicFeaturesTableController phenotypicFeaturesTableController() {
        return new PhenotypicFeaturesTableController();
    }

    /* *****************************************         Disease           ****************************************** */

    @Bean
    @Scope("prototype")
    public DiseaseStatusController<ObservablePedigreeMember> diseaseStatusController(DiseaseIdentifierService diseaseIdentifierService) {
        return new DiseaseStatusController<>(diseaseIdentifierService);
    }

    @Bean
    @Scope("prototype")
    public DiseaseTableController diseaseTableController() {
        return new DiseaseTableController();
    }

    /* *****************************************         Various           ****************************************** */
    @Bean
    @Scope("prototype")
    public AgeController ageController() {
        return new AgeController();
    }

    @Bean
    @Scope("prototype")
    public IndividualDetailController individualDetailController() {
        return new IndividualDetailController();
    }

    /* *****************************************          Utils            ****************************************** */

    @Bean
    @Scope("prototype")
    public StatusBarController statusBarController() {
        return new StatusBarController();
    }

    @Bean
    @Scope("prototype")
    public LiftoverController liftoverController() {
        return new LiftoverController();
    }
}
