package org.monarchinitiative.hpo_case_annotator.forms.individual;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.forms.ComponentController;
import org.monarchinitiative.hpo_case_annotator.forms.format.SexStringConverter;
import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;

import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class IndividualController implements ComponentController<Individual> {

    public TextField individualIdTextField;
    public TextField ageTextField;
    public ComboBox<Sex> sexComboBox;

    // TODO - add
    //  - phenotypes
    //  - diseases
    //  - variant genotypes

    public void initialize() {
        sexComboBox.setConverter(SexStringConverter.getInstance());
        sexComboBox.getItems().addAll(Sex.values());
    }

    @Override
    public void presentComponent(Individual individual) {
        individualIdTextField.setText(individual.id());
        individual.age().ifPresent(age -> ageTextField.setText(age.toString()));
    }

    @Override
    public Individual getComponent() {
        Period age = null;
        try {
            age = Period.parse(ageTextField.getText());
        } catch (DateTimeParseException ignored) {}
            return Individual.of(individualIdTextField.getText(),
                age,
                List.of(),
                List.of(),
                Map.of(),
                Sex.UNKNOWN);
    }

}
