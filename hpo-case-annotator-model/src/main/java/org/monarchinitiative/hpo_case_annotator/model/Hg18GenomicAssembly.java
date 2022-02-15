package org.monarchinitiative.hpo_case_annotator.model;

import org.monarchinitiative.svart.assembly.GenomicAssembly;

import java.util.Objects;

public class Hg18GenomicAssembly {
    private static volatile GenomicAssembly HG19_GENOMIC_ASSEMBLY = null;
    private Hg18GenomicAssembly() {
    }

    public static GenomicAssembly hg18GenomicAssembly() {
        if (HG19_GENOMIC_ASSEMBLY == null) {
            synchronized (Hg18GenomicAssembly.class) {
                if (HG19_GENOMIC_ASSEMBLY == null) {
                    HG19_GENOMIC_ASSEMBLY = GenomicAssembly.readAssembly(Objects.requireNonNull(Hg18GenomicAssembly.class.getResourceAsStream("GCF_000001405.12_NCBI36_assembly_report.txt"), "Missing genome hg18 assembly report file. Contact developers"));
                }
            }
        }
        return HG19_GENOMIC_ASSEMBLY;
    }
}
