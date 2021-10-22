package org.monarchinitiative.hpo_case_annotator.forms.variant;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.forms.format.ContigNameStringConverter;
import org.monarchinitiative.hpo_case_annotator.forms.format.Formats;
import org.monarchinitiative.svart.Contig;

public class BreakendController {

    public ComboBox<Contig> contigComboBox;
    public TextField positionTextField;
    public CheckBox isPositiveStrandCheckBox;

    public void initialize() {
        contigComboBox.setConverter(ContigNameStringConverter.getInstance());
        positionTextField.setTextFormatter(Formats.numberFormatter());
    }

}
