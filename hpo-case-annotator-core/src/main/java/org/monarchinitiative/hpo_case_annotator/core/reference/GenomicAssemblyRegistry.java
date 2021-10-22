package org.monarchinitiative.hpo_case_annotator.core.reference;


import org.monarchinitiative.svart.GenomicAssembly;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface GenomicAssemblyRegistry {

    Set<GenomicAssemblyService> genomicAssemblyServices();

    default Optional<GenomicAssemblyService> assemblyForName(String assemblyName) {
        return genomicAssemblyServices().stream()
                .filter(service -> {
                    GenomicAssembly assembly = service.genomicAssembly();
                    return assemblyName.equals(assembly.name())
                            || assemblyName.equals(assembly.genBankAccession())
                            || assemblyName.equals(assembly.refSeqAccession());
                })
                .findFirst();
    }

    default Set<String> assemblyNames() {
        return genomicAssemblyServices().stream()
                .map(service -> service.genomicAssembly().name())
                .collect(Collectors.toUnmodifiableSet());
    }

}
