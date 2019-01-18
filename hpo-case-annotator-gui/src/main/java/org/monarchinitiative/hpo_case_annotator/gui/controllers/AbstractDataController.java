package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;

/**
 * Abstract class for grouping GUI-related methods, such as tooltips, text formatters..
 *
 * @param <T> type of the data being controlled
 */
public abstract class AbstractDataController<T> implements DataController<T> {


    private static final String VALID_STYLE = "-fx-border-color: green; -fx-border-width: 1px;";

    private static final String INVALID_STYLE = "-fx-border-color: red; -fx-border-width: 2px;";

    private static final String EMPTY_STYLE = "";


    /**
     * Add {@link Tooltip} to given <code>control</code> FXML element that is visible when the <code>control</code> is
     * focused on.
     *
     * @param control     FXML element to which the tooltip is being added
     * @param tooltipText text which will be displayed in a tooltip
     */
    protected static void decorateWithTooltipOnFocus(Control control, String tooltipText) {
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
     * @param <K>       class of the formatter being returned
     * @return {@link TextFormatter} for the <code>control</code>'s content
     */
    protected static <K> TextFormatter<K> makeTextFormatter(Control control, String yesRegexp) {
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
     * @param <K>       class of the formatter being returned
     * @return {@link TextFormatter} for the <code>control</code>'s content
     */
    protected static <K> TextFormatter<K> makeToleratingTextFormatter(Control control, String yesRegexp) {
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
}
