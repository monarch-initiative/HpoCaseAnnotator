package org.monarchinitiative.hpo_case_annotator.core.reference;

import org.monarchinitiative.svart.GenomicAssembly;
import org.monarchinitiative.svart.GenomicRegion;

public interface GenomicAssemblyService extends AutoCloseable {

    GenomicAssembly genomicAssembly();

    /**
     * Get reference sequence for given <code>region</code>
     * @param region query
     * @return reference sequence
     */
    String referenceSequence(GenomicRegion region);
}
