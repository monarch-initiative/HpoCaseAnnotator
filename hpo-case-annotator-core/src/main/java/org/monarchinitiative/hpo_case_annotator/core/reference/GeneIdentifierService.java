package org.monarchinitiative.hpo_case_annotator.core.reference;

import java.util.List;
import java.util.Set;

public interface GeneIdentifierService {

    Set<GeneIdentifier> genes();

    List<String> transcriptIdsForGene(GeneIdentifier geneIdentifier);

}
