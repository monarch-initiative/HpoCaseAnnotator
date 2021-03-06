package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import com.google.inject.Inject;
import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationResult;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationRunner;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.AbstractDataController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.DiseaseCaseDataController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValues;
import org.monarchinitiative.hpo_case_annotator.gui.util.HostServicesWrapper;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


/**
 * Place for shared content of VariantControllers. Needs to be subclassed by every class that wants to act as a controller
 * of {@link Variant} model class and be placed in {@link DiseaseCaseDataController}.
 * <p>
 * All the variants are validated by the same validator, hence the validator is defined in this class.
 * Created by ielis on 5/17/17.
 */
public abstract class AbstractVariantController extends AbstractDataController<Variant> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVariantController.class);

    /**
     * Allows to open hyperlink in OS-dependent default web browser.
     */
    protected final HostServicesWrapper hostServices;

    /**
     * POJO containing data to be used for populating content of FXML elements such as ComboBoxes.
     */
    protected final GuiElementValues elementValues;

    protected final List<ValidationResult> validationResults;

    private final StringProperty entrezId;

    @Inject
    AbstractVariantController(HostServicesWrapper hostServices, GuiElementValues elementValues) {
        this.hostServices = hostServices;
        this.elementValues = elementValues;
        this.validationResults = new ArrayList<>();
        this.entrezId = new SimpleStringProperty(this, "entrezId", null);
    }

    public String getEntrezId() {
        return entrezId.get();
    }

    public StringProperty entrezIdProperty() {
        return entrezId;
    }

    @Override
    public boolean isComplete() {
        Variant variant = getData();
        ValidationRunner<Variant> runner = ValidationRunner.variantValidationRunner(variant.getVariantValidation().getContext());
        validationResults.clear();
        validationResults.addAll(runner.validateSingleModel(variant));
        return validationResults.isEmpty();
    }

    protected static int parseIntOrGetDefaultValue(Supplier<String> stringSupplier, int defaultValue) {
        try {
            return Integer.parseInt(stringSupplier.get());
        } catch (NumberFormatException e) {
            LOGGER.debug("Unable to convert string {} to integer, returning {}", stringSupplier.get(), defaultValue);
            return defaultValue;
        }
    }

    public abstract Binding<String> variantTitleBinding();

    /**
     * Utility method for keeping track of {@link Observable}s that the {@link Variant} depends on.
     * <p>
     * Validity of the variant is checked after each change to observables.
     *
     * @return {@link List} with observables
     */
    abstract List<? extends Observable> getObservableVariantDependencies();


}
