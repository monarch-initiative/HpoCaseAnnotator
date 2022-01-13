package org.monarchinitiative.hpo_case_annotator.app.model.genome;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class GenomicLocalResources extends GenomicResources<GenomicLocalResource> {

    private final ObjectProperty<GenomicLocalResource> hg18 = new SimpleObjectProperty<>(this, "hg18", new GenomicLocalResource());
    private final ObjectProperty<GenomicLocalResource> hg19 = new SimpleObjectProperty<>(this, "hg19", new GenomicLocalResource());
    private final ObjectProperty<GenomicLocalResource> hg38 = new SimpleObjectProperty<>(this, "hg38", new GenomicLocalResource());

    public GenomicLocalResources() {
        super();
    }

    public GenomicLocalResources(GenomicLocalResource hg18, GenomicLocalResource hg19, GenomicLocalResource hg38) {
        this.hg18.set(hg18);
        this.hg19.set(hg19);
        this.hg38.set(hg38);
    }

    @Override
    public ObjectProperty<GenomicLocalResource> hg18Property() {
        return hg18;
    }

    @Override
    public ObjectProperty<GenomicLocalResource> hg19Property() {
        return hg19;
    }

    @Override
    public ObjectProperty<GenomicLocalResource> hg38Property() {
        return hg38;
    }


}
