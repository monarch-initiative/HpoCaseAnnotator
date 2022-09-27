package org.monarchinitiative.hpo_case_annotator.app.model.genome;

import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.observable.deep.DeepObservable;

import java.util.stream.Stream;

public class GenomicLocalResources extends DeepObservable implements GenomicResources<GenomicLocalResource> {

    private static final Callback<GenomicLocalResources, Stream<Observable>> EXTRACTOR = glr -> Stream.of(glr.hg19, glr.hg38);

    private final ObjectProperty<GenomicLocalResource> hg19 = new SimpleObjectProperty<>(this, "hg19", new GenomicLocalResource());
    private final ObjectProperty<GenomicLocalResource> hg38 = new SimpleObjectProperty<>(this, "hg38", new GenomicLocalResource());
    private final BooleanBinding genomicResourcesAreUnset = hg19.isNull().and(hg38.isNull());;

    public GenomicLocalResources() {
    }

    @Override
    public ObjectProperty<GenomicLocalResource> hg19Property() {
        return hg19;
    }

    @Override
    public ObjectProperty<GenomicLocalResource> hg38Property() {
        return hg38;
    }

    public BooleanBinding genomicResourcesAreUnset() {
        return genomicResourcesAreUnset;
    }

    @Override
    protected Stream<Property<? extends Observable>> objectProperties() {
        return Stream.of(hg19, hg38);
    }

    @Override
    protected Stream<Observable> observables() {
        return EXTRACTOR.call(this);
    }

    @Override
    public String toString() {
        return "GenomicLocalResources{" +
                "hg19=" + hg19.get() +
                ", hg38=" + hg38.get() +
                '}';
    }
}
