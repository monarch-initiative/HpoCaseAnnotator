package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantPosition;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CnvVariantValidator implements Validator<Variant> {

    @Override
    public List<ValidationResult> validate(Variant variant) {
        List<ValidationResult> results = new ArrayList<>();

        VariantPosition vp = variant.getVariantPosition();
        int begin = vp.getPos() - 1; // convert to 0-based
        int end = vp.getPos2();
        // genome assembly
        if (vp.getGenomeAssembly() == null
                || vp.getGenomeAssembly().equals(GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY)
                || vp.getGenomeAssembly().equals(GenomeAssembly.UNRECOGNIZED)) {
            results.add(ValidationResult.fail("Missing genome assembly"));
        }

        // chromosome
        if (vp.getContig() == null || vp.getContig().isEmpty()) {
            results.add(ValidationResult.fail("Contig/chromosome is missing"));
        }

        // begin
        if (begin < 0) {
            results.add(ValidationResult.fail("Begin position cannot be negative"));
        }

        // end
        if (end < 0) {
            results.add(ValidationResult.fail("End position cannot be negative"));
        }

        if (begin > end) {
            results.add(ValidationResult.fail("End position is before begin"));
        }

        if (variant.getCnvPloidy() < 0) {
            results.add(ValidationResult.fail("Ploidy cannot be negative"));
        }

        if (variant.getCnvPloidy() == 2) {
            results.add(ValidationResult.warn("CNV with ploidy 2 is not a CNV"));
        }

        return results;
    }
}
