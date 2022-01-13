package org.monarchinitiative.hpo_case_annotator.core.reference;

import org.monarchinitiative.svart.GenomicAssembly;
import org.monarchinitiative.svart.GenomicRegion;

import java.nio.file.Path;
import java.util.Optional;

public interface GenomicAssemblyService extends AutoCloseable {

    static GenomicAssemblyService of(Path assemblyReportPath, Path fastaPath, Path fastaFai, Path fastaDict) throws InvalidFastaFileException {
        return GenomicAssemblyServiceDefault.of(assemblyReportPath, fastaPath, fastaFai, fastaDict);
    }

    GenomicAssembly genomicAssembly();

    /**
     * Get reference sequence for given <code>region</code>
     * @param region query
     * @return reference sequence
     */
    Optional<StrandedSequence> referenceSequence(GenomicRegion region);
}
