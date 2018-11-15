package org.monarchinitiative.hpo_case_annotator.model.xml_model;

/**
 * This bean represents one OMIM disease term as present in /dat/OMIM.tab file
 *
 * @author Daniel Danis
 */
public final class OMIM {

    /* OMIM mimid string */
    private String mimID;

    /* List of OMIM names */
    private String name;

    /* OMIM canonical name string */
    private String canonicalName;


    public OMIM(String mimID, String name, String canonicalName) {
        this.mimID = mimID;
        this.name = name;
        this.canonicalName = canonicalName;
    }


    public String getMimID() {
        return mimID;
    }


    public void setMimID(String mimID) {
        this.mimID = mimID;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getCanonicalName() {
        return canonicalName;
    }


    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

}
