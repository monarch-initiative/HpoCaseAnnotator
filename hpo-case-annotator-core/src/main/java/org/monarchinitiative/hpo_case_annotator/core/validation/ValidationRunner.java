package org.monarchinitiative.hpo_case_annotator.core.validation;


import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.ModelUtils;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssemblies;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.SequenceDao;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.SingleFastaSequenceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class performs validation of {@link DiseaseCase} instances.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.2
 * @since 0.0
 */
public final class ValidationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationRunner.class);

    private final GenomicPositionValidator genomicPositionValidator;

    private final CompletenessValidator completenessValidator;



    public ValidationRunner(GenomicPositionValidator genomicPositionValidator,
                            CompletenessValidator completenessValidator) {
        this.genomicPositionValidator = genomicPositionValidator;
        this.completenessValidator = completenessValidator;
    }


    /**
     * Run validation on given collection of models and return results as a list of validation lines.
     *
     * @param models {@link Collection} of {@link DiseaseCase} instances.
     * @return {@link List} of {@link ValidationLine}s.
     */
    public List<ValidationLine> validateModels(Collection<DiseaseCase> models) {
        return models.stream()
                .map(this::singleValidation)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }


    /**
     * Validate single model instance with validators.
     *
     * @param model {@link DiseaseCase} instance to be validated.
     * @return {@link List} of {@link ValidationLine}s created by validators.
     */
    private List<ValidationLine> singleValidation(DiseaseCase model) {
        List<ValidationLine> valList = new ArrayList<>();
        String modelName = ModelUtils.getNameFor(model);

        // Completeness validator
        ValidationResult completenessResult = completenessValidator.validateDiseaseCase(model);

        valList.add(new ValidationLine(modelName,
                completenessValidator.getClass().getSimpleName(),
                completenessResult));
        if (completenessResult.equals(ValidationResult.FAILED))
            return valList; // further validation is a waste of time if the model is incomplete

        // Genomic position validator
        valList.add(new ValidationLine(modelName,
                genomicPositionValidator.getClass().getSimpleName(),
                genomicPositionValidator.validateDiseaseCase(model)));

        return valList;
    }

    public static Collection<ValidationLine> validateModel(DiseaseCase model, GenomeAssemblies assemblies) {
        Set<ValidationLine> lines = new HashSet<>();

        // validate completness
        CompletenessValidator completenessValidator = new CompletenessValidator();
        ValidationResult result = completenessValidator.validateDiseaseCase(model);
        lines.add(new ValidationLine(ModelUtils.getNameFor(model), completenessValidator.getClass().getSimpleName(), result));
        if (result != ValidationResult.PASSED)
            return lines; // further validation is a waste of time, since the model is incomplete

        // figure out which genome build is used in the current model
        GenomeAssembly assembly;
        switch (model.getGenomeBuild().toLowerCase()) {
            case "grch37":
            case "hg19":
                assembly = assemblies.getAssemblyMap().get(GenomeAssembly.HG19.toString());
                break;
            case "grch38":
            case "hg38":
                assembly = assemblies.getAssemblyMap().get(GenomeAssembly.HG38.toString());
                break;
            default:
                LOGGER.warn("Unknown genome build '{}' in model '{}'", model.getGenomeBuild(), ModelUtils.getNameFor(model));
                lines.add(new ValidationLine(ModelUtils.getNameFor(model), "GenomeBuildValidator",
                        AbstractValidator.makeValidationResult(ValidationResult.FAILED, "Unknown genome build '" + model.getGenomeBuild() + "'")));
                return lines;
        }

        // validate genomic coordinates
        File fastaFile = assembly.getFastaPath();
        if (fastaFile == null || !fastaFile.isFile()) {
            lines.add(new ValidationLine(ModelUtils.getNameFor(model), GenomicPositionValidator.class.getSimpleName(),
                    AbstractValidator.makeValidationResult(ValidationResult.UNAPPLICABLE, "Genome build '" + assembly.toString() + "' not available. Download in Set resources dialog")));
        } else {
            try (SequenceDao sequenceDao = new SingleFastaSequenceDao(assembly.getFastaPath())) {
                GenomicPositionValidator genomicPositionValidator = new GenomicPositionValidator(sequenceDao);
                lines.add(new ValidationLine(ModelUtils.getNameFor(model), GenomicPositionValidator.class.getSimpleName(),
                        genomicPositionValidator.validateDiseaseCase(model)));
            } catch (Exception e) {
                LOGGER.warn("Error occured during validation of genomic coordinates of the model '{}' using assembly '{}'", ModelUtils.getNameFor(model), assembly.toString());
                lines.add(new ValidationLine(ModelUtils.getNameFor(model), GenomicPositionValidator.class.getSimpleName(),
                        AbstractValidator.makeValidationResult(ValidationResult.UNAPPLICABLE, "Error occured during validation using assembly '" + assembly.toString() + "'")));
            }
        }
        return lines;
    }
}
