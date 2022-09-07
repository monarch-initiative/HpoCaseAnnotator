package org.monarchinitiative.hpo_case_annotator.forms;

import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.publication.Publication;
import org.monarchinitiative.hpo_case_annotator.forms.v2.*;
import org.monarchinitiative.hpo_case_annotator.forms.v2.individual.IndividualController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.IndividualVariantSummaryController;
import org.monarchinitiative.hpo_case_annotator.forms.tree.SimpleOntologyClassTreeView;
import org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.*;
import org.monarchinitiative.hpo_case_annotator.forms.variants.input.GenomicBreakendDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.variants.input.VcfBreakendVariantDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.variants.input.VcfSequenceVariantDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.variants.input.VcfSymbolicVariantDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.variants.input.cache.HgvsVariantController;

public class ControllerFactory implements HCAControllerFactory {

    private final GenomicAssemblyRegistry genomicAssemblyRegistry;
    private final FunctionalAnnotationRegistry functionalAnnotationRegistry;
    private final GeneIdentifierService geneIdentifierService;

    public ControllerFactory(GenomicAssemblyRegistry genomicAssemblyRegistry,
                             FunctionalAnnotationRegistry functionalAnnotationRegistry,
                             GeneIdentifierService geneIdentifierService) {
        this.genomicAssemblyRegistry = genomicAssemblyRegistry;
        this.functionalAnnotationRegistry = functionalAnnotationRegistry;
        this.geneIdentifierService = geneIdentifierService;
    }

    public Object getController(Class<?> clz) {
        return call(clz);
    }

    @Override
    public Object call(Class<?> clz) {
        // variant
        if (clz.equals(VcfSequenceVariantDataEdit.class)) {
            VcfSequenceVariantDataEdit controller = new VcfSequenceVariantDataEdit();
            controller.genomicAssemblyRegistryProperty().set(genomicAssemblyRegistry);
            controller.functionalAnnotationRegistryProperty().set(functionalAnnotationRegistry);
            return controller;
        } else if (clz.equals(VcfSymbolicVariantDataEdit.class)) {
            VcfSymbolicVariantDataEdit controller = new VcfSymbolicVariantDataEdit();
            controller.genomicAssemblyRegistryProperty().set(genomicAssemblyRegistry);
            controller.functionalAnnotationRegistryProperty().set(functionalAnnotationRegistry);
            return controller;
        } else if (clz.equals(VcfBreakendVariantDataEdit.class)) {
            VcfBreakendVariantDataEdit controller = new VcfBreakendVariantDataEdit();
            controller.genomicAssemblyRegistryProperty().set(genomicAssemblyRegistry);
            controller.functionalAnnotationRegistryProperty().set(functionalAnnotationRegistry);
            return controller;
        } else if (clz.equals(GenomicBreakendDataEdit.class)) {
            return new GenomicBreakendDataEdit();
        } else if (clz.equals(HgvsVariantController.class)) {
            return new HgvsVariantController(geneIdentifierService);
        }

        // various
        else if (clz.equals(AgeController.class)) {
            return new AgeController();
        }

        // phenotypes
        else if (clz.equals(org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.PhenotypicFeatureController.class)) {
            return new org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.PhenotypicFeatureController();
        } else if (clz.equals(PhenotypicFeaturesTableController.class)) {
            return new PhenotypicFeaturesTableController();
        } else if (clz.equals(TextMiningController.class)) {
            return new TextMiningController();
        } else if (clz.equals(PhenotypeEntryController.class)) {
            return new PhenotypeEntryController();
        } else if (clz.equals(PhenotypeBrowserController.class)) {
            return new PhenotypeBrowserController<>(this);
        } else if (clz.equals(SimpleOntologyClassTreeView.class)) {
            return new SimpleOntologyClassTreeView();
        }

        // individual & family
        else if (clz.equals(IndividualController.class)) {
            return new IndividualController();
        } else if (clz.equals(IndividualVariantSummaryController.class)) {
            return new IndividualVariantSummaryController();
        } else if (clz.equals(org.monarchinitiative.hpo_case_annotator.forms.v2.PedigreeController.class)) {
            return new org.monarchinitiative.hpo_case_annotator.forms.v2.PedigreeController(this);
        } else if (clz.equals(org.monarchinitiative.hpo_case_annotator.forms.v2.FamilyStudyController.class)) {
            return new org.monarchinitiative.hpo_case_annotator.forms.v2.FamilyStudyController();
        } else if (clz.equals(org.monarchinitiative.hpo_case_annotator.forms.v2.individual.PedigreeMemberController.class)) {
            return new org.monarchinitiative.hpo_case_annotator.forms.v2.individual.PedigreeMemberController();
        } else if (clz.equals(IndividualDetailController.class)) {
            return new IndividualDetailController();
        } else if (clz.equals(CohortController.class)) {
            return new CohortController(this);
        } else if (clz.equals(CohortStudyController.class)) {
            return new CohortStudyController();
        }

        // New View option
//        else if (clz.equals(PedigreeMemberIdsComponent.class)) {
//            return new PedigreeMemberIdsComponent();
//        } else if (clz.equals(TimeElementComponent.class)) {
//            return new TimeElementComponent();
//        } else if (clz.equals(PhenotypeDataEdit.class)) {
//            return new PhenotypeDataEdit();
//        }

        // publication & metadata
        else if (clz.equals(PublicationController.class)) {
            return new PublicationController();
        } else if (clz.equals(StudyMetadataController.class)) {
            return new StudyMetadataController();
        } else if (clz.equals(Publication.class)) {
            return new Publication();
        } else {
            throw new RuntimeException("Unknown controller " + clz);
        }

    }
}
