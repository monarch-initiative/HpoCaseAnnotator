package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
public final class CompletenessValidator implements Validator<DiseaseCase> {

    private final VariantSyntaxValidator variantValidator;

    public CompletenessValidator() {
        this(new VariantSyntaxValidator());
    }

    public CompletenessValidator(VariantSyntaxValidator variantValidator) {
        this.variantValidator = variantValidator;
    }

    /**
     * For a model to be complete it must contain:
     * <ul>
     * <li>Publication</li>
     * <li>At least one valid {@link Variant} is present</li>
     * </ul>
     *
     * @param diseaseCase {@link DiseaseCase} about to be validated.
     * @return {@link List} with {@link ValidationResult}
     */
    @Override
    public List<ValidationResult> validate(DiseaseCase diseaseCase) {
        // TODO - more thorough validation
        List<ValidationResult> results = new ArrayList<>();

        // check publication
        final Publication publication = diseaseCase.getPublication();
        if (Publication.getDefaultInstance().equals(publication)) {
            results.add(ValidationResult.fail("Publication data is not set"));
        }

        // validate all the variants
        final List<Variant> variantList = diseaseCase.getVariantList();
        if (!variantList.isEmpty()) {
            variantList.stream()
                    .map(variantValidator::validate)
                    .flatMap(Collection::stream)
                    .forEach(results::add);
        } else {
            results.add(ValidationResult.fail("At least one variant should be present"));
        }

        return results;
    }
}
