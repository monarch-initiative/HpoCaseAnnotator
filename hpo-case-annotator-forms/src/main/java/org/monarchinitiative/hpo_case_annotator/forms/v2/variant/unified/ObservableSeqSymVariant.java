package org.monarchinitiative.hpo_case_annotator.forms.v2.variant.unified;

import javafx.beans.property.*;
import org.monarchinitiative.svart.Contig;
import org.monarchinitiative.svart.VariantType;

/**
 * Observable representation of sequence or symbolic variant, as described in Svart.
 */
public class ObservableSeqSymVariant {

    private final ObjectProperty<Contig> contig = new SimpleObjectProperty<>(this, "contig");
    private final StringProperty id = new SimpleStringProperty(this, "id");
    private final IntegerProperty start = new SimpleIntegerProperty(this, "start");
    private final IntegerProperty end = new SimpleIntegerProperty(this, "end");
    private final IntegerProperty changeLength = new SimpleIntegerProperty(this, "changeLength");
    private final StringProperty ref = new SimpleStringProperty(this, "ref");
    private final StringProperty alt = new SimpleStringProperty(this, "alt");
    private final ObjectProperty<VariantType> variantType = new SimpleObjectProperty<>(this, "variantType");

    // TODO - add metadata

    public Contig getContig() {
        return contig.get();
    }

    public void setContig(Contig contig) {
        this.contig.set(contig);
    }

    public ObjectProperty<Contig> contigProperty() {
        return contig;
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    public int getStart() {
        return start.get();
    }

    public void setStart(int start) {
        this.start.set(start);
    }

    public IntegerProperty startProperty() {
        return start;
    }

    public int getEnd() {
        return end.get();
    }

    public void setEnd(int end) {
        this.end.set(end);
    }

    public IntegerProperty endProperty() {
        return end;
    }

    public int getChangeLength() {
        return changeLength.get();
    }

    public void setChangeLength(int changeLength) {
        this.changeLength.set(changeLength);
    }

    public IntegerProperty changeLengthProperty() {
        return changeLength;
    }

    public String getRef() {
        return ref.get();
    }

    public void setRef(String ref) {
        this.ref.set(ref);
    }

    public StringProperty refProperty() {
        return ref;
    }

    public String getAlt() {
        return alt.get();
    }

    public void setAlt(String alt) {
        this.alt.set(alt);
    }

    public StringProperty altProperty() {
        return alt;
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
}
