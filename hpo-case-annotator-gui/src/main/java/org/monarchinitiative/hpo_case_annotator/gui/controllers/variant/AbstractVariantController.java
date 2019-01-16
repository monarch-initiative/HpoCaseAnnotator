package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationResult;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationRunner;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.DataController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.DiseaseCaseDataController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValues;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;

import java.util.ArrayList;
import java.util.List;

/**
 * Place for shared content of VariantControllers. Needs to be subclassed by every class that wants to act as a controller
 * of {@link Variant} model class and be placed in {@link DiseaseCaseDataController}.
 * Created by ielis on 5/17/17.
 */
public abstract class AbstractVariantController implements DataController<Variant> {

    private static final String VALID_STYLE = "-fx-border-color: green; -fx-border-width: 1px;";

    private static final String INVALID_STYLE = "-fx-border-color: red; -fx-border-width: 2px;";

    private static final String EMPTY_STYLE = "";

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


    /**
     * Add {@link Tooltip} to given <code>control</code> FXML element that is visible when the <code>control</code> is
     * focused on.
     *
     * @param control     FXML element to which the tooltip is being added
     * @param tooltipText text which will be displayed in a tooltip
     */
    static void decorateWithTooltip(Control control, String tooltipText) {
        Tooltip t = new Tooltip(tooltipText);
        control.setTooltip(t);
        control.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                Point2D anchor = control.localToScreen(control.getLayoutBounds().getMaxX(), control.getLayoutBounds().getMaxY());
                t.show(control, anchor.getX(), anchor.getY());
            } else {
                t.hide();
            }
        }));
    }

    /**
     * @param control   {@link Control} wrapped with the text formatter
     * @param yesRegexp String with regular expression. The <code>control</code> will have green border if the content
     *                  matches the <code>yesRegexp</code> and red border if it does not
     * @param <T>       class of the formatter being returned
     * @return {@link TextFormatter} for the <code>control</code>'s content
     */
    static <T> TextFormatter<T> makeTextFormatter(Control control, String yesRegexp) {
        return new TextFormatter<>(change -> {
            if (change.getControlNewText().matches(yesRegexp)) {
                control.setStyle(VALID_STYLE);
            } else {
                control.setStyle(INVALID_STYLE);
            }
            return change;
        });
    }

    /**
     * @param control   {@link Control} wrapped with the text formatter
     * @param yesRegexp String with regular expression. The <code>control</code> will have green border if the content
     *                  matches the <code>yesRegexp</code>, red border if it does not, and <em>neutral</em> border, if
     *                  the content is empty
     * @param <T>       class of the formatter being returned
     * @return {@link TextFormatter} for the <code>control</code>'s content
     */
    static <T> TextFormatter<T> makeToleratingTextFormatter(Control control, String yesRegexp) {
        return new TextFormatter<>(change -> {
            if (change.getControlNewText().isEmpty()) {
                control.setStyle(EMPTY_STYLE);
            } else if (change.getControlNewText().matches(yesRegexp)) {
                control.setStyle(VALID_STYLE);
            } else {
                control.setStyle(INVALID_STYLE);
            }
            return change;
        });
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
