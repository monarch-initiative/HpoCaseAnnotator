package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This validator checks aspects of {@link VariantValidation} POJO.
 */
public class VariantValidationDataSyntaxValidator implements Validator<VariantValidation> {

    @Override
    public List<ValidationResult> validate(VariantValidation instance) {
        List<ValidationResult> results = new ArrayList<>();

        if (VariantValidation.getDefaultInstance().equals(instance)) {
            results.add(ValidationResult.fail("Variant validation is missing"));
        }

        final VariantValidation.Context context = instance.getContext();
        switch (context) {
            case MENDELIAN:
                results.addAll(validateMendelianFields(instance));
                break;
            case SOMATIC:
                results.addAll(validateSomaticFields(instance));
                break;
            case SPLICING:
                results.addAll(validateSplicingFields(instance));
                break;
            case TRANSLOCATION:
            case INTRACHROMOSOMAL:
                results.addAll(validateStructuralFields(instance));
                break;
            default:
                results.add(ValidationResult.fail("Unknown variant validation context " + context));
                break;
        }
        return results;
    }

    /**
     * There are 9 fields for splicing validation represented by check boxes in the validation view. I think at least
     * one should be present, if there is enough evidence for the variant's pathogenicity.
     *
     * @param instance to be checked
     * @return Result
     */
    private Collection<? extends ValidationResult> validateSplicingFields(VariantValidation instance) {
//        List<ValidationResult> results = new ArrayList<>();
//        if (!instance.getMinigeneValidation() && !instance.getSiteDirectedMutagenesisValidation() && !instance.getRtPcrValidation()
//                && !instance.getSrProteinOverexpressionValidation() && !instance.getSrProteinKnockdownValidation() && !instance.getCDnaSequencingValidation()
//                && !instance.getPcrValidation() && !instance.getMutOfWtSpliceSiteValidation() && !instance.getOtherValidation()) {
//                    results.add(ValidationResult.fail("At least one splicing validation type should be checked"));
//                }
//        return results;
        return Collections.emptyList();
    }

    private Collection<? extends ValidationResult> validateSomaticFields(VariantValidation instance) {
        // TODO - validate data in the somatic validation
        return Collections.emptyList();
    }

    private Collection<? extends ValidationResult> validateMendelianFields(VariantValidation instance) {
        // TODO - validate data in the mendelian validation
        return Collections.emptyList();
    }

    private Collection<? extends ValidationResult> validateStructuralFields(VariantValidation instance) {
        return Collections.emptyList();
    }
}
