package org.monarchinitiative.hpo_case_annotator.model.first_model.mutation;


/**
 * This class encapsulates all of the information about the validation of a particular regulatory mutation. Note that
 * there are four main items that we would like to record.
 * <p>Note that this class can sotre data about either a Mendelian mutation or a
 * cancer mutation, and it is up to the XML Handler and the GUI to use the correct methods. In essence, the only
 * relevant differences is that a different subset of data is used to validate the cancer mutations and the mendelian
 * mutations. For instance, Mendelian mutations have cosegregation, and cancer mutations often have data like "m of n
 * patients" with a certain cancer.
 * <ul>
 * <li> Reporter. An assay such as luciferase or CAT that measures the transcriptional
 * activity of a promoter or an enhancer. It can be decreased or increased by the mutation, and this is indicated by the
 * radio button in the GUI.
 * <li>EMSA. Electrophoretic mobility shift assay. Enter the Entrez Gene id corresponding to the
 * transcription factor that was investigated.
 * <li>Cogregration. The mutation cosegregates with the disease in the family (Mendelian).
 * <li>Number of patients with mutation -- n of m patients with a certain kind of cancer (Oncology).
 * <li>Comparability. This is the last resort, and means that we believe the sequence variant is a valid regulatory
 * mutation because
 * it is comparable with other published mutations for which experimental evidence exists (e.g., on the same nucleotide
 * or otherwise comparable).
 * <li>Other. A list of rarer possibilities. Effect size is always with respect to the category of other.
 * </ul>
 *
 * @author Peter Robinson
 * @version 0.1.6 (9 Nov, 2015)
 */

public class Validation {

    /** The change in reporte activity (up or down) in percent of wildtype. */
    protected Double reporterEffectSize = null;

    ;

    /** The direction of change in reporter activity (e.g., luciferase). No if there was no experiment */
    protected Effect reporterEffect = Effect.NO;

    /** The direction of change in other activity (e.g., luciferase). No if there was no experiment */
    protected Effect otherEffect = Effect.NO;

    /**
     * The gene symbol of the transcription factor that was investigated by EMSA (electrophoretic mobility assay).
     */
    protected String emsaSymbol = null;

    /**
     * The Entrez gene ID of the transcription factor that was investigated by EMSA (electrophoretic mobility assay).
     */
    protected Integer emsaGeneID = null;

    /** True if the mutation was shown to cosegregate with the disease */
    protected boolean hasCosegregation = false;

    /**
     * True if teh mutation was found to be pathogenic because it was similar to other published regulatory mutations in
     * the gene
     */
    protected boolean hasComparability = false;

    /** True if there is an "other" reason for believing pathogenicity (see combobox) */
    protected boolean hasOther = false;

    /** The category of \"other\" effect, anything not covered by the main categories */
    protected String other = null;

    /** The number of patients in a cancer study who have the mutation in question (M of N, e.g., 12/27) */
    protected Integer mPatientsWithMutation = null;

    /** The total number of patients in a cancer study (N). */
    protected Integer nPatients = null;


    public Validation() {

    }


    public void setM(Integer M) {
        this.mPatientsWithMutation = M;
    }


    public void setN(Integer N) {
        this.nPatients = N;
    }


    public Integer getM_PatientsWithMutation() {
        return this.mPatientsWithMutation;
    }


    public Integer getN_Patients() {
        return nPatients;
    }


    /**
     * @return true if this variant has frequency data (from a cancer study).
     */
    public boolean hasFrequencyData() {
        return (this.mPatientsWithMutation != null && this.nPatients != null);
    }


    public void setReporterDecreased() {
        this.reporterEffect = Effect.DECREASED;
    }


    public void setReporterIncreased() {
        this.reporterEffect = Effect.INCREASED;
    }


    public void setReporterDecreased(Double effect) {
        this.reporterEffect = Effect.DECREASED;
        this.reporterEffectSize = effect;
    }


    public void setReporterIncreased(Double effect) {
        this.reporterEffect = Effect.INCREASED;
        this.reporterEffectSize = effect;
    }


    public void setEmsaGeneID(Integer ID) {
        this.emsaGeneID = ID;
    }


    public void setCosegregation(boolean c) {
        this.hasCosegregation = c;
    }


    public void setComparability(boolean c) {
        this.hasComparability = c;
    }


    public void setOtherIncreased(String reason) {
        this.other = reason;
        this.otherEffect = Effect.INCREASED;
    }


    public void setOtherIncreased() {
        this.otherEffect = Effect.INCREASED;
    }


    public void setOtherDecreased(String reason) {
        this.other = reason;
        this.otherEffect = Effect.DECREASED;
    }


    public void setOtherDemonstrated(String reason) {
        this.other = reason;
        this.otherEffect = Effect.DEMONSTRATED;
    }


    public void setOtherDecreased() {
        this.otherEffect = Effect.DECREASED;
    }


    public void setOtherDemonstrated() {
        this.otherEffect = Effect.DEMONSTRATED;
    }


    public void setOtherReason(String reason) {
        this.other = reason;
    }


    public boolean hasReporterEffect() {
        return this.reporterEffect != Effect.NO;
    }


    public boolean reporterIncreased() {
        return this.reporterEffect == Effect.INCREASED;
    }


    public boolean reporterDecreased() {
        return this.reporterEffect == Effect.DECREASED;
    }


    public Double getReporterEffectSize() {
        return this.reporterEffectSize;
    }


    public void setReporterEffectSize(String eff) {
        try {
            Double d = Double.parseDouble(eff);
            this.reporterEffectSize = d;
        } catch (NumberFormatException e) {
            System.err.println("[ERROR] Could not parse double value for effect size in Validation.java");
            System.err.println(e.getMessage());
        }
    }


    public boolean hasEMSA() {
        return (this.emsaSymbol != null && this.emsaGeneID != null);
    }


    public String getEmsaSymbol() {
        return this.emsaSymbol;
    }


    public void setEmsaSymbol(String sym) {
        this.emsaSymbol = sym;
    }


    public Integer getEmsaGeneID() {
        return this.emsaGeneID;
    }


    public void setEmsaGeneID(String id) {
        try {
            Integer i = Integer.parseInt(id);
            this.emsaGeneID = i;
        } catch (NumberFormatException e) {
            System.err.println("[ERROR] Could not parse integer value for Emsa ID in Validation.java");
            System.err.println(e.getMessage());
        }
    }


    public boolean hasCosegregation() {
        return this.hasCosegregation;
    }


    public boolean hasComparability() {
        return this.hasComparability;
    }


    public boolean hasOtherEffect() {
        return this.otherEffect != Effect.NO;
    }


    public boolean otherIncreased() {
        return this.otherEffect == Effect.INCREASED;
    }


    public boolean otherDecreased() {
        return this.otherEffect == Effect.DECREASED;
    }


    public boolean otherDemonstrated() {
        return this.otherEffect == Effect.DEMONSTRATED;
    }


    /** @return The category of validation assay used (e.g., telomerase assay) */
    public String getOtherCategory() {
        return this.other;
    }


    /**
     * The effect of the mutation on the assay (no: no effect, up: increased activitiy, down: decreased activity.
     */
    public enum Effect {
        NO("no"),
        DECREASED("decreased"),
        INCREASED("increased"),
        DEMONSTRATED("demonstrated");

        private String name = null;


        /** constructor sets the name variable. */
        Effect(String n) {
            name = n;
        }


        public String toString() {
            return this.name;
        }
    }

}
/* eof */
