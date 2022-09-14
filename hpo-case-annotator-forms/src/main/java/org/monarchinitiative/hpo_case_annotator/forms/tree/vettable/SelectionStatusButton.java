package org.monarchinitiative.hpo_case_annotator.forms.tree.vettable;

import javafx.beans.binding.Binding;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.control.ToggleButton;
import org.controlsfx.control.SegmentedButton;
import org.monarchinitiative.hpo_case_annotator.forms.tree.VettingStatus;

class SelectionStatusButton extends SegmentedButton {

    private final ToggleButton includedButton = new ToggleButton("Y");
    private final ToggleButton indeterminateButton = new ToggleButton("?");
    private final ToggleButton excludedButton = new ToggleButton("N");
    private final ObjectBinding<VettingStatus> vettingStatus;

    public SelectionStatusButton() {
        includedButton.setFocusTraversable(false);
        indeterminateButton.setFocusTraversable(false);
        excludedButton.setFocusTraversable(false);
        getButtons().addAll(includedButton, indeterminateButton, excludedButton);
        setFocusTraversable(false);

        vettingStatus = new ObjectBinding<>() {
            {
                bind(includedButton.selectedProperty(), indeterminateButton.selectedProperty(), excludedButton.selectedProperty());
            }

            @Override
            protected VettingStatus computeValue() {
                if (includedButton.isSelected())
                    return VettingStatus.APPROVED;
                else if (excludedButton.isSelected())
                    return VettingStatus.REJECTED;
                else
                    return VettingStatus.INDETERMINATE;
            }
        };
    }

    public void setVettingStatus(VettingStatus status) {
        getToggleGroup().selectToggle(
                switch (status) {
                    case APPROVED -> includedButton;
                    case INDETERMINATE -> indeterminateButton;
                    case REJECTED -> excludedButton;
                });
    }

    public Binding<VettingStatus> vettingStatusBinding() {
        return vettingStatus;
    }

}
