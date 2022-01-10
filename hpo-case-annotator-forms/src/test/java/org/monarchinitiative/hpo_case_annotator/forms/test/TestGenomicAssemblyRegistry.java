package org.monarchinitiative.hpo_case_annotator.forms.test;

import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyService;
import org.monarchinitiative.svart.GenomicAssemblies;

import java.util.Set;
import java.util.stream.Stream;

public class TestGenomicAssemblyRegistry implements GenomicAssemblyRegistry {

    private final Set<GenomicAssemblyService> services = Set.of(
            new TestGenomicAssemblyService(GenomicAssemblies.GRCh37p13()),
            new TestGenomicAssemblyService(GenomicAssemblies.GRCh38p13()));

    @Override
    public Stream<GenomicAssemblyService> genomicAssemblyServices() {
        return services.stream();
    }

}
