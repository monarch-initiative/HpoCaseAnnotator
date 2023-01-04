package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.hpo_case_annotator.observable.deep.ObservableItem;
import org.monarchinitiative.svart.*;

import java.util.Optional;
import java.util.stream.Stream;

public class ObservableCuratedVariant extends ObservableItem implements CuratedVariant {

    public static final Strand VCF_SEQUENCE_SYMBOLIC_VARIANT_STRAND = Strand.POSITIVE;
    public static final Callback<ObservableCuratedVariant, Observable[]> EXTRACTOR = obs -> new Observable[]{
            obs.variantNotation,
            obs.genomicAssembly,
            obs.contig,
            obs.start,
            obs.startCi,
            obs.end,
            obs.endCi,
            obs.id,
            obs.ref,
            obs.alt,
            obs.variantType,
            obs.changeLength,
            obs.left,
            obs.right,
            obs.variantMetadata};

    private final ObjectProperty<VariantNotation> variantNotation = new SimpleObjectProperty<>(this, "variantNotation");
    private final StringProperty genomicAssembly = new SimpleStringProperty(this, "genomicAssembly");
    private final ObjectProperty<Contig> contig = new SimpleObjectProperty<>(this, "contig");
    private final IntegerProperty start = new SimpleIntegerProperty(this, "start");
    private final ObjectProperty<ConfidenceInterval> startCi = new SimpleObjectProperty<>(this, "startCi");
    private final IntegerProperty end = new SimpleIntegerProperty(this, "end");
    private final ObjectProperty<ConfidenceInterval> endCi = new SimpleObjectProperty<>(this, "endCi");
    // Serves as variant ID for sequence and symbolic notations, and as event ID for breakends.
    private final StringProperty id = new SimpleStringProperty(this, "id");
    private final StringProperty ref = new SimpleStringProperty(this, "ref");
    private final StringProperty alt = new SimpleStringProperty(this, "alt");
    private final ObjectProperty<VariantType> variantType = new SimpleObjectProperty<>(this, "variantType");
    private final IntegerProperty changeLength = new SimpleIntegerProperty(this, "changeLength");

    private final ObjectProperty<ObservableGenomicBreakend> left = new SimpleObjectProperty<>(this, "left");
    private final ObjectProperty<ObservableGenomicBreakend> right = new SimpleObjectProperty<>(this, "right");
    private final ObjectProperty<VariantMetadata> variantMetadata = new SimpleObjectProperty<>(this, "variantMetadata", VariantMetadata.emptyMetadata());

    public ObservableCuratedVariant() {
    }

    public ObservableCuratedVariant(CuratedVariant curatedVariant) {
        if (curatedVariant != null) {
            // (*) GenomicAssembly
            genomicAssembly.set(curatedVariant.getGenomicAssembly());

            // (*) GenomicVariant
            curatedVariant.getVariant().ifPresent(gv -> {
                // Notation
                if (gv.isBreakend()) {
                    variantNotation.set(VariantNotation.BREAKEND);
                    GenomicBreakendVariant gb = (GenomicBreakendVariant) gv;
                    id.set(gb.eventId());
                    left.set(new ObservableGenomicBreakend(gb.left()));
                    right.set(new ObservableGenomicBreakend(gb.right()));
                } else {
                    Contig contig = gv.contig();
                    if (contig != null)
                        this.contig.set(contig);

                    if (gv.isSymbolic()) {
                        variantNotation.set(VariantNotation.SYMBOLIC);

                        start.set(gv.startWithCoordinateSystem(VCF_COORDINATE_SYSTEM));
                        end.set(gv.endWithCoordinateSystem(VCF_COORDINATE_SYSTEM));
                        changeLength.set(gv.changeLength());
                    } else {
                        variantNotation.set(VariantNotation.SEQUENCE);
                        start.set(gv.startWithCoordinateSystem(VCF_COORDINATE_SYSTEM));
                    }

                    id.set(gv.id());
                }

                // Common for all notations.
                ref.set(gv.ref());
                alt.set(gv.alt());
                variantType.set(gv.variantType());
            });

            // (*) Metadata
            VariantMetadata metadata = curatedVariant.getVariantMetadata();
            if (metadata != null)
                variantMetadata.set(metadata);
        }
    }

    public VariantNotation getVariantNotation() {
        return variantNotation.get();
    }

    public ObjectProperty<VariantNotation> variantNotationProperty() {
        return variantNotation;
    }

    public void setVariantNotation(VariantNotation variantNotation) {
        this.variantNotation.set(variantNotation);
    }

    public void setGenomicAssembly(String genomicAssembly) {
        this.genomicAssembly.set(genomicAssembly);
    }

    @Override
    public String getGenomicAssembly() {
        return genomicAssembly.get();
    }

    public StringProperty genomicAssemblyProperty() {
        return genomicAssembly;
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

    public int getStart() {
        return start.get();
    }

    public IntegerProperty startProperty() {
        return start;
    }

    public void setStart(int start) {
        this.start.set(start);
    }

    public ConfidenceInterval getStartCi() {
        return startCi.get();
    }

    public ObjectProperty<ConfidenceInterval> startCiProperty() {
        return startCi;
    }

    public void setStartCi(ConfidenceInterval startCi) {
        this.startCi.set(startCi);
    }

    public int getEnd() {
        return end.get();
    }

    public IntegerProperty endProperty() {
        return end;
    }

    public void setEnd(int end) {
        this.end.set(end);
    }

    public ConfidenceInterval getEndCi() {
        return endCi.get();
    }

    public ObjectProperty<ConfidenceInterval> endCiProperty() {
        return endCi;
    }

    public void setEndCi(ConfidenceInterval endCi) {
        this.endCi.set(endCi);
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

    public String getRef() {
        return ref.get();
    }

    public StringProperty refProperty() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref.set(ref);
    }

    public String getAlt() {
        return alt.get();
    }

    public StringProperty altProperty() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt.set(alt);
    }

    public VariantType getVariantType() {
        return variantType.get();
    }

    public void setVariantType(VariantType variantType) {
        this.variantType.set(variantType);
    }

    public ObjectProperty<VariantType> variantTypeProperty() {
        return variantType;
    }

    public int getChangeLength() {
        return changeLength.get();
    }

    public IntegerProperty changeLengthProperty() {
        return changeLength;
    }

    public void setChangeLength(int changeLength) {
        this.changeLength.set(changeLength);
    }

    @Override
    public Optional<GenomicVariant> getVariant() {
        VariantNotation notation = variantNotation.get();
        return switch (notation) {
            case SEQUENCE -> {
                try {
                    yield Optional.of(
                            GenomicVariant.of(
                                    contig.get(),
                                    id.get(),
                                    VCF_SEQUENCE_SYMBOLIC_VARIANT_STRAND,
                                    VCF_COORDINATE_SYSTEM,
                                    start.get(),
                                    ref.get(),
                                    alt.get()
                            )
                    );
                } catch (Exception e) { // TODO - narrow down exception, or even throw the exception if necessary
                    yield Optional.empty();
                }
            }
            case SYMBOLIC -> {
                try {
                    Coordinates coordinates = Coordinates.of(VCF_COORDINATE_SYSTEM, start.get(), end.get());
                    yield Optional.of(
                            GenomicVariant.of(
                                    contig.get(),
                                    id.get(),
                                    VCF_SEQUENCE_SYMBOLIC_VARIANT_STRAND,
                                    coordinates,
                                    ref.get(),
                                    alt.get()
                            )
                    );
                } catch (Exception e) { // TODO - narrow down exception, or even throw the exception if necessary
                    yield Optional.empty();
                }
            }
            case BREAKEND -> {
                try {
                    GenomicBreakend l = left.get().getBreakend().get();
                    GenomicBreakend r = right.get().getBreakend().get();
                    GenomicVariant gv = GenomicVariant.of(id.get(), l, r, ref.get(), alt.get());
                    yield Optional.of(gv);
                } catch (Exception e) { // TODO - narrow down exception, or even throw the exception if necessary
                    yield Optional.empty();
                }

            }
        };
    }

    public void setVariantMetadata(VariantMetadata variantMetadata) {
        this.variantMetadata.set(variantMetadata);
    }

    public ObservableGenomicBreakend getLeft() {
        return left.get();
    }

    public ObjectProperty<ObservableGenomicBreakend> leftProperty() {
        return left;
    }

    public void setLeft(ObservableGenomicBreakend left) {
        this.left.set(left);
    }

    public ObservableGenomicBreakend getRight() {
        return right.get();
    }

    public ObjectProperty<ObservableGenomicBreakend> rightProperty() {
        return right;
    }

    public void setRight(ObservableGenomicBreakend right) {
        this.right.set(right);
    }

    @Override
    public VariantMetadata getVariantMetadata() {
        return variantMetadata.get();
    }

    public ObjectProperty<VariantMetadata> variantMetadataProperty() {
        return variantMetadata;
    }

    @Override
    public Stream<Observable> observables() {
        return Stream.of(EXTRACTOR.call(this));
    }

    @Override
    public String toString() {
        return "ObservableCuratedVariant{" +
                "variantNotation=" + variantNotation.get() +
                ", genomicAssembly=" + genomicAssembly.get() +
                ", contig=" + contig.get() +
                ", start=" + start.get() +
                ", startCi=" + startCi.get() +
                ", end=" + end.get() +
                ", endCi=" + endCi.get() +
                ", id=" + id.get() +
                ", ref=" + ref.get() +
                ", alt=" + alt.get() +
                ", variantType=" + variantType.get() +
                ", changeLength=" + changeLength.get() +
                ", left=" + left.get() +
                ", right=" + right.get() +
                ", variantMetadata=" + variantMetadata.get() +
                '}';
    }
}
