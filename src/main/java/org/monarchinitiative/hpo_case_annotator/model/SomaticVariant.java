package org.monarchinitiative.hpo_case_annotator.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class contains attributes pertaining to validation of variants which cause Somatic/Cancer disorders.
 * <p>Currently we are interested in these attributes: <ul> <li>Regulator</li> <li>Reporter regulation - up/down/no</li>
 * <li>Reporter residual activity</li> <li>EMSA validation - performed or not</li> <li>EMSA transcription factor
 * symbol</li> <li>EMSA gene ID</li> <li>N & M patients - characteristics of cohort of cancer patients</li> <li>Other
 * effect - no, up, down or demonstrated</li> <li>Other effect - specification</li> </ul>
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class SomaticVariant extends Variant {

    private final VariantMode variantMode = VariantMode.SOMATIC;

    /* Regulator */
    private StringProperty regulator = new SimpleStringProperty(this, "regulator");

    /* Reporter combobox - choices {up, down, no} */
    private StringProperty reporterRegulation = new SimpleStringProperty(this, "reporterRegulation");

    /* Reporter residual activity text field */
    private StringProperty reporterResidualActivity = new SimpleStringProperty(this, "reporterResidualActivity");

    /* EMSA combobox - choices {yes, no} */
    private StringProperty emsaValidationPerformed = new SimpleStringProperty(this, "emsaValidationPerformed");

    /* EMSA TF symbol text field */
    private StringProperty emsaTFSymbol = new SimpleStringProperty(this, "emsaTFSymbol");

    /* EMSA Gene ID text field */
    private StringProperty emsaGeneId = new SimpleStringProperty(this, "emsaGeneId");

    /* */
    private StringProperty nPatients = new SimpleStringProperty(this, "nPatients");

    /* */
    private StringProperty mPatients = new SimpleStringProperty(this, "mPatients");

    /* Other effect - {no, up, down, demonstrated} */
    private StringProperty otherChoices = new SimpleStringProperty(this, "otherChoices");

    /* Other effect - more verbose choices */
    private StringProperty otherEffect = new SimpleStringProperty(this, "otherEffect");


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
        result = 31 * result + (getRegulator() != null ? getRegulator().hashCode() : 0);
        result = 31 * result + (getReporterRegulation() != null ? getReporterRegulation().hashCode() : 0);
        result = 31 * result + (getReporterResidualActivity() != null ? getReporterResidualActivity().hashCode() : 0);
        result = 31 * result + (getEmsaValidationPerformed() != null ? getEmsaValidationPerformed().hashCode() : 0);
        result = 31 * result + (getEmsaTFSymbol() != null ? getEmsaTFSymbol().hashCode() : 0);
        result = 31 * result + (getEmsaGeneId() != null ? getEmsaGeneId().hashCode() : 0);
        result = 31 * result + (nPatients != null ? nPatients.hashCode() : 0);
        result = 31 * result + (mPatients != null ? mPatients.hashCode() : 0);
        result = 31 * result + (otherChoices != null ? otherChoices.hashCode() : 0);
        result = 31 * result + (getOtherEffect() != null ? getOtherEffect().hashCode() : 0);
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SomaticVariant that = (SomaticVariant) o;

        if (getVariantMode() != that.getVariantMode()) return false;
        if (getRegulator() != null ? !getRegulator().equals(that.getRegulator()) : that.getRegulator() != null)
            return false;
        if (getReporterRegulation() != null ? !getReporterRegulation().equals(that.getReporterRegulation()) : that.getReporterRegulation() != null)
            return false;
        if (getReporterResidualActivity() != null ? !getReporterResidualActivity().equals(that.getReporterResidualActivity()) : that.getReporterResidualActivity() != null)
            return false;
        if (getEmsaValidationPerformed() != null ? !getEmsaValidationPerformed().equals(that.getEmsaValidationPerformed()) : that.getEmsaValidationPerformed() != null)
            return false;
        if (getEmsaTFSymbol() != null ? !getEmsaTFSymbol().equals(that.getEmsaTFSymbol()) : that.getEmsaTFSymbol() != null)
            return false;
        if (getEmsaGeneId() != null ? !getEmsaGeneId().equals(that.getEmsaGeneId()) : that.getEmsaGeneId() != null)
            return false;
        if (nPatients != null ? !nPatients.equals(that.nPatients) : that.nPatients != null) return false;
        if (mPatients != null ? !mPatients.equals(that.mPatients) : that.mPatients != null) return false;
        if (otherChoices != null ? !otherChoices.equals(that.otherChoices) : that.otherChoices != null) return false;
        return getOtherEffect() != null ? getOtherEffect().equals(that.getOtherEffect()) : that.getOtherEffect() == null;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SomaticVariant{");
        sb.append("variantMode=").append(variantMode);
        sb.append(", regulator=").append(regulator.get());
        sb.append(", reporterRegulation=").append(reporterRegulation.get());
        sb.append(", reporterResidualActivity=").append(reporterResidualActivity.get());
        sb.append(", emsaValidationPerformed=").append(emsaValidationPerformed.get());
        sb.append(", emsaTFSymbol=").append(emsaTFSymbol.get());
        sb.append(", emsaGeneId=").append(emsaGeneId.get());
        sb.append(", nPatients=").append(nPatients.get());
        sb.append(", mPatients=").append(mPatients.get());
        sb.append(", otherChoices=").append(otherChoices.get());
        sb.append(", otherEffect=").append(otherEffect.get());
        sb.append('}');
        return sb.toString();
    }


    public String getRegulator() {
        return regulator.get();
    }


    public void setRegulator(final String newRegulator) {
        regulator.set(newRegulator);
    }


    public StringProperty regulatorProperty() {
        return regulator;
    }


    public String getReporterRegulation() {
        return reporterRegulation.get();
    }


    public void setReporterRegulation(final String newReporterRegulation) {
        reporterRegulation.set(newReporterRegulation);
    }


    public StringProperty reporterRegulationProperty() {
        return reporterRegulation;
    }


    public final String getReporterResidualActivity() {
        return reporterResidualActivity.get();
    }


    public final void setReporterResidualActivity(final String newReporterResidualActivity) {
        reporterResidualActivity.set(newReporterResidualActivity);
    }


    public StringProperty reporterResidualActivityProperty() {
        return reporterResidualActivity;
    }


    public String getEmsaValidationPerformed() {
        return emsaValidationPerformed.get();
    }


    public void setEmsaValidationPerformed(final String newEmsaValidationPerformed) {
        emsaValidationPerformed.set(newEmsaValidationPerformed);
    }


    public StringProperty emsaValidationPerformedProperty() {
        return emsaValidationPerformed;
    }


    public String getEmsaTFSymbol() {
        return emsaTFSymbol.get();
    }


    public void setEmsaTFSymbol(final String newEmsaTFSymbol) {
        emsaTFSymbol.set(newEmsaTFSymbol);
    }


    public StringProperty emsaTFSymbolProperty() {
        return emsaTFSymbol;
    }


    public String getEmsaGeneId() {
        return emsaGeneId.get();
    }


    public void setEmsaGeneId(final String newEmsaGeneId) {
        emsaGeneId.set(newEmsaGeneId);
    }


    public StringProperty emsaGeneIdProperty() {
        return emsaGeneId;
    }


    public String getNPatients() {
        return nPatients.get();
    }


    public void setNPatients(final String newNPatients) {
        nPatients.set(newNPatients);
    }


    public StringProperty nPatientsProperty() {
        return nPatients;
    }


    public String getMPatients() {
        return mPatients.get();
    }


    public void setMPatients(final String newMPatients) {
        mPatients.set(newMPatients);
    }


    public StringProperty mPatientsProperty() {
        return mPatients;
    }


    public String getOther() {
        return otherChoices.get();
    }


    public void setOther(final String newOtherChoices) {
        otherChoices.set(newOtherChoices);
    }


    public StringProperty otherChoicesProperty() {
        return otherChoices;
    }


    public String getOtherEffect() {
        return otherEffect.get();
    }


    public void setOtherEffect(final String newOtherEffect) {
        otherEffect.set(newOtherEffect);
    }


    public StringProperty otherEffectProperty() {
        return otherEffect;
    }
}
