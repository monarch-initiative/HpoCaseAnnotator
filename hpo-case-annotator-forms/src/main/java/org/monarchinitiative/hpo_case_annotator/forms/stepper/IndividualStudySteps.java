package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.FunctionalAnnotationRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.*;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividualStudy;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.util.List;

/**
 * <h2>Properties</h2>
 * {@link IndividualStudySteps} needs the following properties to be set in order to work.
 * <ul>
 *     <li>{@link #hpoProperty()}</li>
 *     <li>{@link #diseaseIdentifierServiceProperty()}</li>
 *     <li>{@link #genomicAssemblyRegistryProperty()}</li>
 *     <li>{@link #functionalAnnotationRegistryProperty()}</li>
 * </ul>
 */
public class IndividualStudySteps {

    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();
    private final ObjectProperty<DiseaseIdentifierService> diseaseIdentifierService = new SimpleObjectProperty<>();
    private final ObjectProperty<GenomicAssemblyRegistry> genomicAssemblyRegistry = new SimpleObjectProperty<>();
    private final ObjectProperty<FunctionalAnnotationRegistry> functionalAnnotationRegistry = new SimpleObjectProperty<>();

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
    }

    public ObjectProperty<DiseaseIdentifierService> diseaseIdentifierServiceProperty() {
        return diseaseIdentifierService;
    }

    public ObjectProperty<GenomicAssemblyRegistry> genomicAssemblyRegistryProperty() {
        return genomicAssemblyRegistry;
    }

    public ObjectProperty<FunctionalAnnotationRegistry> functionalAnnotationRegistryProperty() {
        return functionalAnnotationRegistry;
    }

    public List<Step<ObservableIndividualStudy>> getSteps() {
        PublicationStep<ObservableIndividualStudy> publication = new PublicationStep<>();
        publication.setHeader("Set publication data");

        VariantsStep<ObservableIndividualStudy> variants = new VariantsStep<>();
        variants.setHeader("Add genomic variants identified in the investigated individual.");
        variants.genomicAssemblyRegistryProperty().bind(genomicAssemblyRegistry);
        variants.functionalAnnotationRegistryProperty().bind(functionalAnnotationRegistry);

        IndividualStep<ObservableIndividualStudy> individual = new IndividualStep<>();
        individual.setHeader("Add data regarding the investigated individual.");
        individual.hpoProperty().bind(hpo);
        individual.diseaseIdentifierServiceProperty().bind(diseaseIdentifierService);
        individual.variantsProperty().bind(variants.variantsProperty());

        IndividualStudyIdStep identifiers = new IndividualStudyIdStep();

        return List.of(
                publication,
                variants,
                individual,
                identifiers
        );
    }

}
