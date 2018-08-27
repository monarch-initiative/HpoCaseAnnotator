package org.monarchinitiative.hpo_case_annotator.validation;

import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.model.SplicingVariant;
import org.monarchinitiative.hpo_case_annotator.model.Variant;

/**
 * This validator performs test to assess whether enough data has been entered to particular {@link DiseaseCaseModel}
 * instance.
 *
 * {@link DiseaseCaseModel} passes this validation if it contains:
 * <ul>
 *     <li>Publication</li>
 *     <li>Genome build</li>
 *     <li>At least one {@link Variant}</li>
 * </ul>
 * <p>
 *     Each <b>variant</b> is considered complete if it contains:
 * <ul>
 *     <li>Chromosome</li>
 *     <li>Position</li>
 *     <li>Reference allele</li>
 *     <li>Alternate allele</li>
 *     <li>Snippet</li>
 *     <li>Genotype</li>
 *     <li>Variant class</li>
 *     <li>Pathomechanism</li>
 * </ul>
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class CompletenessValidator extends AbstractValidator {

    /**
     * Return true if there is some value (not null or empty String) in at least one field which is mandatory for a
     * variant to be complete.
     *
     * @param variant {@link Variant} to be checked.
     * @return true if at least one from required fields is uninitialized.
     */
    static boolean variantInitialized(Variant variant) {
        if (isNullOrEmpty(variant.getChromosome())
                && isNullOrEmpty(variant.getPosition())
                && isNullOrEmpty(variant.getReferenceAllele())
                && isNullOrEmpty(variant.getAlternateAllele())
                && isNullOrEmpty(variant.getSnippet())
                && isNullOrEmpty(variant.getGenotype())
                && isNullOrEmpty(variant.getVariantClass())
                && isNullOrEmpty(variant.getPathomechanism())) {
            return false; // all required fields of variant are empty
        }
        return true; // all fields are not empty
    }


    /**
     * For a model to be complete it must contain: <ul> <li>Publication</li> <li>Genome build</li> <li>At least one
     * {@link Variant}</li></ul>
     *
     * @param model {@link DiseaseCaseModel} instance about to be validated.
     * @return {@link ValidationResult}
     */
    @Override
    public ValidationResult validateDiseaseCase(DiseaseCaseModel model) {

        // At first validate the requirements of DiseaseCaseModel
        if (modelIsComplete(model) == ValidationResult.FAILED) {
            return ValidationResult.FAILED;
        }

        // Model must contain at least one variant
        if (model.getVariants() == null || model.getVariants().isEmpty()) {
            setErrorMessage("Model must contain at least one variant");
            return ValidationResult.FAILED;
        }

        // Then validate general requirements of Variant class
        if (!model.getVariants().stream().allMatch(this::variantIsComplete)) {
            // Error msg is set downstream
            return ValidationResult.FAILED;
        }

        setErrorMessage(OKAY);
        return ValidationResult.PASSED;
    }


    /**
     * Check Publication, genome build and gene data. Set error message if some data is missing.
     *
     * @return {@link ValidationResult} Passed if everything is set, Failed if something is missing.
     */
    private ValidationResult modelIsComplete(DiseaseCaseModel model) {
        if (model.getPublication() == null) {
            setErrorMessage("Missing publication data");
            return ValidationResult.FAILED;
        }
        if (model.getGenomeBuild() == null) {
            setErrorMessage("Missing genome build");
            return ValidationResult.FAILED;
        }
        return ValidationResult.PASSED;
    }


    /**
     * Return true if every mandatory field of variant contains some information. Check also fields that are specific to
     * particular Variant subclasses.
     *
     * @param variant {@link Variant} to be checked.
     * @return true if variant is complete, false if some info is missing
     */
    private boolean variantIsComplete(Variant variant) {
        // TODO - perform sophisticated validation - e.g. check that value is number if number is required
        // This is mandatory for each variant regardless of type
        if (isNullOrEmpty(variant.getChromosome())
                || isNullOrEmpty(variant.getPosition())
                || isNullOrEmpty(variant.getReferenceAllele())
                || isNullOrEmpty(variant.getAlternateAllele())
                || isNullOrEmpty(variant.getSnippet())
                || isNullOrEmpty(variant.getGenotype())
                || isNullOrEmpty(variant.getVariantClass())
                || isNullOrEmpty(variant.getPathomechanism())
                ) {
            setErrorMessage(String.format("%s:%s%s>%s - %s - At least one mandatory field is empty",
                    variant.getChromosome(), variant.getPosition(),
                    variant.getReferenceAllele(), variant.getAlternateAllele(),
                    variant.getVariantMode().name()));
            return false;
        }

        // Subclass specific validation
        switch (variant.getVariantMode()) {
            case MENDELIAN:
                // MendelianVariant mev = (MendelianVariant) variant;
                // Do specific validation here, nothing yet
                return true;
            case SOMATIC:
                // SomaticVariant sov = (SomaticVariant) variant;
                // Do specific validation here, nothing yet
                return true;
            case SPLICING:
                SplicingVariant spv = (SplicingVariant) variant;

            /* Here we check for completness of all aspects that are different between
             SplicingVariant & Variant. At the moment it is CSS stuff. Entering CSS data
             is not mandatory but if some bit of data has been entered it must be complete. */
                String cp = spv.getCrypticPosition();
                String ct = spv.getCrypticSpliceSiteType();
                String cs = spv.getCrypticSpliceSiteSnippet();
                if (isNullOrEmpty(cp) && isNullOrEmpty(ct) && isNullOrEmpty(cs)) {
                    return true; // nothing was entered, so it's ok
                }

                if (!isNullOrEmpty(cp) && !isNullOrEmpty(ct) && !isNullOrEmpty(cs)) {
                    return true; // nothing is missing
                }
                setErrorMessage("CSS fields are incomplete");
                return false;
            default:
                setErrorMessage(String.format("Unsupported mode '%s'", variant.getVariantMode().name()));
                return false;
        }
    }

}
