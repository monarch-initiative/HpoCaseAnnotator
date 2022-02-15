package org.monarchinitiative.hpo_case_annotator.core.reference.genome.obsoleted;


import org.monarchinitiative.hpo_case_annotator.core.reference.genome.GenomicAssemblyService;
import org.monarchinitiative.svart.assembly.GenomicAssembly;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @deprecated in favor of {@link org.monarchinitiative.hpo_case_annotator.core.reference.genome.GenomicAssemblyRegistry}.
 */
@Deprecated
public interface DeprecatedGenomicAssemblyRegistry {

    Stream<GenomicAssemblyService> genomicAssemblyServices();

    default Optional<GenomicAssemblyService> assemblyForName(String assemblyName) {
        return genomicAssemblyServices()
                .filter(service -> {
                    GenomicAssembly assembly = service.genomicAssembly();
                    return assemblyName.equals(assembly.name())
                            || assemblyName.equals(assembly.genBankAccession())
                            || assemblyName.equals(assembly.refSeqAccession());
                })
                .findFirst();
    }

    default Stream<String> assemblyNames() {
        return genomicAssemblyServices()
                .map(service -> service.genomicAssembly().name());
    }

}
