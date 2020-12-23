package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationResult;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationRunner;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDiseaseCaseDataController extends AbstractDataController<DiseaseCase> {

    final List<ValidationResult> validationResults;

    private final ValidationRunner<DiseaseCase> diseaseCaseValidationRunner;

    /**
     * Keep track to path of file containing data of current model so we don't need to ask user where to save a model
     * everytime a change has been made.
     */
    private Path currentModelPath;

    public AbstractDiseaseCaseDataController() {
        this.diseaseCaseValidationRunner = ValidationRunner.forDiseaseCaseValidationOmittingVariants();
        this.validationResults = new ArrayList<>();
    }

    public Path getCurrentModelPath() {
        return currentModelPath;
    }

    public void setCurrentModelPath(Path currentModelPath) {
        this.currentModelPath = currentModelPath;
    }

    @Override
    public boolean isComplete() {
        validationResults.clear();
        validationResults.addAll(diseaseCaseValidationRunner.validateSingleModel(getData()));
        return validationResults.isEmpty();
    }

    abstract Binding<String> diseaseCaseTitleBinding();

    /**
     * Utility method for keeping track of {@link Observable}s that the {@link DiseaseCase} depends on.
     *
     * @return {@link List} with observable dependencies
     */
    abstract List<? extends Observable> getObservableDiseaseCaseDependencies();
}
