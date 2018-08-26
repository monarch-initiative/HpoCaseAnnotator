package org.monarchinitiative.hpo_case_annotator.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class contains attributes pertaining to validation of variants which cause Mendelian regulatory disorders.
 * <p>Currently we are interested in: <ul> <li>Reporter - direction of change of activity</li> <li>Reporter - residual
 * activity value</li> <li>EMSA validation - performed or not</li> <li>EMSA transcription factor - which was being
 * examined</li> <li>EMSA gene id</li> <li>Other effect - no, up, down or demonstrated</li> <li>Other effect -
 * specification</li> </ul>
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class MendelianVariant extends Variant {

    private final VariantMode variantMode = VariantMode.MENDELIAN;

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


    public String getRegulator() {
        return regulator.get();
    }


    public void setRegulator(String newRegulator) {
        regulator.set(newRegulator);
    }


    public StringProperty regulatorProperty() {
        return regulator;
    }


    public String getReporterRegulation() {
        return reporterRegulation.get();
    }


    public void setReporterRegulation(String newReporterRegulation) {
        reporterRegulation.set(newReporterRegulation);
    }


    public StringProperty reporterRegulationProperty() {
        return reporterRegulation;
    }


    public final String getReporterResidualActivity() {
        return reporterResidualActivity.get();
    }


    public final void setReporterResidualActivity(String newReporterResidualActivity) {
        reporterResidualActivity.set(newReporterResidualActivity);
    }


    public StringProperty reporterResidualActivityProperty() {
        return reporterResidualActivity;
    }


    public String getEmsaValidationPerformed() {
        return emsaValidationPerformed.get();
    }


    public void setEmsaValidationPerformed(String newEmsaValidationPerformed) {
        emsaValidationPerformed.set(newEmsaValidationPerformed);
    }


    public StringProperty emsaValidationPerformedProperty() {
        return emsaValidationPerformed;
    }


    public String getEmsaTFSymbol() {
        return emsaTFSymbol.get();
    }


    public void setEmsaTFSymbol(String newEmsaTFSymbol) {
        emsaTFSymbol.set(newEmsaTFSymbol);
    }


    public StringProperty emsaTFSymbolProperty() {
        return emsaTFSymbol;
    }


    public String getEmsaGeneId() {
        return emsaGeneId.get();
    }


    public void setEmsaGeneId(String newEmsaGeneId) {
        emsaGeneId.set(newEmsaGeneId);
    }


    public StringProperty emsaGeneIdProperty() {
        return emsaGeneId;
    }


    public String getOther() {
        return otherChoices.get();
    }


    public void setOther(String newOtherChoices) {
        otherChoices.set(newOtherChoices);
    }


    public StringProperty otherProperty() {
        return otherChoices;
    }


    public String getOtherEffect() {
        return otherEffect.get();
    }


    public void setOtherEffect(String newOtherEffect) {
        otherEffect.set(newOtherEffect);
    }


    public StringProperty otherEffectProperty() {
        return otherEffect;
    }

}
