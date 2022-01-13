package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyService;

public class GenomicAssemblyRegistry {

    private final ObjectProperty<GenomicAssemblyService> hg18Service = new SimpleObjectProperty<>(this, "hg18Service");
    private final ObjectProperty<GenomicAssemblyService> hg19Service = new SimpleObjectProperty<>(this, "hg19Service");
    private final ObjectProperty<GenomicAssemblyService> hg38Service = new SimpleObjectProperty<>(this, "hg38Service");

    public GenomicAssemblyService getHg18Service() {
        return hg18Service.get();
    }

    public void setHg18Service(GenomicAssemblyService hg18Service) {
        this.hg18Service.set(hg18Service);
    }

    public ObjectProperty<GenomicAssemblyService> hg18ServiceProperty() {
        return hg18Service;
    }

    public GenomicAssemblyService getHg19Service() {
        return hg19Service.get();
    }

    public void setHg19Service(GenomicAssemblyService hg19Service) {
        this.hg19Service.set(hg19Service);
    }

    public ObjectProperty<GenomicAssemblyService> hg19ServiceProperty() {
        return hg19Service;
    }

    public GenomicAssemblyService getHg38Service() {
        return hg38Service.get();
    }

    public void setHg38Service(GenomicAssemblyService hg38Service) {
        this.hg38Service.set(hg38Service);
    }

    public ObjectProperty<GenomicAssemblyService> hg38ServiceProperty() {
        return hg38Service;
    }
}
