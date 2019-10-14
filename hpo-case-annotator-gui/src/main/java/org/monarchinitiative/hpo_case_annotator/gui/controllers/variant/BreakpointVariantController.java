package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValues;
import org.monarchinitiative.hpo_case_annotator.gui.util.HostServicesWrapper;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.monarchinitiative.hpo_case_annotator.core.validation.VariantSyntaxValidator.NON_NEGATIVE_INTEGER_REGEXP;
import static org.monarchinitiative.hpo_case_annotator.core.validation.VariantSyntaxValidator.POSITIVE_INTEGER_REGEXP;

/**
 * !!WARNING - not yet implemented!!
 */
public final class BreakpointVariantController extends AbstractVariantController {

    @FXML
    public ComboBox<GenomeAssembly> genomeBuildComboBox;

    @FXML
    public ComboBox<String> chrOneComboBox;

    @FXML
    public TextField posOneComboBox;

    @FXML
    public ComboBox<String> chrTwoComboBox;

    @FXML
    public TextField posTwoComboBox;

    @FXML
    public ComboBox<String> variantClassComboBox;


    @Inject
    public BreakpointVariantController(GuiElementValues elementValues, HostServicesWrapper hostServices) {
        super(elementValues, hostServices);
    }

    @Override
    public Binding<String> variantTitleBinding() {
        return Bindings.createStringBinding(() -> {
                    if (isComplete()) {
                        return String.format("Breakpoint: %s %s:%s - %s:%s", genomeBuildComboBox.getValue(),
                                chrOneComboBox.getValue(), posOneComboBox.getText(),
                                chrTwoComboBox.getValue(), posTwoComboBox.getText());
                    } else {
                        return "Breakpoint: INCOMPLETE: " + validationResults.get(0).getMessage();
                    }
                },
                getObservableVariantDependencies().toArray(new Observable[0]));
    }

    @Override
    List<? extends Observable> getObservableVariantDependencies() {
        return Arrays.asList(genomeBuildComboBox.valueProperty(),
                chrOneComboBox.valueProperty(), posOneComboBox.textProperty(),
                chrTwoComboBox.valueProperty(), posTwoComboBox.textProperty());
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
        chrOneComboBox.getItems().addAll(elementValues.getChromosome());
        posOneComboBox.setTextFormatter(makeTextFormatter(posOneComboBox, NON_NEGATIVE_INTEGER_REGEXP));
        chrTwoComboBox.getItems().addAll(elementValues.getChromosome());
        posTwoComboBox.setTextFormatter(makeTextFormatter(posTwoComboBox, POSITIVE_INTEGER_REGEXP));
    }
}
