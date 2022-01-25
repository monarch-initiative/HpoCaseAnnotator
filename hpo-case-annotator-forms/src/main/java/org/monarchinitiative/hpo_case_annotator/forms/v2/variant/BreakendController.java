package org.monarchinitiative.hpo_case_annotator.forms.v2.variant;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.GenomicAssemblyService;
import org.monarchinitiative.hpo_case_annotator.forms.ComponentController;
import org.monarchinitiative.hpo_case_annotator.forms.InvalidComponentDataException;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.svart.Breakend;
import org.monarchinitiative.svart.Contig;
import org.monarchinitiative.svart.CoordinateSystem;
import org.monarchinitiative.svart.Strand;

public class BreakendController implements ComponentController<Breakend> {

    private final ObjectProperty<GenomicAssemblyService> assemblyService = new SimpleObjectProperty<>(null, "assemblyService");
    @FXML
    private ComboBox<Contig> contigComboBox;
    @FXML
    private TextField positionTextField;
    @FXML
    private TextField idTextField;
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


    @Override
    public void presentComponent(Breakend breakend) {
        contigComboBox.setValue(breakend.contig());
        positionTextField.setText(String.valueOf(breakend.startWithCoordinateSystem(CoordinateSystem.oneBased())));
        idTextField.setText(breakend.id());
        isPositiveStrandCheckBox.setSelected(breakend.strand().isPositive());
    }

    @Override
    public Breakend getComponent() throws InvalidComponentDataException {
        int start = FormUtils.processFormattedInteger(positionTextField.getText());
        return Breakend.of(contigComboBox.getValue(), idTextField.getText(), isPositiveStrandCheckBox.isSelected() ? Strand.POSITIVE : Strand.NEGATIVE, CoordinateSystem.oneBased(), start, start - 1);
    }
}
