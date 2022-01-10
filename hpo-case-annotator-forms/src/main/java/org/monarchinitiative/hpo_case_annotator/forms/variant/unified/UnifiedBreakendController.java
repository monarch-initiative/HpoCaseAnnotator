package org.monarchinitiative.hpo_case_annotator.forms.variant.unified;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyService;
import org.monarchinitiative.hpo_case_annotator.forms.format.Formats;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.svart.Contig;

public class UnifiedBreakendController {

    private final ObjectProperty<GenomicAssemblyService> assemblyService = new SimpleObjectProperty<>(null, "assemblyService");
    @FXML
    private ComboBox<Contig> contigComboBox;
    @FXML
    private TextField positionTextField;
    @FXML
    private CheckBox isPositiveStrandCheckBox;

    public GenomicAssemblyService getAssemblyService() {
        return assemblyService.get();
    }

    public void setAssemblyService(GenomicAssemblyService assemblyService) {
        this.assemblyService.set(assemblyService);
    }

    public ObjectProperty<GenomicAssemblyService> assemblyServiceProperty() {
        return assemblyService;
    }

    @FXML
    private void initialize() {
        FormUtils.initializeContigComboBox(contigComboBox, assemblyService);
        positionTextField.setTextFormatter(Formats.numberFormatter());
    }

}
