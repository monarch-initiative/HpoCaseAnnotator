package org.monarchinitiative.hpo_case_annotator.core.validation;


import com.google.protobuf.Message;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.GenomeAssemblies;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class performs validation of {@link DiseaseCase} instances.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class ValidationRunner<T extends Message> {

    // TODO - add validation of gene, disease, and HPO terms

    private final Function<T, List<ValidationResult>> validationFunction;

    private ValidationRunner(Function<T, List<ValidationResult>> validationFunction) {
        this.validationFunction = validationFunction;
    }

    /**
     * @param assemblies {@link GenomeAssemblies}
     * @return {@link ValidationRunner} that will be validating models using {@link CompletenessValidator} and {@link GenomicPositionValidator}
     */
    public static ValidationRunner<DiseaseCase> forAllValidations(final GenomeAssemblies assemblies) {
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
        return new ValidationRunner<>(validationFunction);
    }

    /**
     * @return {@link ValidationRunner} that will be validating models using {@link CompletenessValidator}
     */
    public static ValidationRunner<DiseaseCase> forCompletenessValidation() {
        Function<DiseaseCase, List<ValidationResult>> validationFunction = dc -> {
            CompletenessValidator cv = new CompletenessValidator();
            return new ArrayList<>(cv.validate(dc));
        };
        return new ValidationRunner<>(validationFunction);
    }

    /**
     * @return {@link ValidationRunner} that will validate {@link DiseaseCase} fields but not variants.
     */
    public static ValidationRunner<DiseaseCase> forDiseaseCaseValidationOmittingVariants() {
        Function<DiseaseCase, List<ValidationResult>> validationFunction = dc -> {
            CompletenessValidator cv = new CompletenessValidator(null);
            return new ArrayList<>(cv.validate(dc));
        };
        return new ValidationRunner<>(validationFunction);
    }

    /**
     * Create {@link ValidationRunner} that returns an empty list if given {@link org.monarchinitiative.hpo_case_annotator.model.proto.Publication}
     * has not yet been used in other biocurated file stored within the <code>diseaseCaseDirectory</code>.
     *
     * @param diseaseCaseDirectory {@link File} pointing to directory with biocurated data
     * @return {@link ValidationRunner} for PubMed validation
     */
    public static ValidationRunner<Publication> forPubMedValidation(File diseaseCaseDirectory) {
        Function<Publication, List<ValidationResult>> validationFunction = pub -> {
            PubMedValidator cv = new PubMedValidator(diseaseCaseDirectory);
            return new ArrayList<>(cv.validate(pub));
        };
        return new ValidationRunner<>(validationFunction);
    }


    /**
     * Get {@link ValidationRunner} for running validation of {@link Variant} with given <code>context</code>.
     *
     * @param context {@link org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation.Context}
     * @return validation runner
     */
    public static ValidationRunner<Variant> variantValidationRunner(VariantValidation.Context context) {
        switch (context) {
            case INTRACHROMOSOMAL:
                return makeIntrachromosomalVariantValidationRunner();
            case TRANSLOCATION:
                return makeBkptValidationRunner();
            case SOMATIC:
            case SPLICING:
            case MENDELIAN:
                return makeDefaultValidationRunner();
            case UNRECOGNIZED:
            case NO_CONTEXT:
            default:
                // no-op runner
                return new ValidationRunner<>(var -> Collections.singletonList(ValidationResult.warn("No validation was run")));
        }
    }

    /**
     * @return {@link ValidationRunner} for validation of {@link Variant}s. Only syntax is validated
     */
    private static ValidationRunner<Variant> makeDefaultValidationRunner() {
        Function<Variant, List<ValidationResult>> validationFunction = var -> {
            VariantSyntaxValidator vsv = new VariantSyntaxValidator();
            return new ArrayList<>(vsv.validate(var));
        };
        return new ValidationRunner<>(validationFunction);
    }

    private static ValidationRunner<Variant> makeBkptValidationRunner() {
        Function<Variant, List<ValidationResult>> validationFunction = var -> {
            BreakendVariantValidator bknd = new BreakendVariantValidator();
            return new ArrayList<>(bknd.validate(var));
        };
        return new ValidationRunner<>(validationFunction);
    }

    private static ValidationRunner<Variant> makeIntrachromosomalVariantValidationRunner() {
        Function<Variant, List<ValidationResult>> validationFunction = var -> {
            IntrachromosomalStructuralVariantValidator cnv = new IntrachromosomalStructuralVariantValidator();
            return new ArrayList<>(cnv.validate(var));
        };
        return new ValidationRunner<>(validationFunction);
    }

    /**
     * Run validation of a single model
     *
     * @param model
     * @return
     */
    public List<ValidationResult> validateSingleModel(T model) {
        return validationFunction.apply(model);
    }


    /**
     * Run validation on given collection of models and return results as a list of validation lines.
     *
     * @param models {@link Collection} of {@link T} instances to be validated
     * @return {@link Map} with {@link T} as keys and {@link List} of {@link ValidationResult}s pertaining to
     * the case
     */
    public Map<T, List<ValidationResult>> validateModels(Collection<T> models) {
        Map<T, List<ValidationResult>> resultMap = new HashMap<>();

        for (T model : models) {
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
        lines.add(new ValidationLine(ModelUtils.getFileNameFor(model), completenessValidator.getClass().getSimpleName(), result));
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
                LOGGER.warn("Unknown genome forAllValidations '{}' in model '{}'", model.getGenomeBuild(), ModelUtils.getFileNameFor(model));
                lines.add(new ValidationLine(ModelUtils.getFileNameFor(model), "GenomeBuildValidator",
                        AbstractValidator.makeValidationResult(ValidationResult.FAILED, "Unknown genome forAllValidations '" + model.getGenomeBuild() + "'")));
                return lines;
        }

        // validate genomic coordinates
        File fastaFile = assembly.getFastaPath();
        if (fastaFile == null || !fastaFile.isFile()) {
            lines.add(new ValidationLine(ModelUtils.getFileNameFor(model), GenomicPositionValidator.class.getSimpleName(),
                    AbstractValidator.makeValidationResult(ValidationResult.UNAPPLICABLE, "Genome forAllValidations '" + assembly.toString() + "' not available. Download in Set resources dialog")));
        } else {
            try (SequenceDao sequenceDao = new SingleFastaSequenceDao(assembly.getFastaPath())) {
                GenomicPositionValidator genomicPositionValidator = new GenomicPositionValidator(sequenceDao);
                lines.add(new ValidationLine(ModelUtils.getFileNameFor(model), GenomicPositionValidator.class.getSimpleName(),
                        genomicPositionValidator.validateDiseaseCase(model)));
            } catch (Exception e) {
                LOGGER.warn("Error occured during validation of genomic coordinates of the model '{}' using assembly '{}'", ModelUtils.getFileNameFor(model), assembly.toString());
                lines.add(new ValidationLine(ModelUtils.getFileNameFor(model), GenomicPositionValidator.class.getSimpleName(),
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
        String modelName = ModelUtils.getFileNameFor(model);

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
