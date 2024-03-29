package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
    public static final Pattern CHROMOSOME_REGEXP = Pattern.compile("([1-9])|([1][0-9])|([2][0-2])|(X)|(Y)|(MT)");

    /**
     * Variant position data must match this regexp - any positive integer.
     */
    public static final Pattern POSITIVE_INTEGER_REGEXP = Pattern.compile("^[^-]?[1-9][0-9]*$");


    public static final Pattern NON_NEGATIVE_INTEGER_REGEXP = Pattern.compile("^[1-9][0-9]*$");

    /**
     * Matches negative integers, positive integers and <code>0</code>. Does not match <code>-0</code>
     */
    public static final Pattern INTEGER_REGEXP = Pattern.compile("0|(-?[1-9]\\d*)");

    /**
     * Alleles (REF, ALT) must match this regexp - only <code>A,C,G,T,a,c,g,t</code> nucleotides are permitted.
     */
    public static final Pattern NONEMPTY_ALLELE_REGEXP = Pattern.compile("[ACGTNacgtn]+");

    public static final Pattern POSSIBLY_EMPTY_ALLELE_REGEXP = Pattern.compile("[ACGTNacgtn]*");

    /**
     * Alt allele of structural variant must match this regexp
     */
    public static final Pattern STRUCTURAL_ALT_ALLELE_REGEXP = Pattern.compile("[A-Z]+");

    /**
     * Sequence snippet must match this regexp - strings like <code>'ACGT[A/CC]ACGTT'</code>, or <code>''AcgT[A/Cc]ACgtT''</code>
     * (both upper and lowercase are allowed.
     */
    public static final Pattern SNIPPET_REGEXP = Pattern.compile("[ACGTacgt]+\\[[ACGTacgt]+/[ACGTacgt]+][ACGTacgt]+");


    /**
     * Sequence snippet for cryptic splice site boundary must match this regexp - string like <code>'AccAC|CaccaT'</code>
     */
    public static final Pattern CSS_SNIPPET_REGEXP = Pattern.compile("[ACGTacgt]+\\|[ACGTacgt]+");

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
     */
    @Override
    public List<ValidationResult> validate(Variant variant) {
        List<ValidationResult> results = new ArrayList<>();

        // check genome assembly
        GenomeAssembly genomeAssembly = variant.getVariantPosition().getGenomeAssembly();

        if (genomeAssembly.equals(GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY)) {
            results.add(ValidationResult.fail("Unknown genome assembly"));
        }

        // check chromosome
        String contig = variant.getVariantPosition().getContig();
        if (contig.isEmpty()) {
            results.add(ValidationResult.fail("Missing chromosome"));
        } else {
            if (!CHROMOSOME_REGEXP.matcher(contig).matches())
                results.add(ValidationResult.fail("Invalid chromosome format: " + contig));
        }

        // check position
        int pos = variant.getVariantPosition().getPos();
        if (pos <= 0) {
            results.add(ValidationResult.fail("Position must be positive: " + pos));
        }

        // check ref allele
        String refAllele = variant.getVariantPosition().getRefAllele();
        if (refAllele.isEmpty()) {
            results.add(ValidationResult.fail("Missing reference allele"));
        } else {
            if (!NONEMPTY_ALLELE_REGEXP.matcher(refAllele).matches()) {
                results.add(ValidationResult.fail("Invalid reference allele format: " + refAllele));
            }
        }

        // check alt allele
        String altAllele = variant.getVariantPosition().getAltAllele();
        if (variant.getVariantClass().equals("structural")) {
            VariantValidation vv = variant.getVariantValidation();
            if (vv.getContext() == VariantValidation.Context.TRANSLOCATION) {
                if (!POSSIBLY_EMPTY_ALLELE_REGEXP.matcher(altAllele).matches())
                    results.add(ValidationResult.fail("Invalid breakend inserted sequence format: " + altAllele));
            } else if (vv.getContext() == VariantValidation.Context.INTRACHROMOSOMAL) {
                if (!STRUCTURAL_ALT_ALLELE_REGEXP.matcher(altAllele).matches())
                    results.add(ValidationResult.fail("Invalid alternate allele format: " + altAllele));
            } else {
                results.add(ValidationResult.fail("Structural variant does not have translocation or intrachromosomal variant validation context: " + altAllele));
            }
        } else {
            if (altAllele.isEmpty()) {
                results.add(ValidationResult.fail("Missing alternate allele"));
            } else {
                if (!NONEMPTY_ALLELE_REGEXP.matcher(altAllele).matches())
                    results.add(ValidationResult.fail("Invalid alternate allele format: " + altAllele));
            }
        }

        // check snippet if the variant class is not structural
        if (!variant.getVariantClass().equals("structural")) {
            String snippet = variant.getSnippet();
            if (snippet.isEmpty()) {
                results.add(ValidationResult.fail("Missing snippet"));
            } else {
                if (!SNIPPET_REGEXP.matcher(snippet).matches())
                    results.add(ValidationResult.fail("Invalid snippet format: " + snippet));
            }
        }

        // check genotype
        Genotype genotype = variant.getGenotype();
        if (genotype.equals(Genotype.UNDEFINED)) {
            results.add(ValidationResult.fail("Undefined genotype"));
        }

        final VariantValidation variantValidation = variant.getVariantValidation();
        results.addAll(variantValidationDataValidator.validate(variantValidation));

        return results;
    }
}
