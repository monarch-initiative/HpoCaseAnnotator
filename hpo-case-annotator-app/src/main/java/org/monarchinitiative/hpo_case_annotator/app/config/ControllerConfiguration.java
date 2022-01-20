package org.monarchinitiative.hpo_case_annotator.app.config;

import org.monarchinitiative.hpo_case_annotator.app.controller.StatusBarController;
import org.monarchinitiative.hpo_case_annotator.forms.*;
import org.monarchinitiative.hpo_case_annotator.forms.v2.*;
import org.monarchinitiative.hpo_case_annotator.forms.v2.individual.IndividualController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.individual.IndividualVariantSummaryController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.individual.PedigreeController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.individual.PedigreeMemberController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.monarchinitiative.hpo_case_annotator.forms.v2.ontotree.OntologyTreeBrowserController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.PhenotypeBrowserController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.PhenotypeEntryController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.PhenotypicFeatureController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.PhenotypicFeaturesTableController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.variant.BreakendController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.variant.VcfBreakendVariantController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.variant.VcfSequenceVariantController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.variant.VcfSymbolicVariantController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * All beans of this configuration have <em>prototype</em> scope.
 */
@Configuration
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
    public VariantSummaryController variantSummaryController(HCAControllerFactory hcaControllerFactory) {
        return new VariantSummaryController(hcaControllerFactory);
    }

    @Bean
    @Scope("prototype")
    public PedigreeController pedigreeController(HCAControllerFactory hcaControllerFactory) {
        return new PedigreeController(hcaControllerFactory);
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
    public VcfSequenceVariantController vcfSequenceVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        return new VcfSequenceVariantController(genomicAssemblyRegistry);
    }

    @Bean
    @Scope("prototype")
    public VcfSymbolicVariantController vcfSymbolicVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        return new VcfSymbolicVariantController(genomicAssemblyRegistry);
    }

    @Bean
    @Scope("prototype")
    public VcfBreakendVariantController vcfBreakendVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        return new VcfBreakendVariantController(genomicAssemblyRegistry);
    }

    @Bean
    @Scope("prototype")
    public BreakendController breakendController() {
        return new BreakendController();
    }

    /* *****************************************  Individual controllers   ***************************************** */

    @Bean
    @Scope("prototype")
    public PedigreeMemberController pedigreeMemberController(HCAControllerFactory hcaControllerFactory) {
        return new PedigreeMemberController();
    }

    @Bean
    @Scope("prototype")
    public IndividualController individualController(HCAControllerFactory hcaControllerFactory) {
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

    @Bean
    @Scope("prototype")
    public OntologyTreeBrowserController ontologyTreeBrowserController() {
        return new OntologyTreeBrowserController();
    }

    /* *****************************************         Various           ****************************************** */
    @Bean
    @Scope("prototype")
    public AgeController ageController() {
        return new AgeController();
    }

    /* *****************************************          Utils            ****************************************** */

    @Bean
    @Scope("prototype")
    public StatusBarController statusBarController() {
        return new StatusBarController();
    }
}
