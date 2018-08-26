package org.monarchinitiative.hpo_case_annotator.first_model.mutation;



/**
 * This is a simple data access object that represents the data that
 * is stored in the DiseaseWidget, being a disease database, a disease name, and the
 * id (accession number) that the disease has inthe database.
 * @author Peter Robinson
 * @version 0.01 (May 25, 2016)
 */
public class Disease {
    /** The database from which the disease ID and disease name are taken. */
    private String diseaseDB=null;
    /** The accession number (id) of the disease in the database */
    private String diseaseID=null;
    /** The name of the disease */
    private String diseaseName=null;
    /** List of errors, if any (if no errors occured in parsing, then this variable should be null).*/
    private String error=null;

    public Disease(String db, String id, String name) {
	this.diseaseDB=db;
	this.diseaseID=id;
	this.diseaseName=name;
    }


    public void setDiseaseDB(String db) { this.diseaseDB=db; }
    public void getDiseaseID(String id) {this.diseaseID=id; }
    public void getDiseaseName(String name) { this.diseaseName=name; }
    public void setError(String err) { this.error=err;}



    public String getDiseaseDB() { return this.diseaseDB; }
    public String getDiseaseID() { return this.diseaseID; }
    public String getDiseaseName() { return this.diseaseName; }
    public boolean validParse() { return this.error==null;}
    public String getErrors() { if (this.error==null) return ""; else return this.error; }

}

/* eof */
