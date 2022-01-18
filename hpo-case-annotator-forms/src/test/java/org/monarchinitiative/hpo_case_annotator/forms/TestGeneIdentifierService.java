package org.monarchinitiative.hpo_case_annotator.forms;

import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifier;
import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifierService;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestGeneIdentifierService implements GeneIdentifierService {

    private final Map<GeneIdentifier, List<String>> genes;

    public TestGeneIdentifierService(Map<GeneIdentifier, List<String>> genes) {
        this.genes = genes;
    }

    @Override
    public Set<GeneIdentifier> genes() {
        return genes.keySet();
    }

    @Override
    public List<String> transcriptIdsForGene(GeneIdentifier geneIdentifier) {
        return genes.getOrDefault(geneIdentifier, List.of());
    }
}
