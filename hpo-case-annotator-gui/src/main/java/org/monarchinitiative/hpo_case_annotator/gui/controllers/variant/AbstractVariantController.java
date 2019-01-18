package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationResult;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationRunner;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.AbstractDataController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.DiseaseCaseDataController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValues;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;

import java.util.ArrayList;
import java.util.List;

/**
 * Place for shared content of VariantControllers. Needs to be subclassed by every class that wants to act as a controller
 * of {@link Variant} model class and be placed in {@link DiseaseCaseDataController}.
 * <p>
 * All the variants are validated by the same validator, hence the validator is defined in this class.
 * Created by ielis on 5/17/17.
 */
public abstract class AbstractVariantController extends AbstractDataController<Variant> {

    /**
     * POJO containing data to be used for populating content of FXML elements such as ComboBoxes.
     */
    final GuiElementValues elementValues;

    final List<ValidationResult> validationResults;

    private final ValidationRunner<Variant> variantValidationRunner;


    AbstractVariantController(GuiElementValues elementValues) {
        this.elementValues = elementValues;
        this.variantValidationRunner = ValidationRunner.variantValidationRunner();
        this.validationResults = new ArrayList<>();
    }


    @Override
    public boolean isComplete() {
        validationResults.clear();
        validationResults.addAll(variantValidationRunner.validateSingleModel(getData()));
        return validationResults.isEmpty();
    }

    public abstract Binding<String> variantTitleBinding();

    /**
     * Utility method for keeping track of {@link Observable}s that the {@link Variant} depends on.
     *
     * @return {@link List} with observable dependencies
     */
    abstract List<? extends Observable> getObservableVariantDependencies();
}
