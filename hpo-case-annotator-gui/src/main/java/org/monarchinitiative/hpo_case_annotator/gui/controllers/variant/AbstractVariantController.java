package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.DataController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValues;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;

/**
 * Place for shared content of VariantControllers. Needs to be subclassed by every class that wants to act as a controller
 * of {@link Variant} model class and be placed in {@link DataController}.
 * Created by ielis on 5/17/17.
 */
public abstract class AbstractVariantController extends TitledPane {

    /**
     * POJO containing data to be used for populating content of FXML elements such as ComboBoxes.
     */
    protected final GuiElementValues elementValues;


    protected AbstractVariantController(GuiElementValues elementValues) {
        this.elementValues = elementValues;
    }


    /**
     * Add {@link Tooltip} to given {@link Control} FXML element.
     *
     * @param control     FXML element to which the tooltip is being added.
     * @param tooltipText text which will be displayed in a tooltip
     */
    protected void addTooltip(Control control, String tooltipText) {
        // Inspiration - https://coderanch.com/t/622070/java/control-Tooltip-visible-time-duration
        Tooltip tooltip = new Tooltip(tooltipText);
        control.setTooltip(tooltip);

        // Mouse events which control displaying of the Tooltip
        control.setOnMouseEntered(e -> {
            Point2D anchor = control.localToScreen(control.getLayoutBounds().getMaxX(),
                    control.getLayoutBounds().getMaxY());
            tooltip.show(control, anchor.getX(), anchor.getY());
        });
        control.setOnMouseExited(e -> tooltip.hide());
    }


    /**
     * Read yaml configuration file and initialize content of fxml view elements.
     */
    protected abstract void populateContent();

    public abstract void presentVariant(Variant variant);

    public abstract Variant getVariant();
}
