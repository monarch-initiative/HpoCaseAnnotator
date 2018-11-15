package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.monarchinitiative.hpo_case_annotator.model.proto.CrypticSpliceSiteType;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;

/**
 * This validator performs test to assess whether enough data has been entered to particular {@link DiseaseCase}
 * instance.
 * <p>
 * {@link DiseaseCase} passes this validation if it contains:
 * <ul>
 * <li>Publication</li>
 * <li>Genome build</li>
 * <li>At least one {@link Variant}</li>
 * </ul>
 * <p>
 * Each <b>variant</b> is considered complete if it contains:
 * <ul>
 * <li>Chromosome</li>
 * <li>Position</li>
 * <li>Reference allele</li>
 * <li>Alternate allele</li>
 * <li>Snippet</li>
 * <li>Genotype</li>
 * <li>Variant class</li>
 * <li>Pathomechanism</li>
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
        return !isNullOrEmpty(variant.getContig())
                || variant.getPos() != 0
                || !isNullOrEmpty(variant.getRefAllele())
                || !isNullOrEmpty(variant.getAltAllele())
                || !isNullOrEmpty(variant.getSnippet())
                || !(variant.getGenotype().equals(Genotype.UNRECOGNIZED) || variant.getGenotype().equals(Genotype.UNDEFINED))
                || !isNullOrEmpty(variant.getVariantClass())
                || !isNullOrEmpty(variant.getPathomechanism());
    }


    /**
     * For a model to be complete it must contain: <ul> <li>Publication</li> <li>Genome build</li> <li>At least one
     * {@link Variant}</li></ul>
     *
     * @param model {@link DiseaseCase} instance about to be validated.
     * @return {@link ValidationResult}
     */
    @Override
    public ValidationResult validateDiseaseCase(DiseaseCase model) {
        ValidationResult result;
        // At first validate the requirements of DiseaseCaseModel
        ValidationResult r = modelIsComplete(model);
        if (r == ValidationResult.FAILED) return r;

        // Model must contain at least one variant
        if (model.getVariantList() == null || model.getVariantList().isEmpty()) {
            return makeValidationResult(ValidationResult.FAILED, "Model must contain at least one variant");
        }

        // Then validate general requirements of Variant class
        return model.getVariantList().stream()
                .map(this::variantIsComplete)
                .filter(vr -> vr != ValidationResult.PASSED)
                .findFirst() // find the first variant that failed validation
                .orElse(makeValidationResult(ValidationResult.PASSED, OKAY)); // or return PASSED if no variant had failed
    }


    /**
     * Check Publication, genome build and gene data. Set error message if some data is missing.
     *
     * @return {@link ValidationResult} Passed if everything is set, Failed if something is missing.
     */
    private ValidationResult modelIsComplete(DiseaseCase model) {
        if (model.getPublication() == null) {
            return makeValidationResult(ValidationResult.FAILED, "Missing publication data");
        }
        if (model.getGenomeBuild() == null) {
            return makeValidationResult(ValidationResult.FAILED, "Missing genome build");
        }
        return makeValidationResult(ValidationResult.PASSED, OKAY);
    }


    /**
     * Return true if every mandatory field of variant contains some information. Check also fields that are specific to
     * particular Variant subclasses.
     *
     * @param variant {@link Variant} to be checked.
     * @return true if variant is complete, false if some info is missing
     */
    private ValidationResult variantIsComplete(Variant variant) {
        // TODO - perform sophisticated validation - e.g. check that value is number if number is required
        // This is mandatory for each variant regardless of type
        if (isNullOrEmpty(variant.getContig())
                || variant.getPos() != 0
                || isNullOrEmpty(variant.getRefAllele())
                || isNullOrEmpty(variant.getAltAllele())
                || isNullOrEmpty(variant.getSnippet())
                || !(variant.getGenotype().equals(Genotype.UNRECOGNIZED) || variant.getGenotype().equals(Genotype.UNDEFINED))
                || isNullOrEmpty(variant.getVariantClass())
                || isNullOrEmpty(variant.getPathomechanism())
                ) {
            return makeValidationResult(ValidationResult.FAILED, String.format("%s:%d%s>%s - %s - At least one mandatory field is empty",
                    variant.getContig(), variant.getPos(),
                    variant.getRefAllele(), variant.getAltAllele(),
                    variant.getVariantValidation().getContext().toString()));
        }

        // Subclass specific validation
        switch (variant.getVariantValidation().getContext()) {
            case MENDELIAN:
                // MendelianVariant mev = (MendelianVariant) variant;
                // Do specific validation here, nothing yet
                return makeValidationResult(ValidationResult.PASSED, OKAY);

            case SOMATIC:
                // SomaticVariant sov = (SomaticVariant) variant;
                // Do specific validation here, nothing yet
                return makeValidationResult(ValidationResult.PASSED, OKAY);

            case SPLICING:
            /* Here we check for completness of all aspects that are different between
             SplicingVariant & Variant. At the moment it is CSS stuff. Entering CSS data
             is not mandatory but if some bit of data has been entered it must be complete. */
                int cp = variant.getCrypticPosition();
                CrypticSpliceSiteType ct = variant.getCrypticSpliceSiteType();
                String cs = variant.getCrypticSpliceSiteSnippet();
                if (cp == 0 && (ct.equals(CrypticSpliceSiteType.UNRECOGNIZED) || ct.equals(CrypticSpliceSiteType.NO)) && isNullOrEmpty(cs)) {
                    return makeValidationResult(ValidationResult.PASSED, OKAY); // nothing was entered, so it's ok
                }

                if (cp == 0 && !(ct.equals(CrypticSpliceSiteType.UNRECOGNIZED) || ct.equals(CrypticSpliceSiteType.NO)) && !isNullOrEmpty(cs)) {
                    return makeValidationResult(ValidationResult.PASSED, OKAY); // nothing is missing
                }
                return makeValidationResult(ValidationResult.FAILED, "CSS fields are incomplete");
            default:
                return makeValidationResult(ValidationResult.FAILED, "Unsupported mode " + variant.getVariantValidation().getContext().toString() + "'");
        }
    }

}
