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
 *
 */
public final class CnvVariantController extends AbstractVariantController {

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
    public TextField cnvPloidyTextField;

    @Inject
    public CnvVariantController(GuiElementValues elementValues, HostServicesWrapper hostServices) {
        super(elementValues, hostServices);
    }

    @Override
    public Binding<String> variantTitleBinding() {
        return Bindings.createStringBinding(() -> {
                    if (isComplete()) {
                        return String.format("CNV: %s %s:%s-%s [*%s]", genomeBuildComboBox.getValue(),
                                chromosomeComboBox.getValue(), beginTextField.getText(),
                                endTextField.getText(), cnvPloidyTextField.getText());
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
                endTextField.textProperty(), cnvPloidyTextField.textProperty());
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
        chromosomeComboBox.getItems().addAll(elementValues.getChromosome());
        beginTextField.setTextFormatter(makeTextFormatter(beginTextField, NON_NEGATIVE_INTEGER_REGEXP));
        endTextField.setTextFormatter(makeTextFormatter(endTextField, POSITIVE_INTEGER_REGEXP));

        cnvPloidyTextField.setTextFormatter(makeTextFormatter(cnvPloidyTextField, NON_NEGATIVE_INTEGER_REGEXP));
    }
}
