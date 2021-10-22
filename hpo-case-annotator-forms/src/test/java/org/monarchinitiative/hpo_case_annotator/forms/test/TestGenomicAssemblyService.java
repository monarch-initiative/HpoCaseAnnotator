package org.monarchinitiative.hpo_case_annotator.forms.test;

import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyService;
import org.monarchinitiative.svart.GenomicAssembly;
import org.monarchinitiative.svart.GenomicRegion;

public record TestGenomicAssemblyService(
        GenomicAssembly genomicAssembly)
        implements GenomicAssemblyService {

    @Override
    public String referenceSequence(GenomicRegion region) {
        return "";
    }

    @Override
    public void close() throws Exception {

    }
}
