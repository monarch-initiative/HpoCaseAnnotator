package org.monarchinitiative.hpo_case_annotator.forms.test;

import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyService;
import org.monarchinitiative.hpo_case_annotator.core.reference.StrandedSequence;
import org.monarchinitiative.svart.GenomicAssembly;
import org.monarchinitiative.svart.GenomicRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public record TestGenomicAssemblyService(GenomicAssembly genomicAssembly)
        implements GenomicAssemblyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestGenomicAssemblyService.class);

    @Override
    public Optional<StrandedSequence> referenceSequence(GenomicRegion region) {
        return Optional.empty();
    }

    @Override
    public void close() throws Exception {
        LOGGER.info("Closing genomic assembly registry for `{}`", genomicAssembly.name());
    }
}
