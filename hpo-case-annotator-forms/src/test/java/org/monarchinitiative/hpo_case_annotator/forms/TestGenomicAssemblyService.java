package org.monarchinitiative.hpo_case_annotator.forms;

import org.monarchinitiative.hpo_case_annotator.core.reference.genome.GenomicAssemblyService;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.StrandedSequence;
import org.monarchinitiative.svart.GenomicRegion;
import org.monarchinitiative.svart.assembly.GenomicAssembly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public record TestGenomicAssemblyService(GenomicAssembly genomicAssembly)
        implements GenomicAssemblyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestGenomicAssemblyService.class);

    @Override
    public Optional<StrandedSequence> referenceSequence(GenomicRegion query) {
        return Optional.empty();
    }

    @Override
    public void close() throws Exception {
        LOGGER.info("Closing genomic assembly registry for `{}`", genomicAssembly.name());
    }
}
