package org.monarchinitiative.hpo_case_annotator.validation;

import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class performs validation of {@link DiseaseCaseModel} instances.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.2
 * @since 0.0
 */
public final class ValidationRunner {


    private final GenomicPositionValidator genomicPositionValidator;

    private final CompletenessValidator completenessValidator;

    private final PubMedValidator pubMedValidator;

    @Inject
    public ValidationRunner(GenomicPositionValidator genomicPositionValidator,
                            CompletenessValidator completenessValidator, PubMedValidator pubMedValidator) {
        this.genomicPositionValidator = genomicPositionValidator;
        this.completenessValidator = completenessValidator;
        this.pubMedValidator = pubMedValidator;
    }


    /**
     * Run validation on given collection of models and return results as a list of validation lines.
     *
     * @param models {@link Collection} of {@link DiseaseCaseModel} instances.
     * @return {@link List} of {@link ValidationLine}s.
     */
    public List<ValidationLine> validateModels(Collection<DiseaseCaseModel> models) {
        return models.stream()
                .map(this::singleValidation)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }


    /**
     * Validate single model instance with validators.
     *
     * @param model {@link DiseaseCaseModel} instance to be validated.
     * @return {@link List} of {@link ValidationLine}s created by validators.
     */
    private List<ValidationLine> singleValidation(DiseaseCaseModel model) {
        List<ValidationLine> valList = new ArrayList<>();
        String modelName = model.getFileName();

        // Completeness validator
        ValidationResult completenessResult = completenessValidator.validateDiseaseCase(model);
        String errorMessage = completenessValidator.getErrorMessage();

        valList.add(new ValidationLine(modelName,
                completenessValidator.getClass().getSimpleName(),
                completenessResult.toString(),
                errorMessage));
        if (completenessResult.equals(ValidationResult.FAILED))
            return valList; // if model is incomplete, further validation is a waste of time

        // Genomic position validator
        valList.add(new ValidationLine(modelName,
                genomicPositionValidator.getClass().getSimpleName(),
                genomicPositionValidator.validateDiseaseCase(model).toString(),
                genomicPositionValidator.getErrorMessage()));

        return valList;
    }


    public boolean seenThisPMIDBefore(String pmid) {
        return pubMedValidator.seenThisPMIDBefore(pmid);
    }
}
