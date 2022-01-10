package org.monarchinitiative.hpo_case_annotator.forms.test;

import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyService;
import org.monarchinitiative.svart.GenomicAssembly;
import org.monarchinitiative.svart.GenomicRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record TestGenomicAssemblyService(GenomicAssembly genomicAssembly)
        implements GenomicAssemblyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestGenomicAssemblyService.class);

    @Override
    public String referenceSequence(GenomicRegion region) {
        return "";
    }

    @Override
    public void close() throws Exception {
        LOGGER.info("Closing genomic assembly registry for `{}`", genomicAssembly.name());
    }
}
