package org.monarchinitiative.hpo_case_annotator.app.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.core.liftover.LiftOverService;
import org.monarchinitiative.hpo_case_annotator.forms.FunctionalAnnotationRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.GenomicAssemblyRegistry;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class OptionalServices {

    private final ObjectProperty<ExecutorService> executorService = new SimpleObjectProperty<>(this, "executorService");
    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>(this, "hpo");
    private final ObjectProperty<DiseaseIdentifierService> diseaseIdentifierService = new SimpleObjectProperty<>(this, "diseaseIdentifierService");
    private final GenomicAssemblyRegistry genomicAssemblyRegistry = new GenomicAssemblyRegistry();
    private final FunctionalAnnotationRegistry functionalAnnotationRegistry = new FunctionalAnnotationRegistry();
    private final ObjectProperty<LiftOverService> liftoverService = new SimpleObjectProperty<>(this, "liftoverService");

    public ExecutorService getExecutorService() {
        return executorService.get();
    }

    public ObjectProperty<ExecutorService> executorServiceProperty() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService.set(executorService);
    }

    public Ontology getHpo() {
        return hpo.get();
    }

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
    }

    public void setHpo(Ontology hpo) {
        this.hpo.set(hpo);
    }

    public DiseaseIdentifierService getDiseaseIdentifierService() {
        return diseaseIdentifierService.get();
    }

    public ObjectProperty<DiseaseIdentifierService> diseaseIdentifierServiceProperty() {
        return diseaseIdentifierService;
    }

    public void setDiseaseIdentifierService(DiseaseIdentifierService diseaseIdentifierService) {
        this.diseaseIdentifierService.set(diseaseIdentifierService);
    }

    public GenomicAssemblyRegistry getGenomicAssemblyRegistry() {
        return genomicAssemblyRegistry;
    }

    public FunctionalAnnotationRegistry getFunctionalAnnotationRegistry() {
        return functionalAnnotationRegistry;
    }

    public LiftOverService getLiftoverService() {
        return liftoverService.get();
    }

    public ObjectProperty<LiftOverService> liftoverServiceProperty() {
        return liftoverService;
    }

    public void setLiftoverService(LiftOverService liftoverService) {
        this.liftoverService.set(liftoverService);
    }
}
