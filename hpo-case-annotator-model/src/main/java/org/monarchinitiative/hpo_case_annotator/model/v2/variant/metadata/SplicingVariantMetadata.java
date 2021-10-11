package org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata;

import java.util.Objects;

public class SplicingVariantMetadata extends VariantMetadata {

    /*
     TODO - add:
     crypticPosition
     cryptic splice site type
     cryptic splice site snippet
     */

    private final boolean minigeneValidation;
    private final boolean siteDirectedMutagenesisValidation;
    private final boolean rtPcrValidation;
    private final boolean srProteinOverexpressionValidation;
    private final boolean srProteinKnockdownValidation;
    private final boolean cDnaSequencingValidation;
    private final boolean pcrValidation;
    private final boolean mutOfWtSpliceSiteValidation;
    private final boolean otherValidation;

    private SplicingVariantMetadata(String snippet,
                                    String variantClass,
                                    String pathomechanism,
                                    boolean cosegregation,
                                    boolean comparability,
                                    boolean minigeneValidation,
                                    boolean siteDirectedMutagenesisValidation,
                                    boolean rtPcrValidation,
                                    boolean srProteinOverexpressionValidation,
                                    boolean srProteinKnockdownValidation,
                                    boolean cDnaSequencingValidation,
                                    boolean pcrValidation,
                                    boolean mutOfWtSpliceSiteValidation,
                                    boolean otherValidation) {
        super(VariantMetadataContext.SPLICING, snippet, variantClass, pathomechanism, cosegregation, comparability);

        this.minigeneValidation = minigeneValidation;
        this.siteDirectedMutagenesisValidation = siteDirectedMutagenesisValidation;
        this.rtPcrValidation = rtPcrValidation;
        this.srProteinOverexpressionValidation = srProteinOverexpressionValidation;
        this.srProteinKnockdownValidation = srProteinKnockdownValidation;
        this.cDnaSequencingValidation = cDnaSequencingValidation;
        this.pcrValidation = pcrValidation;
        this.mutOfWtSpliceSiteValidation = mutOfWtSpliceSiteValidation;
        this.otherValidation = otherValidation;
    }

    public static SplicingVariantMetadata of(String snippet,
                                             String variantClass,
                                             String pathomechanism,
                                             boolean cosegregation,
                                             boolean comparability,
                                             boolean minigeneValidation,
                                             boolean siteDirectedMutagenesisValidation,
                                             boolean rtPcrValidation,
                                             boolean srProteinOverexpressionValidation,
                                             boolean srProteinKnockdownValidation,
                                             boolean cDnaSequencingValidation,
                                             boolean pcrValidation,
                                             boolean mutOfWtSpliceSiteValidation,
                                             boolean otherValidation) {
        return new SplicingVariantMetadata(snippet,
                variantClass,
                pathomechanism,
                cosegregation,
                comparability,
                minigeneValidation,
                siteDirectedMutagenesisValidation,
                rtPcrValidation,
                srProteinOverexpressionValidation,
                srProteinKnockdownValidation,
                cDnaSequencingValidation,
                pcrValidation,
                mutOfWtSpliceSiteValidation,
                otherValidation);
    }

    public boolean isMinigeneValidation() {
        return minigeneValidation;
    }

    public boolean isSiteDirectedMutagenesisValidation() {
        return siteDirectedMutagenesisValidation;
    }

    public boolean isRtPcrValidation() {
        return rtPcrValidation;
    }

    public boolean isSrProteinOverexpressionValidation() {
        return srProteinOverexpressionValidation;
    }

    public boolean isSrProteinKnockdownValidation() {
        return srProteinKnockdownValidation;
    }

    public boolean iscDnaSequencingValidation() {
        return cDnaSequencingValidation;
    }

    public boolean isPcrValidation() {
        return pcrValidation;
    }

    public boolean isMutOfWtSpliceSiteValidation() {
        return mutOfWtSpliceSiteValidation;
    }

    public boolean isOtherValidation() {
        return otherValidation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SplicingVariantMetadata that = (SplicingVariantMetadata) o;
        return minigeneValidation == that.minigeneValidation && siteDirectedMutagenesisValidation == that.siteDirectedMutagenesisValidation && rtPcrValidation == that.rtPcrValidation && srProteinOverexpressionValidation == that.srProteinOverexpressionValidation && srProteinKnockdownValidation == that.srProteinKnockdownValidation && cDnaSequencingValidation == that.cDnaSequencingValidation && pcrValidation == that.pcrValidation && mutOfWtSpliceSiteValidation == that.mutOfWtSpliceSiteValidation && otherValidation == that.otherValidation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), minigeneValidation, siteDirectedMutagenesisValidation, rtPcrValidation, srProteinOverexpressionValidation, srProteinKnockdownValidation, cDnaSequencingValidation, pcrValidation, mutOfWtSpliceSiteValidation, otherValidation);
    }

    @Override
    public String toString() {
        return "SplicingVariantMetadata{" +
                "minigeneValidation=" + minigeneValidation +
                ", siteDirectedMutagenesisValidation=" + siteDirectedMutagenesisValidation +
                ", rtPcrValidation=" + rtPcrValidation +
                ", srProteinOverexpressionValidation=" + srProteinOverexpressionValidation +
                ", srProteinKnockdownValidation=" + srProteinKnockdownValidation +
                ", cDnaSequencingValidation=" + cDnaSequencingValidation +
                ", pcrValidation=" + pcrValidation +
                ", mutOfWtSpliceSiteValidation=" + mutOfWtSpliceSiteValidation +
                ", otherValidation=" + otherValidation +
                "} " + super.toString();
    }
}
