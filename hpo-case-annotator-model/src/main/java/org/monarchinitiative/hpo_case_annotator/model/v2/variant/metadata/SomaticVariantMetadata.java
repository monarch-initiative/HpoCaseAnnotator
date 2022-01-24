package org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata;

import java.util.Objects;

public class SomaticVariantMetadata extends VariantMetadata {

    private final String regulator;
    private final String reporterRegulation;
    private final String reporterResidualActivity;
    private final String emsaGeneId;
    private final String otherChoices;
    private final int nPatients;
    private final int mPatients;

    private SomaticVariantMetadata(String snippet,
                                   String variantClass,
                                   String pathomechanism,
                                   boolean cosegregation,
                                   boolean comparability,
                                   String regulator,
                                   String reporterRegulation,
                                   String reporterResidualActivity,
                                   String emsaGeneId,
                                   String otherChoices,
                                   int nPatients,
                                   int mPatients) {
        super(VariantMetadataContext.SOMATIC, snippet, variantClass, pathomechanism, cosegregation, comparability);
        this.regulator = regulator;
        this.reporterRegulation = reporterRegulation;
        this.reporterResidualActivity = reporterResidualActivity;
        this.emsaGeneId = emsaGeneId;
        this.otherChoices = otherChoices;
        this.nPatients = nPatients;
        this.mPatients = mPatients;
    }

    public static SomaticVariantMetadata of(String snippet,
                                            String variantClass,
                                            String pathomechanism,
                                            boolean cosegregation,
                                            boolean comparability,
                                            String regulator,
                                            String reporterRegulation,
                                            String reporterResidualActivity,
                                            String emsaGeneId,
                                            String otherChoices,
                                            int nPatients,
                                            int mPatients) {
        return new SomaticVariantMetadata(snippet,
                variantClass,
                pathomechanism,
                cosegregation,
                comparability,
                regulator,
                reporterRegulation,
                reporterResidualActivity,
                emsaGeneId,
                otherChoices,
                nPatients,
                mPatients);
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

    public String getEmsaGeneId() {
        return emsaGeneId;
    }

    public String getOtherChoices() {
        return otherChoices;
    }

    public int getnPatients() {
        return nPatients;
    }

    public int getmPatients() {
        return mPatients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SomaticVariantMetadata that = (SomaticVariantMetadata) o;
        return nPatients == that.nPatients && mPatients == that.mPatients && Objects.equals(regulator, that.regulator) && Objects.equals(reporterRegulation, that.reporterRegulation) && Objects.equals(reporterResidualActivity, that.reporterResidualActivity) && Objects.equals(emsaGeneId, that.emsaGeneId) && Objects.equals(otherChoices, that.otherChoices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), regulator, reporterRegulation, reporterResidualActivity, emsaGeneId, otherChoices, nPatients, mPatients);
    }

    @Override
    public String toString() {
        return "SomaticVariantMetadata{" +
                "regulator='" + regulator + '\'' +
                ", reporterRegulation='" + reporterRegulation + '\'' +
                ", reporterResidualActivity='" + reporterResidualActivity + '\'' +
                ", emsaGeneId='" + emsaGeneId + '\'' +
                ", otherChoices='" + otherChoices + '\'' +
                ", nPatients=" + nPatients +
                ", mPatients=" + mPatients +
                "} " + super.toString();
    }
}
