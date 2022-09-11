package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.svart.Contig;
import org.monarchinitiative.svart.GenomicBreakend;
import org.monarchinitiative.svart.Strand;

import java.util.Optional;
import java.util.stream.Stream;

public class ObservableGenomicBreakend extends ObservableItem {

    public static final Callback<ObservableGenomicBreakend, Observable[]> EXTRACTOR = obs -> new Observable[]{obs.id, obs.contig, obs.pos, obs.strand};

    private final StringProperty id = new SimpleStringProperty(this, "id");
    private final ObjectProperty<Contig> contig = new SimpleObjectProperty<>(this, "contig");
    // We store the position of the last base upstream of the break - the last base of the upstream DNA endpoint.
    private final IntegerProperty pos = new SimpleIntegerProperty(this, "pos");
    private final ObjectProperty<Strand> strand = new SimpleObjectProperty<>(this, "strand");

    public ObservableGenomicBreakend() {
        strand.set(Strand.POSITIVE);
    }

    public ObservableGenomicBreakend(GenomicBreakend breakend) {
        if (breakend != null) {
            id.set(breakend.id());
            contig.set(breakend.contig());
            pos.set(breakend.endWithCoordinateSystem(CuratedVariant.VCF_COORDINATE_SYSTEM));
            strand.set(breakend.strand());
        }
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public Contig getContig() {
        return contig.get();
    }

    public ObjectProperty<Contig> contigProperty() {
        return contig;
    }

    public void setContig(Contig contig) {
        this.contig.set(contig);
    }

    public int getPos() {
        return pos.get();
    }

    public IntegerProperty posProperty() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos.set(pos);
    }

    public Strand getStrand() {
        return strand.get();
    }

    public ObjectProperty<Strand> strandProperty() {
        return strand;
    }

    public void setStrand(Strand strand) {
        this.strand.set(strand);
    }

    public Optional<GenomicBreakend> getBreakend() {
        try {
            return Optional.of(
                    GenomicBreakend.of(
                            contig.get(),
                            id.get(),
                            strand.get(),
                            CuratedVariant.VCF_COORDINATE_SYSTEM,
                            pos.get(),
                            pos.get() // TODO - implement
                    )
            );
        } catch (Exception e) { // TODO - narrow down exception, or even throw the exception if necessary
            return Optional.empty();
        }
    }

    @Override
    public Stream<Observable> observables() {
        return Stream.of(EXTRACTOR.call(this));
    }

    @Override
    public String toString() {
        return "ObservableGenomicBreakend{" +
                "id=" + id.get() +
                ", contig=" + contig.get() +
                ", start=" + pos.get() +
                ", strand=" + strand.get() +
                '}';
    }
}
