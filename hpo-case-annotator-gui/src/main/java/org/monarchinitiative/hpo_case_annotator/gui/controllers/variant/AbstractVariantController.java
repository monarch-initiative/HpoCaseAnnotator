package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import javafx.beans.binding.Binding;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.DataController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValues;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;

/**
 * Place for shared content of VariantControllers. Needs to be subclassed by every class that wants to act as a controller
 * of {@link Variant} model class and be placed in {@link DataController}.
 * Created by ielis on 5/17/17.
 */
public abstract class AbstractVariantController {

    /**
     *
     */
    static final String INTEGER_REGEXP = "\\d+";

    static final String ALLELE_REGEXP = "[ACGTacgt]+";

    static final String SNIPPET_REGEXP = "[ACGT]+\\[([ACGT]+|-)/([ACGT]+|-)][ACGT]+";

    static final String CSS_SNIPPET_REGEXP = "[ACGTacgt]+\\|[ACGTacgt]+";

    /**
     * POJO containing data to be used for populating content of FXML elements such as ComboBoxes.
     */
    final GuiElementValues elementValues;


    AbstractVariantController(GuiElementValues elementValues) {
        this.elementValues = elementValues;
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
     * @param yesRegexp String with regular expression. The <code>control</code> will have green border if the content matches the <code>yesRegexp</code> and red border if it does not
     * @param <T>       class of the formatter being returned
     * @return {@link TextFormatter} for the <code>control</code>'s content
     */
    static <T> TextFormatter<T> makeTextFormatter(Control control, String yesRegexp) {
        return new TextFormatter<>(c -> {
            if (c.getControlNewText().matches(yesRegexp)) {
                control.setStyle("-fx-border-color: green; -fx-border-width: 1px;");
            } else {
                control.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            }
            return c;
        });
    }


    public abstract void presentVariant(Variant variant);

    public abstract Variant getVariant();

    /**
     * @return {@link BooleanBinding} that evaluates to true if the data regarding the entered variant is complete.
     */
    public abstract BooleanBinding isCompleteBinding();

    public abstract Binding<String> variantTitleBinding();
}
