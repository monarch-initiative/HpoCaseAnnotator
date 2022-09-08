package org.monarchinitiative.hpo_case_annotator.app.model.genome;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;

import java.util.stream.Stream;

public class GenomicLocalResources extends GenomicResources<GenomicLocalResource> implements Observable {

    static final Callback<GenomicLocalResources, Stream<Observable>> EXTRACTOR = glr -> Stream.of(glr.hg18, glr.hg19, glr.hg38);

    private final ObjectProperty<GenomicLocalResource> hg18 = new SimpleObjectProperty<>(this, "hg18", new GenomicLocalResource());
    private final ObjectProperty<GenomicLocalResource> hg19 = new SimpleObjectProperty<>(this, "hg19", new GenomicLocalResource());
    private final ObjectProperty<GenomicLocalResource> hg38 = new SimpleObjectProperty<>(this, "hg38", new GenomicLocalResource());

    public GenomicLocalResources() {
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

    @Override
    public void addListener(InvalidationListener listener) {
        EXTRACTOR.call(this).forEach(obs -> obs.addListener(listener));
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        EXTRACTOR.call(this).forEach(obs -> obs.removeListener(listener));
    }
}
