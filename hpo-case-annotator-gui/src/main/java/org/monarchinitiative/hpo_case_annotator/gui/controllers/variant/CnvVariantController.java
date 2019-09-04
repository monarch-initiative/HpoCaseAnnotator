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
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.monarchinitiative.hpo_case_annotator.core.validation.VariantSyntaxValidator.*;

/**
 *
 */
public final class CnvVariantController extends AbstractVariantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CnvVariantController.class);

    @FXML
    public ComboBox<GenomeAssembly> genomeBuildComboBox;

    @FXML
    public ComboBox<String> chromosomeComboBox;

    @FXML
    public TextField beginTextField;

    @FXML
    public TextField endTextField;

    @FXML
    public ComboBox<String> variantClassComboBox;

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

    @Inject
    public CnvVariantController(GuiElementValues elementValues, HostServicesWrapper hostServices) {
        super(elementValues, hostServices);
    }

    private static int parseIntOrGetDefaultValue(Supplier<String> stringSupplier, int defaultValue) {
        try {
            return Integer.parseInt(stringSupplier.get());
        } catch (NumberFormatException e) {
            LOGGER.debug("Unable to convert string {} to integer, returning {}", stringSupplier.get(), defaultValue);
            return defaultValue;
        }
    }

    @Override
    public Binding<String> variantTitleBinding() {
        return Bindings.createStringBinding(() -> {
                    if (isComplete()) {
                        return String.format("CNV: %s %s:%s-%s [%s]", genomeBuildComboBox.getValue(),
                                chromosomeComboBox.getValue(), beginTextField.getText(),
                                endTextField.getText(), genotypeComboBox.getValue());
                    } else {
                        return "CNV: INCOMPLETE: " + validationResults.get(0).getMessage();
                    }
                },
                getObservableVariantDependencies().toArray(new Observable[0]));
    }

    @Override
    List<? extends Observable> getObservableVariantDependencies() {
        return Arrays.asList(genomeBuildComboBox.valueProperty(),
                chromosomeComboBox.valueProperty(), beginTextField.textProperty(),
                endTextField.textProperty(), genotypeComboBox.valueProperty());
    }

    @Override
    public void presentData(Variant variant) {
        VariantPosition vp = variant.getVariantPosition();
        genomeBuildComboBox.setValue(vp.getGenomeAssembly());
        chromosomeComboBox.setValue(vp.getContig());
        beginTextField.setText(String.valueOf(vp.getPos() - 1)); // convert to 0-based
        endTextField.setText(String.valueOf(vp.getPos2()));
        ciBeginFirstTextField.setText(String.valueOf(vp.getCiBeginOne()));
        ciBeginSecondTextField.setText(String.valueOf(vp.getCiBeginTwo()));
        ciEndFirstTextField.setText(String.valueOf(vp.getCiEndOne()));
        ciEndSecondTextField.setText(String.valueOf(vp.getCiEndTwo()));

        variantClassComboBox.setValue(variant.getVariantClass());
        genotypeComboBox.setValue(variant.getGenotype());
        svTypeComboBox.setValue(variant.getSvType());

        VariantValidation vv = variant.getVariantValidation();
        cosegregationCheckBox.setSelected(vv.getCosegregation());
    }

    @Override
    public Variant getData() {
        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(genomeBuildComboBox.getValue() == null ? GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY : genomeBuildComboBox.getValue())
                        .setContig(chromosomeComboBox.getValue() == null ? "NA" : chromosomeComboBox.getValue())
                        // we need to save 1-based coordinate in the VariantPosition, hence +1
                        .setPos(parseIntOrGetDefaultValue(beginTextField::getText, 0) + 1) // convert to 1-based
                        .setPos2(parseIntOrGetDefaultValue(endTextField::getText, 0))
                        .setCiBeginOne(parseIntOrGetDefaultValue(ciBeginFirstTextField::getText, 0))
                        .setCiBeginTwo(parseIntOrGetDefaultValue(ciBeginSecondTextField::getText, 0))
                        .setCiEndOne(parseIntOrGetDefaultValue(ciEndFirstTextField::getText, 0))
                        .setCiEndTwo(parseIntOrGetDefaultValue(ciEndSecondTextField::getText, 0))
                        .build())

                .setVariantClass(variantClassComboBox.getValue() == null ? "" : variantClassComboBox.getValue())
                .setGenotype(genotypeComboBox.getValue() == null ? Genotype.UNDEFINED : genotypeComboBox.getValue())
                .setSvType(svTypeComboBox.getValue() == null ? StructuralType.UNKNOWN : svTypeComboBox.getValue())
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.CNV)
                        .setCosegregation(cosegregationCheckBox.isSelected())
                        .build())
                .build();
    }

    public void initialize() {
        genomeBuildComboBox.getItems().addAll(elementValues.getGenomeBuild());
        chromosomeComboBox.getItems().addAll(elementValues.getChromosome());
        beginTextField.setTextFormatter(makeTextFormatter(beginTextField, NON_NEGATIVE_INTEGER_REGEXP));
        endTextField.setTextFormatter(makeTextFormatter(endTextField, POSITIVE_INTEGER_REGEXP));
        variantClassComboBox.getItems().addAll(elementValues.getVariantClass());
        genotypeComboBox.getItems().addAll(Arrays.stream(Genotype.values()).filter(g -> !g.equals(Genotype.UNRECOGNIZED)).collect(Collectors.toList()));
        svTypeComboBox.getItems().addAll(Arrays.stream(StructuralType.values()).filter(g -> !g.equals(StructuralType.UNRECOGNIZED)).collect(Collectors.toList()));

        ciBeginFirstTextField.setTextFormatter(makeTextFormatter(ciBeginFirstTextField, INTEGER_REGEXP));
        ciBeginSecondTextField.setTextFormatter(makeTextFormatter(ciBeginSecondTextField, INTEGER_REGEXP));
        ciEndFirstTextField.setTextFormatter(makeTextFormatter(ciEndFirstTextField, INTEGER_REGEXP));
        ciEndSecondTextField.setTextFormatter(makeTextFormatter(ciEndSecondTextField, INTEGER_REGEXP));
    }
}
