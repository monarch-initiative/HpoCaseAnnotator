package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.core.liftover.LiftOverService;
import org.monarchinitiative.hpo_case_annotator.core.mining.NamedEntityFinder;
import org.monarchinitiative.phenol.ontology.data.Ontology;

/**
 * Resources used to work with {@link org.monarchinitiative.hpo_case_annotator.model.v2.Study}.
 */
public class StudyResources {

    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>(this, "hpo");
    private final ObjectProperty<NamedEntityFinder> namedEntityFinder = new SimpleObjectProperty<>(this, "namedEntityFinder");
    private final ObjectProperty<DiseaseIdentifierService> diseaseIdentifierService = new SimpleObjectProperty<>(this, "diseaseIdentifierService");
    private final ObjectProperty<GenomicAssemblyRegistry> genomicAssemblyRegistry = new SimpleObjectProperty<>(this, "genomicAssemblyRegistry");
    private final ObjectProperty<FunctionalAnnotationRegistry> functionalAnnotationRegistry = new SimpleObjectProperty<>(this, "functionalAnnotationRegistry");
    private final ObjectProperty<LiftOverService> liftoverService = new SimpleObjectProperty<>(this, "liftoverService");

    // -------------------------------------------------------------------------------------------------------------- //

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
    }

    public void setHpo(Ontology hpo) {
        this.hpo.set(hpo);
    }

    public ObjectProperty<NamedEntityFinder> namedEntityFinderProperty() {
        return namedEntityFinder;
    }

    public void setNamedEntityFinder(NamedEntityFinder namedEntityFinder) {
        this.namedEntityFinder.set(namedEntityFinder);
    }

    public ObjectProperty<DiseaseIdentifierService> diseaseIdentifierServiceProperty() {
        return diseaseIdentifierService;
    }

    public void setDiseaseIdentifierService(DiseaseIdentifierService diseaseIdentifierService) {
        this.diseaseIdentifierService.set(diseaseIdentifierService);
    }

    public ObjectProperty<GenomicAssemblyRegistry> genomicAssemblyRegistryProperty() {
        return genomicAssemblyRegistry;
    }

    public void setGenomicAssemblyRegistry(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        this.genomicAssemblyRegistry.set(genomicAssemblyRegistry);
    }

    public ObjectProperty<FunctionalAnnotationRegistry> functionalAnnotationRegistryProperty() {
        return functionalAnnotationRegistry;
    }

    public void setFunctionalAnnotationRegistry(FunctionalAnnotationRegistry functionalAnnotationRegistry) {
        this.functionalAnnotationRegistry.set(functionalAnnotationRegistry);
    }

    public ObjectProperty<LiftOverService> liftoverServiceProperty() {
        return liftoverService;
    }

    public void setLiftoverService(LiftOverService liftoverService) {
        this.liftoverService.set(liftoverService);
    }
}
