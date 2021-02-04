package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantPosition;

import java.util.ArrayList;
import java.util.List;

public class BreakendVariantValidator implements Validator<Variant> {


    @Override
    public List<ValidationResult> validate(Variant variant) {
        List<ValidationResult> results = new ArrayList<>();

        VariantPosition vp = variant.getVariantPosition();
        // genome assembly
        if (vp.getGenomeAssembly().equals(GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY)
                || vp.getGenomeAssembly().equals(GenomeAssembly.UNRECOGNIZED)) {
            results.add(ValidationResult.fail("Missing genome assembly"));
        }

        // chromosome
        if (vp.getContig().isEmpty()) {
            results.add(ValidationResult.fail("Left contig/chromosome is missing"));
        }

        // chromosome 2
        if (vp.getContig2().isEmpty()) {
            results.add(ValidationResult.fail("Right contig/chromosome is missing"));
        }

        if (vp.getContig().equals(vp.getContig2())) {
            results.add(ValidationResult.fail("Translocation contigs should not be the same"));
        }

        // left
        if (vp.getPos() <= 0) {
            results.add(ValidationResult.fail("Begin position cannot be non-positive"));
        }

        // right
        if (vp.getPos2() <= 0) {
            results.add(ValidationResult.fail("End position cannot be non-positive"));
        }

        return results;
    }
}
