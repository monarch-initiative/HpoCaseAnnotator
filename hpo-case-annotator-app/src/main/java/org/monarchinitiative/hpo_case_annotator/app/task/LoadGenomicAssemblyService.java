package org.monarchinitiative.hpo_case_annotator.app.task;

import javafx.concurrent.Task;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResource;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.GenomicAssemblyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadGenomicAssemblyService extends Task<GenomicAssemblyService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadGenomicAssemblyService.class);

    private final GenomicLocalResource resource;

    public LoadGenomicAssemblyService(GenomicLocalResource resource) {
        this.resource = resource;
    }

    @Override
    protected GenomicAssemblyService call() throws Exception {
        LOGGER.debug("Creating GenomicAssemblyService from FASTA: {}, assembly report: {}",
                resource.getFasta().toAbsolutePath(), resource.getAssemblyReport().toAbsolutePath());
        return GenomicAssemblyService.of(resource.getAssemblyReport(), resource.getFasta(), resource.getFastaFai(), resource.getFastaDict());
    }
}
