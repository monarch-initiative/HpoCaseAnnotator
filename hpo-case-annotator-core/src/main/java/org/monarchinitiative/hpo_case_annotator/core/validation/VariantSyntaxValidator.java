package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * This validator validates syntax only. Use {@link GenomicPositionValidator} if you want to validate that e.g. nucleotide
 * <em>A</em> is located at position 100000 of chromosome <em>1</em> of the reference genome.
 */
public class VariantSyntaxValidator implements Validator<Variant> {

    /**
     * Matches 1-9, 10-19, 20-22, X, Y, and MT.
     */
    private static final String CHROMOSOME_REGEXP = "([1-9])|([1][0-9])|([2][0-2])|(X)|(Y)|(MT)";

    /**
     * Both ref and alt allele must match this regexp.
     */
    private static final String ALLELE_REGEXP = "[ACGTacgt]+";

    private static final String INS_SNIPPET = "[ACGTacgt]+\\[[ACGTacgt]/[ACGTacgt]+][ACGTacgt]+";
    private static final String DEL_SNIPPET = "[ACGTacgt]+\\[[ACGTacgt]/[ACGTacgt]+\\][ACGTacgt]+";

    /**
     * Sequence snippet must match this regexp - strings like <code>'ACGT[A/CC]ACGTT'</code>
     */
    static final String SNIPPET_REGEXP = "[ACGT]+\\[([ACGT]+)/([ACGT]+)][ACGT]+";

    private final VariantValidationDataSyntaxValidator variantValidationDataValidator;

    public VariantSyntaxValidator(VariantValidationDataSyntaxValidator variantValidationDataValidator) {
        this.variantValidationDataValidator = variantValidationDataValidator;
    }

    public VariantSyntaxValidator() {
        this(new VariantValidationDataSyntaxValidator());
    }

    /**
     * Each <b>variant</b> is considered complete if it contains:
     * <ul>
     * <li>Chromosome</li>
     * <li>Position - must not be negative</li>
     * <li>Reference allele</li>
     * <li>Alternate allele</li>
     * <li>Snippet</li>
     * <li>Genotype</li>
     * <li>Variant class</li>
     * <li>Pathomechanism</li>
     * </ul>
     *
     * @param instance
     * @return
     */
    @Override
    public List<ValidationResult> validate(Variant instance) {
        List<ValidationResult> results = new ArrayList<>();

        // check chromosome
        final String contig = instance.getVariantPosition().getContig();
        if (contig.isEmpty()) {
            results.add(ValidationResult.fail("Missing chromosome"));
        } else if (!contig.matches(CHROMOSOME_REGEXP)) {
            results.add(ValidationResult.fail("Invalid chromosome format: " + contig));
        }

        // check position
        final int pos = instance.getVariantPosition().getPos();
        if (pos < 0) {
            results.add(ValidationResult.fail("Position cannot be negative"));
        }

        // check ref allele
        final String refAllele = instance.getVariantPosition().getRefAllele();
        if (refAllele.isEmpty()) {
            results.add(ValidationResult.fail("Missing reference allele"));
        } else if (!refAllele.matches(ALLELE_REGEXP)) {
            results.add(ValidationResult.fail("Invalid reference allele format: " + refAllele));
        }

        // check alt allele
        final String altAllele = instance.getVariantPosition().getAltAllele();
        if (altAllele.isEmpty()) {
            results.add(ValidationResult.fail("Missing alternate allele"));
        } else if (!altAllele.matches(ALLELE_REGEXP)) {
            results.add(ValidationResult.fail("Invalid alternate allele format: " + altAllele));
        }

        // check snippet
        final String snippet = instance.getSnippet();
        if (snippet.isEmpty()) {
            results.add(ValidationResult.fail("Missing snippet"));
        } else if (snippet.matches(SNIPPET_REGEXP)) {
            results.add(ValidationResult.fail("Invalid snippet format: " + snippet));
        }

        final VariantValidation variantValidation = instance.getVariantValidation();
        results.addAll(variantValidationDataValidator.validate(variantValidation));

        return results;
    }
}
