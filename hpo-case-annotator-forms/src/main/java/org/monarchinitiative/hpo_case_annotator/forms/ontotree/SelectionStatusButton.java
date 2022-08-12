package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import javafx.beans.binding.Binding;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.control.ToggleButton;
import org.controlsfx.control.SegmentedButton;

class SelectionStatusButton extends SegmentedButton {

    private final ToggleButton includedButton = new ToggleButton("Y");
    private final ToggleButton indeterminateButton = new ToggleButton("?");
    private final ToggleButton excludedButton = new ToggleButton("N");
    private final ObjectBinding<SelectionStatus> selectionStatus;

    SelectionStatusButton() {
        includedButton.setFocusTraversable(false);
        indeterminateButton.setFocusTraversable(false);
        excludedButton.setFocusTraversable(false);
        getButtons().addAll(includedButton, indeterminateButton, excludedButton);
        setFocusTraversable(false);

        selectionStatus = new ObjectBinding<>() {
            {
                bind(includedButton.selectedProperty(), indeterminateButton.selectedProperty(), excludedButton.selectedProperty());
            }

            @Override
            protected SelectionStatus computeValue() {
                if (includedButton.isSelected())
                    return SelectionStatus.SELECTED;
                else if (excludedButton.isSelected())
                    return SelectionStatus.UNSELECTED;
                else
                    return SelectionStatus.INDETERMINATE;
            }
        };
    }

    void setSelectionStatus(SelectionStatus status) {
        getToggleGroup().selectToggle(
                switch (status) {
                    case SELECTED -> includedButton;
                    case INDETERMINATE -> indeterminateButton;
                    case UNSELECTED -> excludedButton;
                });
    }

    Binding<SelectionStatus> selectionStatusBinding() {
        return selectionStatus;
    }

}
