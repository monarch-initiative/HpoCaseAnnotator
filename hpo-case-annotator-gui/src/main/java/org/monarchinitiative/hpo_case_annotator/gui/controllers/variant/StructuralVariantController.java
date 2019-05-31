package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValues;
import org.monarchinitiative.hpo_case_annotator.gui.util.HostServicesWrapper;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.monarchinitiative.hpo_case_annotator.core.validation.VariantSyntaxValidator.NON_NEGATIVE_INTEGER_REGEXP;
import static org.monarchinitiative.hpo_case_annotator.core.validation.VariantSyntaxValidator.POSITIVE_INTEGER_REGEXP;

/**
 *
 */
@Deprecated
public class StructuralVariantController extends AbstractVariantController {


    @FXML
    public ComboBox<GenomeAssembly> genomeBuildComboBox;

    @FXML
    public ComboBox<String> varChromosomeOneComboBox;

    @FXML
    public ComboBox<String> varChromosomeTwoComboBox;

    @FXML
    public TextField varPositionTwoTextField;

    @FXML
    public TextField varPositionOneTextField;

    @FXML
    public ComboBox<String> varClassComboBox;

    @FXML
    public ComboBox<SvClass> svClassComboBox;

    @FXML
    public TextField cnvPloidyTextField;


    @Inject
    public StructuralVariantController(GuiElementValues elementValues, HostServicesWrapper hostServices) {
        super(elementValues, hostServices);
    }

    private static StringConverter<SvClass> getSvClassStringConverter() {
        return new StringConverter<SvClass>() {
            @Override
            public String toString(SvClass object) {
                return object.getRepresentation();
            }

            @Override
            public SvClass fromString(String string) {
                switch (string) {
                    case "Copy number variant":
                    case "CNV":
                        return SvClass.CNV;
                    case "Breakpoint":
                    case "BKPT":
                        return SvClass.BKPT;
                    default:
                        return SvClass.UNKNOWN;
                }
            }
        };
    }

    @Override
    public Binding<String> variantTitleBinding() {
        return Bindings.createStringBinding(() -> {
                    if (isComplete()) {
                        return String.format("Structural variant: %s %s:%s;%s:%s", genomeBuildComboBox.getValue(),
                                varChromosomeOneComboBox.getValue(), varPositionOneTextField.getText(),
                                varChromosomeTwoComboBox.getValue(), varPositionTwoTextField.getText());
                    } else {
                        return "Structural variant: INCOMPLETE: " + validationResults.get(0).getMessage();
                    }
                },
                getObservableVariantDependencies().toArray(new Observable[0]));
    }

    @Override
    List<? extends Observable> getObservableVariantDependencies() {
        return Arrays.asList(genomeBuildComboBox.valueProperty(),
                varChromosomeOneComboBox.valueProperty(), varPositionOneTextField.textProperty(),
                varChromosomeTwoComboBox.valueProperty(), varPositionTwoTextField.textProperty());
    }

    @Override
    public void presentData(Variant data) {

    }

    @Override
    public Variant getData() {
        return Variant.getDefaultInstance();
    }

    public void initialize() {
        genomeBuildComboBox.getItems().addAll(elementValues.getGenomeBuild());
        varChromosomeOneComboBox.getItems().addAll(elementValues.getChromosome());
        varPositionOneTextField.setTextFormatter(makeTextFormatter(varPositionOneTextField, POSITIVE_INTEGER_REGEXP));
        varChromosomeTwoComboBox.getItems().addAll(elementValues.getChromosome());
        varPositionTwoTextField.setTextFormatter(makeTextFormatter(varPositionTwoTextField, POSITIVE_INTEGER_REGEXP));
        varClassComboBox.getItems().addAll(elementValues.getVariantClass());
        svClassComboBox.getItems().addAll(Arrays.stream(SvClass.values()).filter(sc -> !sc.equals(SvClass.UNKNOWN)).collect(Collectors.toList()));
        svClassComboBox.setConverter(getSvClassStringConverter());

        // Breakpoint GUI elements
        // chromosome two combobox and position is enabled only if we are entering data for breakpoint
        BooleanBinding bkptSelected = Bindings.createBooleanBinding(() -> !svClassComboBox.getSelectionModel().isEmpty()
                        && svClassComboBox.getSelectionModel().getSelectedItem().equals(SvClass.BKPT),
                svClassComboBox.valueProperty());
        varChromosomeTwoComboBox.disableProperty().bind(bkptSelected.not());

        // Copy number variation GUI elements
        BooleanBinding cnvSelected = Bindings.createBooleanBinding(() -> !svClassComboBox.getSelectionModel().isEmpty()
                        && svClassComboBox.getSelectionModel().getSelectedItem().equals(SvClass.CNV),
                svClassComboBox.valueProperty());
        cnvPloidyTextField.setTextFormatter(makeTextFormatter(cnvPloidyTextField, NON_NEGATIVE_INTEGER_REGEXP));
        cnvPloidyTextField.visibleProperty().bind(cnvSelected);

    }

    private enum SvClass {
        CNV("Copy number variant"),
        BKPT("Breakpoint"),
        UNKNOWN("Unknown");


        private final String representation;

        SvClass(String representation) {
            this.representation = representation;
        }

        public String getRepresentation() {
            return representation;
        }
    }
}
