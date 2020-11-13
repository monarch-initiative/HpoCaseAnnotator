package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValues;
import org.monarchinitiative.hpo_case_annotator.gui.util.HostServicesWrapper;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.monarchinitiative.hpo_case_annotator.core.validation.VariantSyntaxValidator.POSITIVE_INTEGER_REGEXP;

/**
 *
 */
public class BreakpointVariantController extends AbstractVariantController {

    @FXML
    public ComboBox<GenomeAssembly> assemblyComboBox;

    @FXML
    public ComboBox<String> leftChrComboBox;

    @FXML
    public TextField leftPosTextField;

    @FXML
    public CheckBox leftStrandCheckBox;

    @FXML
    public ComboBox<String> rightChrComboBox;

    @FXML
    public TextField rightPosTextField;

    @FXML
    public CheckBox rightStrandCheckBox;

    @FXML
    public TextField insertedSequenceTextField;

    @FXML
    public TextField ciBeginLeftTextField;

    @FXML
    public TextField ciBeginRightTextField;

    @FXML
    public TextField ciEndLeftTextField;

    @FXML
    public TextField ciEndRightTextField;

    @FXML
    public CheckBox preciseCheckBox;


    @Inject
    public BreakpointVariantController(GuiElementValues elementValues, HostServicesWrapper hostServices) {
        super(elementValues, hostServices);
    }

    @Override
    public Binding<String> variantTitleBinding() {
        return Bindings.createStringBinding(() -> {
                    if (isComplete()) {
                        return String.format("Breakpoint: %s [%s:%s(%s);%s:%s(%s)]",
                                assemblyComboBox.getValue(),
                                leftChrComboBox.getValue(), leftPosTextField.getText(), leftStrandCheckBox.isSelected() ? '-' : '+',
                                rightChrComboBox.getValue(), rightPosTextField.getText(), rightStrandCheckBox.isSelected() ? '-' : '+'
                        );
                    } else {
                        return "Breakpoint: INCOMPLETE: " + validationResults.get(0).getMessage();
                    }
                },
                getObservableVariantDependencies().toArray(new Observable[0]));
    }

    @Override
    List<? extends Observable> getObservableVariantDependencies() {
        return Arrays.asList(
                assemblyComboBox.valueProperty(),
                leftChrComboBox.valueProperty(),
                leftPosTextField.textProperty(),
                rightChrComboBox.valueProperty(),
                rightPosTextField.textProperty()
        );
    }

    @Override
    public void presentData(Variant variant) {
        VariantPosition vp = variant.getVariantPosition();
        assemblyComboBox.setValue(vp.getGenomeAssembly());

        leftChrComboBox.setValue(vp.getContig());
        leftPosTextField.setText(String.valueOf(vp.getPos()));
        leftStrandCheckBox.setSelected(vp.getContigDirection().equals(VariantPosition.Direction.REV));

        rightChrComboBox.setValue(vp.getContig2());
        rightPosTextField.setText(String.valueOf(vp.getPos2()));
        rightStrandCheckBox.setSelected(vp.getContig2Direction().equals(VariantPosition.Direction.REV));

        ciBeginLeftTextField.setText(String.valueOf(vp.getCiBeginOne()));
        ciEndLeftTextField.setText(String.valueOf(vp.getCiBeginTwo()));
        ciBeginRightTextField.setText(String.valueOf(vp.getCiEndOne()));
        ciEndRightTextField.setText(String.valueOf(vp.getCiEndTwo()));

        insertedSequenceTextField.setText(vp.getAltAllele());

        preciseCheckBox.setSelected(!variant.getImprecise());
    }

    @Override
    public Variant getData() {
        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(assemblyComboBox.getValue() == null ? GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY : assemblyComboBox.getValue())
                        .setContig(leftChrComboBox.getValue() == null ? "NA" : leftChrComboBox.getValue())
                        .setPos(parseIntOrGetDefaultValue(leftPosTextField::getText, 0)) // left is 1-based
                        .setContigDirection(leftStrandCheckBox.isSelected() ? VariantPosition.Direction.REV : VariantPosition.Direction.FWD)
                        .setContig2(rightChrComboBox.getValue() == null ? "NA" : rightChrComboBox.getValue())
                        .setPos2(parseIntOrGetDefaultValue(rightPosTextField::getText, 0)) // right is also 1-based

                        .setContig2Direction(rightStrandCheckBox.isSelected() ? VariantPosition.Direction.REV : VariantPosition.Direction.FWD)
                        .setAltAllele(insertedSequenceTextField.getText())
                        .setCiBeginOne(parseIntOrGetDefaultValue(ciBeginLeftTextField::getText, 0))
                        .setCiBeginTwo(parseIntOrGetDefaultValue(ciEndLeftTextField::getText, 0))
                        .setCiEndOne(parseIntOrGetDefaultValue(ciBeginRightTextField::getText, 0))
                        .setCiEndTwo(parseIntOrGetDefaultValue(ciEndRightTextField::getText, 0))
                        .build())
                .setVariantClass("structural")
                .setGenotype(Genotype.HETEROZYGOUS)
                .setSvType(StructuralType.BND)
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.TRANSLOCATION)
                        .build())
                .setImprecise(!preciseCheckBox.isSelected())
                .build();
    }

    public void initialize() {
        assemblyComboBox.getItems().addAll(elementValues.getGenomeBuild());
        leftChrComboBox.getItems().addAll(elementValues.getChromosome());
        leftPosTextField.setTextFormatter(makeTextFormatter(leftPosTextField, POSITIVE_INTEGER_REGEXP));
        decorateWithTooltipOnFocus(leftPosTextField, "1-based position");
        rightChrComboBox.getItems().addAll(elementValues.getChromosome());
        rightPosTextField.setTextFormatter(makeTextFormatter(rightPosTextField, POSITIVE_INTEGER_REGEXP));
        decorateWithTooltipOnFocus(rightPosTextField, "1-based position");

        preciseCheckBox.selectedProperty().addListener((obs, old, current) -> {
            if (current) {
//                 precise variant, disable CI fields
                ciBeginLeftTextField.setDisable(true);
                ciBeginLeftTextField.clear();
                ciEndLeftTextField.setDisable(true);
                ciEndLeftTextField.clear();
                ciBeginRightTextField.setDisable(true);
                ciBeginRightTextField.clear();
                ciEndRightTextField.setDisable(true);
                ciEndRightTextField.clear();
            } else {
//                 imprecise variant, enable CI fields
                ciBeginLeftTextField.setDisable(false);
                ciEndLeftTextField.setDisable(false);
                ciBeginRightTextField.setDisable(false);
                ciEndRightTextField.setDisable(false);
            }
        });
        preciseCheckBox.setSelected(true);
    }
}
