package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.monarchinitiative.hpo_case_annotator.model.proto.*;

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
 * <li>Gene</li>
 * <li>Disease</li>
 * <li>At least one {@link Variant}</li>
 * </ul>
 * <p>
 * Note that the variant itself is validated using {@link VariantSyntaxValidator}.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class CompletenessValidator implements Validator<DiseaseCase> {

    private final VariantSyntaxValidator variantValidator;

    CompletenessValidator() {
        this(new VariantSyntaxValidator());
    }

    /**
     * @param variantValidator {@link VariantSyntaxValidator} to use for variant validation, or <code>null</code>, if the
     *                         variant validation should be skipped
     */
    CompletenessValidator(VariantSyntaxValidator variantValidator) {
        this.variantValidator = variantValidator;
    }

    /**
     * For a model to be complete it must contain:
     * <ul>
     * <li>Publication</li>
     * <li>Gene</li>
     * <li>Disease</li>
     * <li>At least one valid {@link Variant} is present</li>
     * </ul>
     *
     * @param diseaseCase {@link DiseaseCase} about to be validated.
     * @return {@link List} with {@link ValidationResult}
     */
    @Override
    public List<ValidationResult> validate(DiseaseCase diseaseCase) {
        List<ValidationResult> results = new ArrayList<>();

        // check publication
        final Publication publication = diseaseCase.getPublication();
        if (Publication.getDefaultInstance().equals(publication)) {
            results.add(ValidationResult.fail("Publication data is not set"));
        }

        // check gene
        final Gene gene = diseaseCase.getGene();
        if (Gene.getDefaultInstance().equals(gene)) {
            results.add(ValidationResult.fail("Gene data is not complete"));
        } else {
            if (gene.getEntrezId() <= 0) {
                results.add(ValidationResult.fail("Entrez ID is smaller than 0"));
            }
        }

        // variants
        final List<Variant> variantList = diseaseCase.getVariantList();
        if (!variantList.isEmpty()) {
            if (variantValidator != null) {
                // validate all the variants
                variantList.stream()
                        .map(variantValidator::validate)
                        .flatMap(Collection::stream)
                        .forEach(results::add);
            }
        } else {
            results.add(ValidationResult.fail("At least one variant should be present"));
        }

        // check disease
        final Disease disease = diseaseCase.getDisease();
        if (Disease.getDefaultInstance().equals(disease)) {
            results.add(ValidationResult.fail("Disease data is not complete"));
        }

        // check family info
        final FamilyInfo familyInfo = diseaseCase.getFamilyInfo();
        if (FamilyInfo.getDefaultInstance().equals(familyInfo)) {
            results.add(ValidationResult.fail("Family info data is not complete"));
        }

        return results;
    }
}
