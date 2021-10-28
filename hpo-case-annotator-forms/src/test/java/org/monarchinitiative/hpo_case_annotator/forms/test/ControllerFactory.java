package org.monarchinitiative.hpo_case_annotator.forms.test;

import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifierService;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.*;
import org.monarchinitiative.hpo_case_annotator.forms.ontotree.OntologyTreeBrowserController;
import org.monarchinitiative.hpo_case_annotator.forms.ontotree.SelectableOntologyTreeController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypeEntryController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypicFeatureController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypicFeaturesTableController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.TextMiningController;
import org.monarchinitiative.hpo_case_annotator.forms.variant.*;

public class ControllerFactory {

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
        if (clz.equals(CuratedVariantController.class)) {
            return new CuratedVariantController();
        } else if (clz.equals(HgvsVariantController.class)) {
            return new HgvsVariantController(geneIdentifierService);
        } else if (clz.equals(StatusBarController.class)) {
            return new StatusBarController();
        } else if (clz.equals(VcfSequenceVariantController.class)) {
            return new VcfSequenceVariantController(genomicAssemblyRegistry);
        } else if (clz.equals(VcfSymbolicVariantController.class)) {
            return new VcfSymbolicVariantController(genomicAssemblyRegistry);
        } else if (clz.equals(VcfBreakendVariantController.class)) {
            return new VcfBreakendVariantController(genomicAssemblyRegistry);
        } else if (clz.equals(BreakendController.class)) {
            return new BreakendController();
        }
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
        } else {
            throw new RuntimeException("Unknown controller " + clz);
        }

    }
}
