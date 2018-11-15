package org.monarchinitiative.hpo_case_annotator.model.xml_model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class SplicingVariant extends Variant {

    private final VariantMode variantMode = VariantMode.SPLICING;

    /**
     * Validation property - required for each variant. Validation is specific for a given {@link VariantMode}.
     */
    private ObjectProperty<SplicingValidation> splicingValidation = new SimpleObjectProperty<>(this, "splicingValidation", new SplicingValidation());

    /* Consequence of nucleotide change. Result of mutation, e.g. Exon skipping or cryptic splice site creation */
    private StringProperty consequence = new SimpleStringProperty(this, "consequence", "");

    /* This is 1-based coordinate of novel, variant-induced cryptic splice site.
     * Either it is the first base of truncated exon due to the novel 3'SS or
     * last base of truncated exon due to the novel 5'SS. */
    private StringProperty crypticPosition = new SimpleStringProperty(this, "crypticPosition", "");

    /* Type of Cryptic Splice Site - either new 5'SS or 3'SS. */
    private StringProperty crypticSpliceSiteType = new SimpleStringProperty(this, "crypticSpliceSiteType", "");

    /* Sequence snippet describing new splice site, e.g. CCTCGACGAGC]GTGCGA. Bracket symbol denotes
     * either end ']' of exonic sequence in case of novel 5'SS,
     * or start '[' of exonic sequence in case of new 3'SS */
    private StringProperty crypticSpliceSiteSnippet = new SimpleStringProperty(this, "crypticSpliceSiteSnippet", "");


    public final VariantMode getVariantMode() {
        return this.variantMode;
    }


    public final void setVariantMode(VariantMode newVariantMode) {
        throw new UnsupportedOperationException("Unable to change readonly property of variantMode");
    }


    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getVariantMode().hashCode();
        result = 31 * result + (getSplicingValidation() != null ? getSplicingValidation().hashCode() : 0);
        result = 31 * result + (getConsequence() != null ? getConsequence().hashCode() : 0);
        result = 31 * result + (getCrypticPosition() != null ? getCrypticPosition().hashCode() : 0);
        result = 31 * result + (getCrypticSpliceSiteType() != null ? getCrypticSpliceSiteType().hashCode() : 0);
        result = 31 * result + (getCrypticSpliceSiteSnippet() != null ? getCrypticSpliceSiteSnippet().hashCode() : 0);
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SplicingVariant that = (SplicingVariant) o;

        if (getVariantMode() != that.getVariantMode()) return false;
        if (getSplicingValidation() != null ? !getSplicingValidation().equals(that.getSplicingValidation()) : that.getSplicingValidation() != null)
            return false;
        if (getConsequence() != null ? !getConsequence().equals(that.getConsequence()) : that.getConsequence() != null)
            return false;
        if (getCrypticPosition() != null ? !getCrypticPosition().equals(that.getCrypticPosition()) : that.getCrypticPosition() != null)
            return false;
        if (getCrypticSpliceSiteType() != null ? !getCrypticSpliceSiteType().equals(that.getCrypticSpliceSiteType()) : that.getCrypticSpliceSiteType() != null)
            return false;
        return getCrypticSpliceSiteSnippet() != null ? getCrypticSpliceSiteSnippet().equals(that.getCrypticSpliceSiteSnippet()) : that.getCrypticSpliceSiteSnippet() == null;
    }


    public final SplicingValidation getSplicingValidation() {
        return splicingValidation.get();
    }


    public final void setSplicingValidation(SplicingValidation newSplicingValidationProperty) {
        splicingValidation.set(newSplicingValidationProperty);
    }


    public ObjectProperty<SplicingValidation> splicingValidationProperty() {
        return splicingValidation;
    }


    public final String getConsequence() {
        return consequence.get();
    }


    public final void setConsequence(String newConsequence) {
        consequence.set(newConsequence);
    }


    public StringProperty consequenceProperty() {
        return consequence;
    }


    public final String getCrypticPosition() {
        return crypticPosition.get();
    }


    public final void setCrypticPosition(String newCrypticPosition) {
        crypticPosition.set(newCrypticPosition);
    }


    public StringProperty crypticPositionProperty() {
        return crypticPosition;
    }


    public final String getCrypticSpliceSiteType() {
        return crypticSpliceSiteType.get();
    }


    public final void setCrypticSpliceSiteType(String newCrypticSpliceSiteType) {
        crypticSpliceSiteType.set(newCrypticSpliceSiteType);
    }


    public StringProperty crypticSpliceSiteTypeProperty() {
        return crypticSpliceSiteType;
    }


    public final String getCrypticSpliceSiteSnippet() {
        return crypticSpliceSiteSnippet.get();
    }


    public final void setCrypticSpliceSiteSnippet(String newCrypticSpliceSiteSnippet) {
        crypticSpliceSiteSnippet.set(newCrypticSpliceSiteSnippet);
    }


    public StringProperty crypticSpliceSiteSnippetProperty() {
        return crypticSpliceSiteSnippet;
    }
}
