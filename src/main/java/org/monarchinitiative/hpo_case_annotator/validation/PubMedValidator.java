package org.monarchinitiative.hpo_case_annotator.validation;

import org.monarchinitiative.hpo_case_annotator.io.ModelParser;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This validator doesn't validate {@link DiseaseCaseModel} directly because there is no PubMed attribute present in
 * the model. The purpose of this validator is to check if a publication with given pmid has been already curated
 * within this project and if there exists a corresponding model file in project directory.
 */
public final class PubMedValidator extends AbstractValidator {


    private final ModelParser modelParser;

    @Inject
    public PubMedValidator(ModelParser modelParser) {
        this.modelParser = modelParser;
    }


    /**
     * This validator always returns {@link ValidationResult#PASSED} as described in class description.
     *
     * @param model {@link DiseaseCaseModel} instance about to be validated.
     * @return {@link ValidationResult#PASSED}.
     */
    @Override
    public ValidationResult validateDiseaseCase(DiseaseCaseModel model) {
        setErrorMessage(OKAY);
        return ValidationResult.PASSED;
    }


    /**
     * Read all model files in project directory and check if there exists at least one model file that has given pmid.
     *
     * @return true if there is a file in project directory that has the same pmid as provided.
     */
    public boolean seenThisPMIDBefore(String pmid) {
        Collection<String> modelNames = modelParser.getModelNames();
        Set<DiseaseCaseModel> models = modelNames.stream()
                .map(modelParser::readModel)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        return !models.isEmpty() && models.stream().anyMatch(model -> pmid.equals(model.getPublication().getPmid()));
    }

}
