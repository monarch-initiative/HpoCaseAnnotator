package org.monarchinitiative.hpo_case_annotator.forms;

import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementComponent;
import org.monarchinitiative.hpo_case_annotator.forms.nvo.FamilyStudy;
import org.monarchinitiative.hpo_case_annotator.forms.pedigree.Pedigree;
import org.monarchinitiative.hpo_case_annotator.forms.pedigree.PedigreeMember;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypeDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.publication.Publication;
import org.monarchinitiative.hpo_case_annotator.forms.v2.*;
import org.monarchinitiative.hpo_case_annotator.forms.v2.individual.IndividualController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.IndividualVariantSummaryController;
import org.monarchinitiative.hpo_case_annotator.forms.tree.SimpleOntologyClassTreeView;
import org.monarchinitiative.hpo_case_annotator.forms.component.PedigreeMemberIdsComponent;
import org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.*;
import org.monarchinitiative.hpo_case_annotator.forms.v2.variant.*;
import org.monarchinitiative.hpo_case_annotator.forms.v2.variant.cache.HgvsVariantController;
import org.monarchinitiative.hpo_case_annotator.forms.variants.VariantSummaryController;

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
        if (clz.equals(VcfSequenceVariantController.class)) {
            return new VcfSequenceVariantController(genomicAssemblyRegistry, functionalAnnotationRegistry);
        } else if (clz.equals(VcfSymbolicVariantController.class)) {
            return new VcfSymbolicVariantController(genomicAssemblyRegistry, functionalAnnotationRegistry);
        } else if (clz.equals(VcfBreakendVariantController.class)) {
            return new VcfBreakendVariantController(genomicAssemblyRegistry, functionalAnnotationRegistry);
        } else if (clz.equals(BreakendController.class)) {
            return new BreakendController();
        } else if (clz.equals(HgvsVariantController.class)) {
            return new HgvsVariantController(geneIdentifierService);
        } else if (clz.equals(VariantSummaryController.class)) {
            return new VariantSummaryController(this);
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
