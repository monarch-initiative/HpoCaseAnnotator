package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.ToggleButton;
import org.controlsfx.control.SegmentedButton;

class TermSelectionButton extends SegmentedButton {

    private final ToggleButton includedButton = new ToggleButton("Y");
    private final ToggleButton excludedButton = new ToggleButton("N");

    TermSelectionButton() {
        initGraphics();
    }

    private void initGraphics() {
        includedButton.setFocusTraversable(false);
        excludedButton.setFocusTraversable(false);
        getButtons().addAll(includedButton, excludedButton);
        setFocusTraversable(false);
    }

    public BooleanProperty includedProperty() {
        return includedButton.selectedProperty();
    }

    public BooleanProperty excludedProperty() {
        return excludedButton.selectedProperty();
    }

}
