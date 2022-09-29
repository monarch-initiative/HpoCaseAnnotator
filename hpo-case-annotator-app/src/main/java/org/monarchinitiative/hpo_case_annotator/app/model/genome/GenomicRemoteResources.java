package org.monarchinitiative.hpo_case_annotator.app.model.genome;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class GenomicRemoteResources implements GenomicResources<GenomicRemoteResource> {

    private final ObjectProperty<GenomicRemoteResource> hg19 = new SimpleObjectProperty<>(this, "hg19");
    private final ObjectProperty<GenomicRemoteResource> hg38 = new SimpleObjectProperty<>(this, "hg38");

    public GenomicRemoteResources() {
        super();
    }

    public GenomicRemoteResources(GenomicRemoteResource hg19, GenomicRemoteResource hg38) {
        this.hg19.set(hg19);
        this.hg38.set(hg38);
    }

    @Override
    public ObjectProperty<GenomicRemoteResource> hg19Property() {
        return hg19;
    }

    @Override
    public ObjectProperty<GenomicRemoteResource> hg38Property() {
        return hg38;
    }


}
