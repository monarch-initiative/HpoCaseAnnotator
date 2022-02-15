package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.core.reference.functional.FunctionalAnnotationService;

public class FunctionalAnnotationRegistry {

    private final ObjectProperty<FunctionalAnnotationService> hg18Service = new SimpleObjectProperty<>(this, "hg18Service");
    private final ObjectProperty<FunctionalAnnotationService> hg19Service = new SimpleObjectProperty<>(this, "hg19Service");
    private final ObjectProperty<FunctionalAnnotationService> hg38Service = new SimpleObjectProperty<>(this, "hg38Service");

    public FunctionalAnnotationRegistry() {
    }

    public FunctionalAnnotationRegistry(FunctionalAnnotationService hg18Service,
                                        FunctionalAnnotationService hg19Service,
                                        FunctionalAnnotationService hg38Service) {
        this.hg18Service.set(hg18Service);
        this.hg19Service.set(hg19Service);
        this.hg38Service.set(hg38Service);
    }

    public FunctionalAnnotationService getHg18Service() {
        return hg18Service.get();
    }

    public ObjectProperty<FunctionalAnnotationService> hg18ServiceProperty() {
        return hg18Service;
    }

    public void setHg18Service(FunctionalAnnotationService hg18Service) {
        this.hg18Service.set(hg18Service);
    }

    public FunctionalAnnotationService getHg19Service() {
        return hg19Service.get();
    }

    public ObjectProperty<FunctionalAnnotationService> hg19ServiceProperty() {
        return hg19Service;
    }

    public void setHg19Service(FunctionalAnnotationService hg19Service) {
        this.hg19Service.set(hg19Service);
    }

    public FunctionalAnnotationService getHg38Service() {
        return hg38Service.get();
    }

    public ObjectProperty<FunctionalAnnotationService> hg38ServiceProperty() {
        return hg38Service;
    }

    public void setHg38Service(FunctionalAnnotationService hg38Service) {
        this.hg38Service.set(hg38Service);
    }
}
