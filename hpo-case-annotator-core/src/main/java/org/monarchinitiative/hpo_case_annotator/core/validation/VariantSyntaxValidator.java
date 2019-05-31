package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * This validator checks that the data has been entered in correct format (e.g. snippet contains upstream, downstream
 * nucleotides, there are two brackets and one forward slash present, etc.)
 * <p>
 * Each <b>variant</b> is considered complete if it contains information regarding:
 * <ul>
 * <li>Chromosome - allowed 1-9, 10-19, 20-22, X, Y, and MT</li>
 * <li>Position - positive integer</li>
 * <li>Reference allele</li>
 * <li>Alternate allele</li>
 * <li>Snippet</li>
 * <li>Genotype</li>
 * </ul>
 * <p>
 * This validator validates syntax only. Use {@link GenomicPositionValidator} if you want to validate that e.g. nucleotide
 * <em>A</em> is located at position 100000 of chromosome <em>1</em> of the reference genome.
 */
public class VariantSyntaxValidator implements Validator<Variant> {

    /**
     * Matches 1-9, 10-19, 20-22, X, Y, and MT.
     */
    public static final String CHROMOSOME_REGEXP = "([1-9])|([1][0-9])|([2][0-2])|(X)|(Y)|(MT)";

    /**
     * Variant position data must match this regexp - any positive integer.
     */
    public static final String POSITIVE_INTEGER_REGEXP = "^[^-]?[1-9][0-9]*$";


    public static final String NON_NEGATIVE_INTEGER_REGEXP = "^[1-9][0-9]*$";

    /**
     * Alleles (REF, ALT) must match this regexp - only <code>A,C,G,T,a,c,g,t</code> nucleotides are permitted.
     */
    public static final String ALLELE_REGEXP = "[ACGTacgt]+";

    /**
     * Sequence snippet must match this regexp - strings like <code>'ACGT[A/CC]ACGTT'</code>, or <code>''AcgT[A/Cc]ACgtT''</code>
     * (both upper and lowercase are allowed.
     */
    public static final String SNIPPET_REGEXP = "[ACGTacgt]+\\[[ACGTacgt]+/[ACGTacgt]+][ACGTacgt]+";


    /**
     * Sequence snippet for cryptic splice site boundary must match this regexp - string like <code>'AccAC|CaccaT'</code>
     */
    public static final String CSS_SNIPPET_REGEXP = "[ACGTacgt]+\\|[ACGTacgt]+";

    private final VariantValidationDataSyntaxValidator variantValidationDataValidator;

    VariantSyntaxValidator(VariantValidationDataSyntaxValidator variantValidationDataValidator) {
        this.variantValidationDataValidator = variantValidationDataValidator;
    }

    VariantSyntaxValidator() {
        this(new VariantValidationDataSyntaxValidator());
    }

    /**
     * Each <b>variant</b> is considered complete if it contains:
     * <ul>
     *     <li>Genome assembly</li>
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

        // check genome assembly
        final GenomeAssembly genomeAssembly = instance.getVariantPosition().getGenomeAssembly();

        if (genomeAssembly.equals(GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY)) {
            results.add(ValidationResult.fail("Unknown genome assembly"));
        }

        // check chromosome
        final String contig = instance.getVariantPosition().getContig();
        if (contig.isEmpty()) {
            results.add(ValidationResult.fail("Missing chromosome"));
        } else if (!contig.matches(CHROMOSOME_REGEXP)) {
            results.add(ValidationResult.fail("Invalid chromosome format: " + contig));
        }

        // check position
        final int pos = instance.getVariantPosition().getPos();
        if (pos <= 0) {
            results.add(ValidationResult.fail("Position must be positive: " + pos));
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
        } else if (!snippet.matches(SNIPPET_REGEXP)) {
            results.add(ValidationResult.fail("Invalid snippet format: " + snippet));
        }

        // check genotype
        final Genotype genotype = instance.getGenotype();
        if (genotype.equals(Genotype.UNDEFINED)) {
            results.add(ValidationResult.fail("Undefined genotype"));
        }

        final VariantValidation variantValidation = instance.getVariantValidation();
        results.addAll(variantValidationDataValidator.validate(variantValidation));

        return results;
    }
}
