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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.monarchinitiative.hpo_case_annotator.core.validation.VariantSyntaxValidator.NON_NEGATIVE_INTEGER_REGEXP;
import static org.monarchinitiative.hpo_case_annotator.core.validation.VariantSyntaxValidator.POSITIVE_INTEGER_REGEXP;

/**
 *
 */
public class IntrachromosomalVariantController extends AbstractVariantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntrachromosomalVariantController.class);

    @FXML
    public ComboBox<GenomeAssembly> assemblyComboBox;

    @FXML
    public ComboBox<String> chromosomeComboBox;

    /**
     * Text field for 1-based begin position of the Cnv
     */
    @FXML
    public TextField beginTextField;

    /**
     * Text field for 1-based end position of the Cnv
     */
    @FXML
    public TextField endTextField;

    @FXML
    public TextField ciBeginFirstTextField;

    @FXML
    public TextField ciBeginSecondTextField;

    @FXML
    public TextField ciEndFirstTextField;

    @FXML
    public TextField ciEndSecondTextField;

    @FXML
    public ComboBox<Genotype> genotypeComboBox;

    @FXML
    public ComboBox<StructuralType> svTypeComboBox;

    @FXML
    public CheckBox cosegregationCheckBox;

    @FXML
    public CheckBox preciseCheckBox;

    @FXML
    public TextField insertionLengthTextField;

    private final String defaultRefAllele = "N";

    @Inject
    public IntrachromosomalVariantController(GuiElementValues elementValues, HostServicesWrapper hostServices) {
        super(elementValues, hostServices);
    }

    @Override
    public Binding<String> variantTitleBinding() {
        return Bindings.createStringBinding(() -> {
                    if (isComplete()) {
                        return String.format("%s: %s %s:%s-%s [%s]",
                                svTypeComboBox.getValue() == null ? "" : svTypeComboBox.getValue(),
                                assemblyComboBox.getValue(),
                                chromosomeComboBox.getValue(), beginTextField.getText(),
                                endTextField.getText(), genotypeComboBox.getValue());
                    } else {
                        return "SV: INCOMPLETE: " + validationResults.get(0).getMessage();
                    }
                },
                getObservableVariantDependencies().toArray(new Observable[0]));
    }

    @Override
    List<? extends Observable> getObservableVariantDependencies() {
        return Arrays.asList(assemblyComboBox.valueProperty(),
                chromosomeComboBox.valueProperty(),
                beginTextField.textProperty(),
                endTextField.textProperty(),
                svTypeComboBox.valueProperty(),
                genotypeComboBox.valueProperty(),
                insertionLengthTextField.textProperty());
    }

    @Override
    public void presentData(Variant variant) {
        VariantPosition vp = variant.getVariantPosition();
        assemblyComboBox.setValue(vp.getGenomeAssembly());
        chromosomeComboBox.setValue(vp.getContig());
        beginTextField.setText(String.valueOf(vp.getPos()));
        endTextField.setText(String.valueOf(vp.getPos2()));
        ciBeginFirstTextField.setText(String.valueOf(vp.getCiBeginOne()));
        ciBeginSecondTextField.setText(String.valueOf(vp.getCiBeginTwo()));
        ciEndFirstTextField.setText(String.valueOf(vp.getCiEndOne()));
        ciEndSecondTextField.setText(String.valueOf(vp.getCiEndTwo()));

        genotypeComboBox.setValue(variant.getGenotype());
        svTypeComboBox.setValue(variant.getSvType());
        if (variant.getSvType().equals(StructuralType.INS)) {
            insertionLengthTextField.setText(String.valueOf(variant.getNInserted()));
        }

        VariantValidation vv = variant.getVariantValidation();
        cosegregationCheckBox.setSelected(vv.getCosegregation());
        preciseCheckBox.setSelected(!variant.getImprecise());
    }

    @Override
    public Variant getData() {
        StructuralType svType = svTypeComboBox.getValue() == null ? StructuralType.UNKNOWN : svTypeComboBox.getValue();
        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(assemblyComboBox.getValue() == null ? GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY : assemblyComboBox.getValue())
                        .setContig(chromosomeComboBox.getValue() == null ? "NA" : chromosomeComboBox.getValue())
                        .setPos(parseIntOrGetDefaultValue(beginTextField::getText, 0)) // begin is 1-based
                        .setPos2(parseIntOrGetDefaultValue(endTextField::getText, 0)) // end is also 1-based
                        .setRefAllele(defaultRefAllele)
                        .setAltAllele(svType.name())
                        .setCiBeginOne(parseIntOrGetDefaultValue(ciBeginFirstTextField::getText, 0))
                        .setCiBeginTwo(parseIntOrGetDefaultValue(ciBeginSecondTextField::getText, 0))
                        .setCiEndOne(parseIntOrGetDefaultValue(ciEndFirstTextField::getText, 0))
                        .setCiEndTwo(parseIntOrGetDefaultValue(ciEndSecondTextField::getText, 0))
                        .build())

                .setVariantClass("structural")
                .setGenotype(genotypeComboBox.getValue() == null ? Genotype.UNDEFINED : genotypeComboBox.getValue())
                .setSvType(svType)
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.INTRACHROMOSOMAL)
                        .setCosegregation(cosegregationCheckBox.isSelected())
                        .build())
                .setImprecise(!preciseCheckBox.isSelected())
                .setNInserted(svType.equals(StructuralType.INS)
                        ? parseIntOrGetDefaultValue(insertionLengthTextField::getText, 0)
                        : 0)
                .build();
    }

    public void initialize() {
        assemblyComboBox.getItems().addAll(elementValues.getGenomeBuild());
        chromosomeComboBox.getItems().addAll(elementValues.getChromosome());
        beginTextField.setTextFormatter(makeTextFormatter(beginTextField, NON_NEGATIVE_INTEGER_REGEXP));
        decorateWithTooltipOnFocus(beginTextField, "1-based begin position");
        endTextField.setTextFormatter(makeTextFormatter(endTextField, POSITIVE_INTEGER_REGEXP));
        decorateWithTooltipOnFocus(endTextField, "1-based end position");
        genotypeComboBox.getItems().addAll(Arrays.stream(Genotype.values()).filter(g -> !g.equals(Genotype.UNRECOGNIZED)).collect(Collectors.toList()));
        svTypeComboBox.getItems().addAll(Arrays.stream(StructuralType.values()).filter(g -> !g.equals(StructuralType.UNRECOGNIZED)).collect(Collectors.toList()));
        decorateWithTooltipOnFocus(insertionLengthTextField, "N of inserted bases");
        insertionLengthTextField.setTextFormatter(makeToleratingTextFormatter(insertionLengthTextField, NON_NEGATIVE_INTEGER_REGEXP));

        preciseCheckBox.selectedProperty().addListener((obs, old, current) -> {
            if (current) {
//                 precise variant, disable CI fields
                ciBeginFirstTextField.setDisable(true);
                ciBeginFirstTextField.clear();
                ciBeginSecondTextField.setDisable(true);
                ciBeginSecondTextField.clear();
                ciEndFirstTextField.setDisable(true);
                ciEndFirstTextField.clear();
                ciEndSecondTextField.setDisable(true);
                ciEndSecondTextField.clear();
            } else {
//                 imprecise variant, enable CI fields
                ciBeginFirstTextField.setDisable(false);
                ciBeginSecondTextField.setDisable(false);
                ciEndFirstTextField.setDisable(false);
                ciEndSecondTextField.setDisable(false);
            }
        });
        preciseCheckBox.setSelected(true);

        svTypeComboBox.valueProperty().addListener((o, old, novel) -> {
            if (novel.equals(StructuralType.INS)) {
                insertionLengthTextField.setDisable(false);
            } else {
                insertionLengthTextField.clear();
                insertionLengthTextField.setDisable(true);
            }
        });
    }
}
