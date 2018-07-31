package org.monarchinitiative.hpo_case_annotator.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * This class contains attributes pertaining to validation of variants which lead to abberant mRNA splicing. Currently
 * we are interested in: <ul> <li>Type of <em>in-vitro</em> or <em>in-vivo</em> asssay. E.g. minigene, site directed
 * mutagenesis, RT-PCR, SR protein overexpression or knockdown analysis</li> </ul>
 *
 * @author Daniel Danis
 */
public final class SplicingValidation {

    // these are the checkboxes in SplicingVariantController

    private BooleanProperty minigeneValidation = new SimpleBooleanProperty(this, "minigeneValidation", false);

    private BooleanProperty siteDirectedMutagenesisValidation = new SimpleBooleanProperty(this,
            "siteDirectedMutagenesisValidation", false);

    private BooleanProperty rtPCRValidation = new SimpleBooleanProperty(this, "rtPCRValidation", false);

    private BooleanProperty srProteinOverexpressionValidation = new SimpleBooleanProperty(this,
            "srProteinOverexpressionValidation", false);

    private BooleanProperty srProteinKnockdownValidation = new SimpleBooleanProperty(this,
            "srProteinKnockdownValidation", false);

    private BooleanProperty cDNASequencingValidation = new SimpleBooleanProperty(this, "cDNASequencingValidation", false);

    private BooleanProperty pcrValidation = new SimpleBooleanProperty(this, "pcrValidation", false);

    private BooleanProperty mutOfWTSpliceSiteValidation = new SimpleBooleanProperty(this,
            "mutOfWTSpliceSiteValidation", false);

    private BooleanProperty otherValidation = new SimpleBooleanProperty(this, "otherValidation", false);


    public SplicingValidation() {
        // no-op
    }


    public SplicingValidation(boolean minigeneValidation,
                              boolean siteDirectedMutagenesisValidation,
                              boolean rtPCRValidation,
                              boolean srProteinOverexpressionValidation,
                              boolean srProteinKnockdownValidation,
                              boolean cDNASequencingValidation,
                              boolean pcrValidation,
                              boolean mutOfWTSpliceSiteValidation,
                              boolean otherValidation) {
        this.minigeneValidation.set(minigeneValidation);
        this.siteDirectedMutagenesisValidation.set(siteDirectedMutagenesisValidation);
        this.rtPCRValidation.set(rtPCRValidation);
        this.srProteinOverexpressionValidation.set(srProteinOverexpressionValidation);
        this.srProteinKnockdownValidation.set(srProteinKnockdownValidation);
        this.cDNASequencingValidation.set(cDNASequencingValidation);
        this.pcrValidation.set(pcrValidation);
        this.mutOfWTSpliceSiteValidation.set(mutOfWTSpliceSiteValidation);
        this.otherValidation.set(otherValidation);
    }


    public boolean isCDNASequencingValidation() {
        return cDNASequencingValidation.get();
    }


    public void setCDNASequencingValidation(boolean cDNASequencingValidation) {
        this.cDNASequencingValidation.set(cDNASequencingValidation);
    }


    public BooleanProperty cDNASequencingValidationProperty() {
        return cDNASequencingValidation;
    }


    public boolean isMutOfWTSpliceSiteValidation() {
        return mutOfWTSpliceSiteValidation.get();
    }


    public void setMutOfWTSpliceSiteValidation(boolean mutOfWTSpliceSiteValidation) {
        this.mutOfWTSpliceSiteValidation.set(mutOfWTSpliceSiteValidation);
    }


    public BooleanProperty mutOfWTSpliceSiteValidationProperty() {
        return mutOfWTSpliceSiteValidation;
    }


    public boolean isOtherValidation() {
        return otherValidation.get();
    }


    public void setOtherValidation(boolean otherValidation) {
        this.otherValidation.set(otherValidation);
    }


    public BooleanProperty otherValidationProperty() {
        return otherValidation;
    }


    public boolean isMinigeneValidation() {
        return minigeneValidation.get();
    }


    public void setMinigeneValidation(boolean minigeneValidation) {
        this.minigeneValidation.set(minigeneValidation);
    }


    public BooleanProperty minigeneValidationProperty() {
        return minigeneValidation;
    }


    public boolean isSiteDirectedMutagenesisValidation() {
        return siteDirectedMutagenesisValidation.get();
    }


    public void setSiteDirectedMutagenesisValidation(boolean siteDirectedMutagenesisValidation) {
        this.siteDirectedMutagenesisValidation.set(siteDirectedMutagenesisValidation);
    }


    public BooleanProperty siteDirectedMutagenesisValidationProperty() {
        return siteDirectedMutagenesisValidation;
    }


    public boolean isRtPCRValidation() {
        return rtPCRValidation.get();
    }


    public void setRtPCRValidation(boolean rtPCRValidation) {
        this.rtPCRValidation.set(rtPCRValidation);
    }


    public BooleanProperty rtPCRValidationProperty() {
        return rtPCRValidation;
    }


    public boolean isSrProteinOverexpressionValidation() {
        return srProteinOverexpressionValidation.get();
    }


    public void setSrProteinOverexpressionValidation(boolean srProteinOverexpressionValidation) {
        this.srProteinOverexpressionValidation.set(srProteinOverexpressionValidation);
    }


    public BooleanProperty srProteinOverexpressionValidationProperty() {
        return srProteinOverexpressionValidation;
    }


    public boolean isSrProteinKnockdownValidation() {
        return srProteinKnockdownValidation.get();
    }


    public void setSrProteinKnockdownValidation(boolean srProteinKnockdownValidation) {
        this.srProteinKnockdownValidation.set(srProteinKnockdownValidation);
    }


    public BooleanProperty srProteinKnockdownValidationProperty() {
        return srProteinKnockdownValidation;
    }


    public boolean isPcrValidation() {
        return pcrValidation.get();
    }


    public void setPcrValidation(boolean pcrValidation) {
        this.pcrValidation.set(pcrValidation);
    }


    public BooleanProperty pcrValidationProperty() {
        return pcrValidation;
    }


    @Override
    public int hashCode() {
        int result = (isMinigeneValidation() ? 1 : 0);
        result = 31 * result + (isSiteDirectedMutagenesisValidation() ? 1 : 0);
        result = 31 * result + (isRtPCRValidation() ? 1 : 0);
        result = 31 * result + (isSrProteinOverexpressionValidation() ? 1 : 0);
        result = 31 * result + (isSrProteinKnockdownValidation() ? 1 : 0);
        result = 31 * result + (isCDNASequencingValidation() ? 1 : 0);
        result = 31 * result + (isPcrValidation() ? 1 : 0);
        result = 31 * result + (isMutOfWTSpliceSiteValidation() ? 1 : 0);
        result = 31 * result + (isOtherValidation() ? 1 : 0);
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SplicingValidation that = (SplicingValidation) o;

        if (!isMinigeneValidation() == that.isMinigeneValidation()) return false;
        if (!isSiteDirectedMutagenesisValidation() == that.isSiteDirectedMutagenesisValidation()) return false;
        if (!isRtPCRValidation() == that.isRtPCRValidation()) return false;
        if (!isSrProteinOverexpressionValidation() == that.isSrProteinOverexpressionValidation()) return false;
        if (!isSrProteinKnockdownValidation() == that.isSrProteinKnockdownValidation()) return false;
        if (!isCDNASequencingValidation() == that.isCDNASequencingValidation()) return false;
        if (!isPcrValidation() == that.isPcrValidation()) return false;
        if (!isMutOfWTSpliceSiteValidation() == that.isMutOfWTSpliceSiteValidation()) return false;
        return isOtherValidation() == that.isOtherValidation();
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SplicingValidation{");
        sb.append("minigeneValidation=").append(minigeneValidation.get());
        sb.append(", siteDirectedMutagenesisValidation=").append(siteDirectedMutagenesisValidation.get());
        sb.append(", rtPCRValidation=").append(rtPCRValidation.get());
        sb.append(", srProteinOverexpressionValidation=").append(srProteinOverexpressionValidation.get());
        sb.append(", srProteinKnockdownValidation=").append(srProteinKnockdownValidation.get());
        sb.append(", cDNASequencingValidation=").append(cDNASequencingValidation.get());
        sb.append(", pcrValidation=").append(pcrValidation.get());
        sb.append(", mutOfWTSpliceSiteValidation=").append(mutOfWTSpliceSiteValidation.get());
        sb.append(", otherValidation=").append(otherValidation.get());
        sb.append('}');
        return sb.toString();
    }
}
