package org.monarchinitiative.hpo_case_annotator.app.model.genome;

import javafx.beans.property.ObjectProperty;

public abstract class GenomicResources<T> {

    public GenomicResources() {
    }

    public T getHg18() {
        return hg18Property().get();
    }

    public void setHg18(T hg18) {
        hg18Property().set(hg18);
    }

    public abstract ObjectProperty<T> hg18Property();

    public T getHg19() {
        return hg19Property().get();
    }

    public void setHg19(T hg19) {
        hg19Property().set(hg19);
    }

    public abstract ObjectProperty<T> hg19Property();

    public T getHg38() {
        return hg38Property().get();
    }

    public void setHg38(T hg38) {
        hg38Property().set(hg38);
    }

    public abstract ObjectProperty<T> hg38Property();

}
