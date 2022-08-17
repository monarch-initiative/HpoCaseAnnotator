package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.IdStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.IndividualStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.PublicationStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.VariantsStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividualStudy;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.util.List;

public class IndividualStudySteps {

    private final HCAControllerFactory controllerFactory;
    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();
    private final ObjectProperty<DiseaseIdentifierService> diseaseIdentifierService = new SimpleObjectProperty<>();

    public IndividualStudySteps(HCAControllerFactory controllerFactory,
                                ObjectProperty<Ontology> hpo,
                                ObjectProperty<DiseaseIdentifierService> diseaseIdentifierService) {
        this.controllerFactory = controllerFactory;
        this.hpo.bind(hpo);
        this.diseaseIdentifierService.bind(diseaseIdentifierService);
    }

    public List<Step<ObservableIndividualStudy>> getSteps() {
        VariantsStep<ObservableIndividualStudy> variants = new VariantsStep<>(controllerFactory);
        variants.setHeader("Add genomic variants identified in the individual.");
        IndividualStep<ObservableIndividualStudy> individual = new IndividualStep<>();
        individual.hpoProperty().bind(hpo);
        individual.diseaseIdentifierServiceProperty().bind(diseaseIdentifierService);
        return List.of(
                new PublicationStep<>(),
                variants,
                individual,
                new IdStep<>()
        );
    }

}
