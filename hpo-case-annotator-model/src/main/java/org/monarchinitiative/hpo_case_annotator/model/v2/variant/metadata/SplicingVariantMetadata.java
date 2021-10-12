package org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata;

import java.util.Objects;

public class SplicingVariantMetadata extends VariantMetadata {

    private final int crypticPosition;
    private final CrypticSpliceSiteType crypticSpliceSiteType;
    private final String crypticSpliceSiteSnippet;
    private final String consequence;
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
                                    int crypticPosition,
                                    CrypticSpliceSiteType crypticSpliceSiteType,
                                    String crypticSpliceSiteSnippet,
                                    String consequence,
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
        this.crypticPosition = crypticPosition;
        this.crypticSpliceSiteType = crypticSpliceSiteType;
        this.crypticSpliceSiteSnippet = crypticSpliceSiteSnippet;
        this.consequence = consequence;
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
                                             int crypticPosition,
                                             CrypticSpliceSiteType crypticSpliceSiteType,
                                             String crypticSpliceSiteSnippet,
                                             String consequence,
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
                crypticPosition,
                crypticSpliceSiteType,
                crypticSpliceSiteSnippet,
                consequence,
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

    public int getCrypticPosition() {
        return crypticPosition;
    }

    public CrypticSpliceSiteType getCrypticSpliceSiteType() {
        return crypticSpliceSiteType;
    }

    public String getCrypticSpliceSiteSnippet() {
        return crypticSpliceSiteSnippet;
    }

    public String getConsequence() {
        return consequence;
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
        return crypticPosition == that.crypticPosition && minigeneValidation == that.minigeneValidation && siteDirectedMutagenesisValidation == that.siteDirectedMutagenesisValidation && rtPcrValidation == that.rtPcrValidation && srProteinOverexpressionValidation == that.srProteinOverexpressionValidation && srProteinKnockdownValidation == that.srProteinKnockdownValidation && cDnaSequencingValidation == that.cDnaSequencingValidation && pcrValidation == that.pcrValidation && mutOfWtSpliceSiteValidation == that.mutOfWtSpliceSiteValidation && otherValidation == that.otherValidation && crypticSpliceSiteType == that.crypticSpliceSiteType && Objects.equals(crypticSpliceSiteSnippet, that.crypticSpliceSiteSnippet) && Objects.equals(consequence, that.consequence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), crypticPosition, crypticSpliceSiteType, crypticSpliceSiteSnippet, consequence, minigeneValidation, siteDirectedMutagenesisValidation, rtPcrValidation, srProteinOverexpressionValidation, srProteinKnockdownValidation, cDnaSequencingValidation, pcrValidation, mutOfWtSpliceSiteValidation, otherValidation);
    }

    @Override
    public String toString() {
        return "SplicingVariantMetadata{" +
                "crypticPosition=" + crypticPosition +
                ", crypticSpliceSiteType=" + crypticSpliceSiteType +
                ", crypticSpliceSiteSnippet='" + crypticSpliceSiteSnippet + '\'' +
                ", consequence='" + consequence + '\'' +
                ", minigeneValidation=" + minigeneValidation +
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

    public enum CrypticSpliceSiteType {
        FIVE_PRIME,
        THREE_PRIME,
        NO_CSS
    }
}
