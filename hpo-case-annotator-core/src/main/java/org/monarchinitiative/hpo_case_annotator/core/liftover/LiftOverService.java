package org.monarchinitiative.hpo_case_annotator.core.liftover;

import org.monarchinitiative.svart.assembly.GenomicAssembly;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * An interface for transformation of a position on a contig between genomic assemblies.
 *
 * @since 2.0.0
 */
public interface LiftOverService {

    /**
     * @return set of supported <em>source</em> genomic assemblies
     * @since 2.0.0
     */
    Set<GenomicAssembly> sourceGenomicAssemblies();

    /**
     * Get set of supported <em>target</em> genomic assembly names.
     *
     * @param source genomic assembly name
     * @return set of target genomic assembly names
     * @since 2.0.0
     */
    Set<String> supportedConversions(String source);

    /**
     * Liftover coordinates of the <code>contigPosition</code> from <em>source</em> assembly to <em>target</em> assembly.
     *
     * @param contigPosition position to lift over
     * @param from           name of the source genomic assembly
     * @param to             name of the target genomic assembly
     * @return optional with the lifted region
     * @since 2.0.0
     */
    Optional<ContigPosition> liftOver(ContigPosition contigPosition, String from, String to);

    default List<String> sourceGenomicAssemblyNames() {
        return sourceGenomicAssemblies().stream()
                .map(GenomicAssembly::name)
                .sorted()
                .toList();
    }

    record ContigPosition(String contig, int position) {
    }
}
