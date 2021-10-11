package org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata;

import java.util.Objects;

public class MendelianVariantMetadata extends VariantMetadata {

    private final String regulator;
    private final String reporterRegulation;
    private final String reporterResidualActivity;
    private final boolean emsaValidationPerformed;
    private final String emsaTfSymbol;
    private final String emsaGeneId;
    private final String otherChoices;
    private final String otherEffect;

    private MendelianVariantMetadata(String snippet,
                                     String variantClass,
                                     String pathomechanism,
                                     boolean cosegregation,
                                     boolean comparability,
                                     String regulator,
                                     String reporterRegulation,
                                     String reporterResidualActivity,
                                     boolean emsaValidationPerformed,
                                     String emsaTfSymbol,
                                     String emsaGeneId,
                                     String otherChoices,
                                     String otherEffect) {
        super(VariantMetadataContext.MENDELIAN, snippet, variantClass, pathomechanism, cosegregation, comparability);
        this.regulator = regulator;
        this.reporterRegulation = reporterRegulation;
        this.reporterResidualActivity = reporterResidualActivity;
        this.emsaValidationPerformed = emsaValidationPerformed;
        this.emsaTfSymbol = emsaTfSymbol;
        this.emsaGeneId = emsaGeneId;
        this.otherChoices = otherChoices;
        this.otherEffect = otherEffect;
    }

    public static MendelianVariantMetadata of(String snippet,
                                              String variantClass,
                                              String pathomechanism,
                                              boolean cosegregation,
                                              boolean comparability,
                                              String regulator,
                                              String reporterRegulation,
                                              String reporterResidualActivity,
                                              boolean emsaValidationPerformed,
                                              String emsaTfSymbol,
                                              String emsaGeneId,
                                              String otherChoices,
                                              String otherEffect) {
        return new MendelianVariantMetadata(snippet,
                variantClass,
                pathomechanism,
                cosegregation,
                comparability,
                regulator,
                reporterRegulation,
                reporterResidualActivity,
                emsaValidationPerformed,
                emsaTfSymbol,
                emsaGeneId,
                otherChoices,
                otherEffect);
    }

    public String getRegulator() {
        return regulator;
    }

    public String getReporterRegulation() {
        return reporterRegulation;
    }

    public String getReporterResidualActivity() {
        return reporterResidualActivity;
    }

    public boolean isEmsaValidationPerformed() {
        return emsaValidationPerformed;
    }

    public String getEmsaTfSymbol() {
        return emsaTfSymbol;
    }

    public String getEmsaGeneId() {
        return emsaGeneId;
    }

    public String getOtherChoices() {
        return otherChoices;
    }

    public String getOtherEffect() {
        return otherEffect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MendelianVariantMetadata that = (MendelianVariantMetadata) o;
        return emsaValidationPerformed == that.emsaValidationPerformed && Objects.equals(regulator, that.regulator) && Objects.equals(reporterRegulation, that.reporterRegulation) && Objects.equals(reporterResidualActivity, that.reporterResidualActivity) && Objects.equals(emsaTfSymbol, that.emsaTfSymbol) && Objects.equals(emsaGeneId, that.emsaGeneId) && Objects.equals(otherChoices, that.otherChoices) && Objects.equals(otherEffect, that.otherEffect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), regulator, reporterRegulation, reporterResidualActivity, emsaValidationPerformed, emsaTfSymbol, emsaGeneId, otherChoices, otherEffect);
    }

    @Override
    public String toString() {
        return "MendelianVariantMetadata{" +
                "regulator='" + regulator + '\'' +
                ", reporterRegulation='" + reporterRegulation + '\'' +
                ", reporterResidualActivity='" + reporterResidualActivity + '\'' +
                ", emsaValidationPerformed=" + emsaValidationPerformed +
                ", emsaTfSymbol='" + emsaTfSymbol + '\'' +
                ", emsaGeneId='" + emsaGeneId + '\'' +
                ", otherChoices='" + otherChoices + '\'' +
                ", otherEffect='" + otherEffect + '\'' +
                "} " + super.toString();
    }
}
