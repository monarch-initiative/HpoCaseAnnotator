package org.monarchinitiative.hpo_case_annotator.forms.test;

import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifierService;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.*;
import org.monarchinitiative.hpo_case_annotator.forms.study.PedigreeController;
import org.monarchinitiative.hpo_case_annotator.forms.study.FamilyStudyController;
import org.monarchinitiative.hpo_case_annotator.forms.study.IndividualController;
import org.monarchinitiative.hpo_case_annotator.forms.study.IndividualVariantSummaryController;
import org.monarchinitiative.hpo_case_annotator.forms.study.PedigreeMemberController;
import org.monarchinitiative.hpo_case_annotator.forms.ontotree.OntologyTreeBrowserController;
import org.monarchinitiative.hpo_case_annotator.forms.ontotree.SelectableOntologyTreeController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypeEntryController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypicFeatureController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypicFeaturesTableController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.TextMiningController;
import org.monarchinitiative.hpo_case_annotator.forms.variant.*;
import org.monarchinitiative.hpo_case_annotator.forms.variant.cache.HgvsVariantController;

public class ControllerFactory implements HCAControllerFactory {

    private final Resources resources;
    private final GenomicAssemblyRegistry genomicAssemblyRegistry;
    private final GeneIdentifierService geneIdentifierService;

    public ControllerFactory(Resources resources,
                             GenomicAssemblyRegistry genomicAssemblyRegistry,
                             GeneIdentifierService geneIdentifierService) {
        this.resources = resources;
        this.genomicAssemblyRegistry = genomicAssemblyRegistry;
        this.geneIdentifierService = geneIdentifierService;
    }

    public Object getController(Class<?> clz) {
        return call(clz);
    }

    @Override
    public Object call(Class<?> clz) {
        // variant
        if (clz.equals(VcfSequenceVariantController.class)) {
            return new VcfSequenceVariantController(genomicAssemblyRegistry);
        } else if (clz.equals(VcfSymbolicVariantController.class)) {
            return new VcfSymbolicVariantController(genomicAssemblyRegistry);
        } else if (clz.equals(VcfBreakendVariantController.class)) {
            return new VcfBreakendVariantController(genomicAssemblyRegistry);
        } else if (clz.equals(BreakendController.class)) {
            return new BreakendController();
        }
        else if (clz.equals(StatusBarController.class)) {
            return new StatusBarController();
        } else if (clz.equals(HgvsVariantController.class)) {
            return new HgvsVariantController(geneIdentifierService);
        }

        // unified
        /*
        else if (clz.equals(UnifiedCuratedVariantController.class)) {
            return new UnifiedCuratedVariantController();
        } else if (clz.equals(UnifiedVcfSequenceVariantController.class)) {
            return new UnifiedVcfSequenceVariantController(genomicAssemblyRegistry);
        } else if (clz.equals(UnifiedVcfSymbolicVariantController.class)) {
            return new UnifiedVcfSymbolicVariantController(genomicAssemblyRegistry);
        } else if (clz.equals(UnifiedVcfBreakendVariantController.class)) {
            return new UnifiedVcfBreakendVariantController(genomicAssemblyRegistry);
        } else if (clz.equals(UnifiedBreakendController.class)) {
            return new UnifiedBreakendController();
        }
        */

        // phenotypes
        else if (clz.equals(PhenotypeController.class)) {
            PhenotypeController phenotypeController = new PhenotypeController();
            phenotypeController.ontologyProperty().bind(resources.ontologyProperty());
            return phenotypeController;
        } else if (clz.equals(PhenotypicFeatureController.class)) {
            return new PhenotypicFeatureController();
        } else if (clz.equals(PhenotypicFeaturesTableController.class)) {
            return new PhenotypicFeaturesTableController();
        } else if (clz.equals(TextMiningController.class)) {
            return new TextMiningController();
        } else if (clz.equals(PhenotypeEntryController.class)) {
            return new PhenotypeEntryController();
        } else if (clz.equals(SelectableOntologyTreeController.class)) {
            return new SelectableOntologyTreeController();
        } else if (clz.equals(OntologyController.class)) {
            OntologyController ontologyController = new OntologyController();
            ontologyController.ontologyProperty().bind(resources.ontologyProperty());
            return ontologyController;
        } else if (clz.equals(PhenotypeBrowserController.class)) {
            PhenotypeBrowserController phenotypeBrowserController = new PhenotypeBrowserController();
            phenotypeBrowserController.ontologyProperty().bind(resources.ontologyProperty());
            return phenotypeBrowserController;
        } else if (clz.equals(OntologyTreeBrowserController.class)) {
            return new OntologyTreeBrowserController();
        }

        // individual & family
        else if (clz.equals(IndividualController.class)) {
            return new IndividualController();
        } else if (clz.equals(IndividualVariantSummaryController.class)) {
            return new IndividualVariantSummaryController();
        } else if (clz.equals(PedigreeController.class)) {
            return new PedigreeController(this);
        } else if (clz.equals(FamilyStudyController.class)) {
            return new FamilyStudyController();
        } else if (clz.equals(PedigreeMemberController.class)) {
            return new PedigreeMemberController();
        } else if (clz.equals(VariantSummaryController.class)) {
            return new VariantSummaryController(this);
        }

        // publication & metadata
        else if (clz.equals(PublicationController.class)) {
            return new PublicationController();
        } else if (clz.equals(MetadataController.class)) {
            return new MetadataController();
        } else

        {
            throw new RuntimeException("Unknown controller " + clz);
        }

    }
}
