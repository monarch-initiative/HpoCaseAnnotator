package org.monarchinitiative.hpo_case_annotator.core.validation;


import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssemblies;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class performs validation of {@link DiseaseCase} instances.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.2
 * @since 0.0
 */
public final class ValidationRunner {

    // TODO - add validation of gene, disease, and HPO terms

    private final Function<DiseaseCase, List<ValidationResult>> validationFunction;

    private ValidationRunner(Function<DiseaseCase, List<ValidationResult>> validationFunction) {
        this.validationFunction = validationFunction;
    }

    /**
     * @param assemblies {@link GenomeAssemblies}
     * @return {@link ValidationRunner} that will be validating models using {@link CompletenessValidator} and {@link GenomicPositionValidator}
     */
    public static ValidationRunner forAllValidations(final GenomeAssemblies assemblies) {
        Function<DiseaseCase, List<ValidationResult>> validationFunction = dc -> {
            CompletenessValidator cv = new CompletenessValidator();
            GenomicPositionValidator gpv = new GenomicPositionValidator(assemblies);

            List<ValidationResult> results = new ArrayList<>();
            results.addAll(cv.validate(dc));
            results.addAll(dc.getVariantList().stream()
                    .map(gpv::validate)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));
            return results;
        };
        return new ValidationRunner(validationFunction);
    }

    /**
     * @return {@link ValidationRunner} that will be validating models using {@link CompletenessValidator}
     */
    public static ValidationRunner forCompletenessValidation() {
        Function<DiseaseCase, List<ValidationResult>> validationFunction = dc -> {
            CompletenessValidator cv = new CompletenessValidator();
            return new ArrayList<>(cv.validate(dc));
        };
        return new ValidationRunner(validationFunction);
    }

    public List<ValidationResult> validateSingleModel(DiseaseCase diseaseCase) {
        return validationFunction.apply(diseaseCase);
    }


    /**
     * Run validation on given collection of models and return results as a list of validation lines.
     *
     * @param models {@link Collection} of {@link DiseaseCase} instances.
     * @return {@link Map} with {@link DiseaseCase} as keys and {@link List} of {@link ValidationResult}s pertaining to
     * the case
     */
    public Map<DiseaseCase, List<ValidationResult>> validateModels(Collection<DiseaseCase> models) {
        Map<DiseaseCase, List<ValidationResult>> resultMap = new HashMap<>();

        for (DiseaseCase model : models) {
            final List<ValidationResult> results = validateSingleModel(model);
            resultMap.put(model, results);
        }

        return resultMap;
    }


//  ******************************************** LEGACY CODE ********************************************


//    @Deprecated
//    public static Collection<ValidationLine> validateModel(DiseaseCase model, GenomeAssemblies assemblies) {
//        Set<ValidationLine> lines = new HashSet<>();

        /*// validate completness
        CompletenessValidator completenessValidator = new CompletenessValidator(variantValidator);
        ValidationResult result = completenessValidator.validateDiseaseCase(model);
        lines.add(new ValidationLine(ModelUtils.getNameFor(model), completenessValidator.getClass().getSimpleName(), result));
        if (result != ValidationResult.PASSED)
            return lines; // further validation is a waste of time, since the model is incomplete

        // figure out which genome forAllValidations is used in the current model
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
                LOGGER.warn("Unknown genome forAllValidations '{}' in model '{}'", model.getGenomeBuild(), ModelUtils.getNameFor(model));
                lines.add(new ValidationLine(ModelUtils.getNameFor(model), "GenomeBuildValidator",
                        AbstractValidator.makeValidationResult(ValidationResult.FAILED, "Unknown genome forAllValidations '" + model.getGenomeBuild() + "'")));
                return lines;
        }

        // validate genomic coordinates
        File fastaFile = assembly.getFastaPath();
        if (fastaFile == null || !fastaFile.isFile()) {
            lines.add(new ValidationLine(ModelUtils.getNameFor(model), GenomicPositionValidator.class.getSimpleName(),
                    AbstractValidator.makeValidationResult(ValidationResult.UNAPPLICABLE, "Genome forAllValidations '" + assembly.toString() + "' not available. Download in Set resources dialog")));
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
        }*/
//        return lines;
//    }


//    /**
//     * Validate single model instance with validators.
//     *
//     * @param model {@link DiseaseCase} instance to be validated.
//     * @return {@link List} of {@link ValidationLine}s created by validators.
//     */
    /*private List<ValidationLine> singleValidation(DiseaseCase model) {
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
        return Collections.emptyList();
    }*/


}
