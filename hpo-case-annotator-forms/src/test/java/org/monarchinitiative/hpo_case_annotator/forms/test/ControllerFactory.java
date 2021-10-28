package org.monarchinitiative.hpo_case_annotator.forms.test;

import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifierService;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.CuratedVariantController;
import org.monarchinitiative.hpo_case_annotator.forms.PhenotypeController;
import org.monarchinitiative.hpo_case_annotator.forms.StatusBarController;
import org.monarchinitiative.hpo_case_annotator.forms.OntologyTreeController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.*;
import org.monarchinitiative.hpo_case_annotator.forms.variant.*;
import org.monarchinitiative.phenol.ontology.data.Ontology;

public class ControllerFactory {

    private final GenomicAssemblyRegistry genomicAssemblyRegistry;
    private final GeneIdentifierService geneIdentifierService;
    private final Ontology ontology;

    public ControllerFactory(GenomicAssemblyRegistry genomicAssemblyRegistry, GeneIdentifierService geneIdentifierService, Ontology ontology) {
        this.genomicAssemblyRegistry = genomicAssemblyRegistry;
        this.geneIdentifierService = geneIdentifierService;
        this.ontology = ontology;
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
            return new PhenotypeController();
        } else if (clz.equals(PhenotypicFeatureController.class)) {
            return new PhenotypicFeatureController();
        } else if (clz.equals(PhenotypicFeaturesTableController.class)) {
            return new PhenotypicFeaturesTableController();
        } else if (clz.equals(TextMiningController.class)) {
            return new TextMiningController();
        } else if (clz.equals(PhenotypeEntryController.class)) {
            return new PhenotypeEntryController();
        } else if (clz.equals(OntologyTreeController.class)) {
            return new OntologyTreeController(ontology);
        } else {
            throw new RuntimeException("Unknown controller " + clz);
        }

    }
}
