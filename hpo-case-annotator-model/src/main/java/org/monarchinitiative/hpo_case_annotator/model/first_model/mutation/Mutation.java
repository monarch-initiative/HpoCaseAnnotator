package org.monarchinitiative.hpo_case_annotator.model.first_model.mutation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that represents a mutation case (usually in a regulatory element) together with
 * the corresponding phenotype data from the publication. Note that the Mutation
 * describes a published case, which may be compound heterozygote, i.e., may contain
 * two pathogenic variants.
 * @author Peter N Robinson
 * @version 0.2.09 (5 Jan, 2016)
 */
public class Mutation {
    /** Name of affected person or family */
    String identifier=null;
    /** A Free text field for summarizing any important points. */
    String metadata="-";
    /** NCBI Entrez Gene id for the target gene (that is regulated by the regulatory
     * element in which the mutation is located) .*/
    String entrezID=null;
    /** Gene symbol of the target gene */
    String targetGeneSymbol=null;
    /** Name of the biocurator who entered the data on this mutation. */
    private String biocurator=null;
    /** Records and problems encountered in XML parsing */
    private String xmlError=null;
    /** An object (inner class) that encapsulates data about the publication describing the current mutation. */
    private Publication pub=null;
    /** The actual DNA variant being described (in some cases there are two variants if the patient is compound heterozygous). */
    private Variant variant1=null;
    /** The second variant being described in the patient (in case of compound het mutations). */
    private Variant variant2=null;
    /** Data in support of pathogenicity of variant1 */
    private Validation validation1=null;
     /** Data in support of pathogenicity of variant2 */
    private Validation validation2=null;
    private Disease dis=null;
    private Phenotype phen=null;
    /** Date when this entry was created (represented as a Long value). */
    private Long createdDate=null;
    /** Date when this entry was last modified (represented as a long value). */
    private Long lastModifiedDate=null;


    /**
     * This method will report anything about the data in the current mutation 
     * that looks suspicious and that the user should check. For instance, a null
     ** value. For now, just do this for the biocurator name, but this
     * needs to be extended to all data elements
     */
    public String validityCheck() {
	boolean OK=true;
	StringBuilder sb = new StringBuilder();
	if (this.biocurator==null) {
	    sb.append("No biocurator name indicated; ");
	    OK=false;
	}
	if (identifier==null) {
	    sb.append("No patient/family ID provided; ");
	    OK=false;
	}
	if (this.xmlError != null) {
	    sb.append(this.xmlError + "; ");
	    OK=false;
	}
	if (OK) return "OK";
	else return sb.toString();
    }

    public boolean publicationValid() {
	return this.pub.isValid();
    }

    /**
     * This method will report anything about the data in the current mutation 
     * that looks suspicious and that the user should check. For instance, a null
     ** value. For now, just do this for the biocurator name, but this
     * needs to be extended to all data elements
     */
    public String validityCheckCancer() {
	boolean OK=true;
	StringBuilder sb = new StringBuilder();
	if (this.biocurator==null) {
	    sb.append("No biocurator name indicated; ");
	    OK=false;
	}
	if (this.xmlError != null) {
	    sb.append(this.xmlError + "; ");
	    OK=false;
	}
	if (OK) return "OK";
	else return sb.toString();
    }


    /**
     * Update the lastChangedDate. If there is no
     * value for the createdDate, set it to the
     * current time.
     * Note: Use java.sql.Date date = new java.sql.Date(time);
     * to make a display (yyyy-mm-dd)
     */
    private void updateDateValues() {
	long time = System.currentTimeMillis();
	if (this.createdDate==null)
	    this.createdDate=time;
	this.lastModifiedDate=time;
    }

    /**
     * Update the datestamps of this mutation object. 
     * Always modify {@link #lastModifiedDate}. Only 
     * modify {@link #createdDate} if it is uninitialized (i.e., null).
     * @param now Representation of current time in milliseconds.
     */
    public void updateTime(long now) {
	if (this.createdDate==null)
	    this.createdDate=now;
	this.lastModifiedDate=now;
    }

    /**
     * Get the date (represented as a Long value) when this entry
     * was created. Intended to store the date in the XML file. For
     * human readable display, used {@link #getCreatedDateAsString}.
     */
    public long getCreatedDate() {
	if (this.createdDate==null)
	    updateDateValues();
	return this.createdDate;
    }

    public String getCreatedDateAsString() {
	if (this.createdDate==null) return "";
	return new java.sql.Date(this.createdDate).toString();
    }

    /**
     * @param date A string representing the millisecond date value when this entry was created.
     */
    public void setCreatedDate(String date) {
	try {
	    this.createdDate = Long.parseLong(date);
	} catch (NumberFormatException e) {
	    System.out.println(e);
	}
    }

    /**
     * @param date the millisecond date value when this entry was created.
     */
    public void setCreatedDate(Long date) {
	this.createdDate=date;
    }

    public boolean publicationInitialized() {
	return (this.pub != null && this.pub.isInitialized());
    }


    public void setPublication(Publication p) {
	this.pub = p;
    }

    
    /**
     * @param date A string representing the millisecond date value when this entry was last modified.
     */
    public void setLastModifiedDate(String date) {
	try {
	    this.lastModifiedDate =  Long.parseLong(date);
	} catch (NumberFormatException e) {
	    System.out.println(e);
	}
    }

     /**
     * Get the date (represented as a Long value) when this entry
     * was last modified. Intended to store the date in the XML file. For
     * human readable display, used {@link #getLastModifiedDateAsString}.
     */
    public long getLastModifiedDate() {
	if (this.lastModifiedDate==null)
	    updateDateValues();
	return this.lastModifiedDate;
    }

    public String getLastModifiedDateAsString() {
	if (this.lastModifiedDate==null) return "";
	return new java.sql.Date(this.lastModifiedDate).toString();	
    }
  
    /* End of inner class for variant */
    public class Disease {
	String name=null;
	String database=null;
	String id=null;
	public String toString() {
	    return String.format("%s:%s(%s)",database,id,name);
	}
    }
    /* End of inner class for disease */
    public class Hpoterm {
	String name=null;
	String id=null;
	public Hpoterm(String name,String id) {
	    this.name=name; this.id=id;
	}
	public String getID() { return this.id; }
	public String getName() { return this.name; }
    }
    /* End of inner class for Hpoterm*/
    public class Phenotype {
	List<Hpoterm> termlist = new ArrayList<Hpoterm>();
	public void addTerm(String name, String id) {
	    termlist.add(new Hpoterm(name,id));
	}
	public List<Hpoterm> getTerms() { return this.termlist;}
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    boolean notfirst=false;
	    for (Hpoterm t: termlist) {
		if (notfirst) { sb.append(";");  }
		else notfirst=true;
		sb.append(t.getID());
	    }
	    return sb.toString();
	}
    }
    /* End of inner class for Phenotype */
    
   
    
    public Mutation() {
	this.pub=new Publication();
	this.variant1 =new Variant();
	this.variant2=new Variant();
	this.validation1=new Validation();
	this.validation2=new Validation();
	this.dis=new Disease();
	this.phen=new Phenotype();
	//setLastModifiedDate(); // update
    }
    
    public void addHPOTerm(String name, String id){
	this.phen.addTerm(name,id);
    }

    public void outputPhenotype() {
	System.err.println(phen);
    }

    
    public void setDiseaseName(String n) {
	this.dis.name=n;
    }

     public String getDiseaseName() {
	return this.dis.name;
    }
    
    public void setDiseaseDatabase(String d){
    	this.dis.database=d;
    }

    public String getDiseaseDatabase(){
    	return this.dis.database;
    }

    public void setXMLError(String xe) {
	this.xmlError=xe;
    }

    /**
     * This is used by the GUI to decide if we need to refresh the GUI to 
     * show empty fields (so that the user can enter new data) or if we need
     * to show the "old" data of a previously entered mutation that the
     * user is opening again.
     */
    public boolean isInitialized() {
	return this.pub.getPMID() != null;
    }

    
    public void setDiseaseID(String i){
    	this.dis.id=i;
    }

    public String getDiseaseID(){
    	return this.dis.id;
    }
    
    /** @return A String that is similar to the PubMed Summary(text) items.
     */
    public String getPubMedSummary() {
	return this.pub.asPubMedSummary();
    }

    public String getAuthorList() {
	return this.pub.getPublicationAuthorlist();
    }

    public String getGeneSymbol() {
	return this.targetGeneSymbol;
    }

    public String getEntrezGeneID() {
	return this.entrezID;
    }


    public String getFirstAuthorAndYear() {
	String A[] = this.pub.getPublicationAuthorlist().split(",");
	return String.format("%s, %s",A[0],this.pub.getPublicationYear());
    }

    public void setValidation1(Validation val) {
	this.validation1=val;
    }

    public void setValidation2(Validation val) {
	this.validation2=val;
      }

   
	
    /**
     * @param t The title of the  article describing the mutation
     */
    public void setPublicationTitle(String t) {
	this.pub.setPublicationTitle(t);
    }
    /**
     * @return The title of the  article describing the mutation
     */
     public String getPublicationTitle() {
	 return this.pub.getPublicationTitle();
    }

    /** @param l List of the authors of the article describing the mutation */
    public void setPublicationAuthorlist(String l) {
	this.pub.setPublicationAuthorlist(l);
    }

     /** @return List of the authors of the article describing the mutation */
    public String getPublicationAuthorlist() {
	return this.pub.getPublicationAuthorlist();
    }

    /**
     * @param y The year in which the article describing the mutation was published.
     */
    public void setPublicationYear(String y){
	this.pub.setPublicationYear(y);
    }
    /** @return The year in which the article describing the mutation was published.*/
    public String getPublicationYear() {
	return this.pub.getPublicationYear();
    }
    
    /**
     * The volume of the journal in which the 
     * current mutation was published. Can optionally also include the
     * issue in parentheses.
     * @param v The volume (e.g., <b>5</b> or <b>6(7)</b>)
     */
    public void setPublicationVolume(String v){
    	this.pub.setPublicationVolume(v);
    }

    /** @return The volume of the journal article in which the mutation was published. */
    public String getPublicationVolume() {
	return this.pub.getPublicationVolume();
    }

    /**
     * The range of pages for the article in which the current
     * mutation was described.
     * @param p Page range, e.g., 56-62
     */
    public void setPublicationPages(String p){
    	this.pub.setPublicationPages(p);
    }

    /** @return The pages of the journal article in which the mutation was published. */
    public String getPublicationPages() {
	return this.pub.getPublicationPages();
    }

    /** @param p The PubMed ID of the  article in which the mutation was published. */
    public void setPMID(String p){
    	this.pub.setPMID(p);
    }
     /** @return The PubMed ID of the  article in which the mutation was published. */
    public String getPMID(){
    	return this.pub.getPMID();
    }
    /**
     * @param j the journal article in which the mutation was published. 
     */
    public void setPublicationJournal(String j) {
    	this.pub.setPublicationJournal(j);
    }

    /**
     * @return the journal article in which the mutation was published. 
     */
    public String getPublicationJournal() {
    	return this.pub.getPublicationJournal();
    }
    
    
    
    public void setIdentifier(String id) {
    	this.identifier=id;
    }

    public String getIdentifier() {
    	return this.identifier;
    }
    
    public void setMetadata(String md) {
    	this.metadata=md;
    }

    public String getMetadata() {
    	return this.metadata;
    }

    public boolean targetGeneIsInitialized() {
	return this.entrezID != null && this.targetGeneSymbol != null;
    }
    
    
    public void setTargetGeneEntrezID(String id) {
    	this.entrezID=id;
    }

    public String getTargetGeneEntrezID() {
	return this.entrezID;
    }
    
    public void setTargetGeneSymbol(String sym){
    	this.targetGeneSymbol=sym;
    }

    public String getTargetGeneSymbol(){
    	return this.targetGeneSymbol;
    }
     
    public void setBiocurator(String b) {
	this.biocurator=b;
    }
    
    public String getBiocurator() {
	return this.biocurator;
    }
   
    public boolean isVariant1initialized() {
	return this.variant1.isInitialized();
    }

    /**
     * Note that if only one variant was entered, then Variant2 is null
     * The method thus only returns true if two Variants have been
     * entered for a given case; additionally, sufficient information
     * must have been entered for the variant to be considered "initialized"
     * See .
     */
    public boolean isVariant2initialized() {
	if (variant2==null) {
	    return false;
	}
	return this.variant2.isInitialized();
    }

    /** @return The {@link Variant Variant} object representing the mutation (or the first of two compound het mutations)*/
    public Variant getVariant1() {
	return this.variant1;
    }
    /** @return The {@link Variant Variant} object representing the second of two compound het mutations (or null if there is only one)*/
    public Variant getVariant2() {
	return this.variant2;
    }

    /**
     * @return the Validation object that corresponds to variant 1
     */
    public Validation getValidation1() {
	return this.validation1;
    }

    /**
     * @return the Validation object that corresponds to variant 1
     */
    public Validation getValidation2() {
	return this.validation2;
    }



    public void setVariant1(Variant v) {
	this.variant1 = v;
    }

    public void setVariant2(Variant v) {
	this.variant2 = v;
    }


    public String getBuild() {
	if (this.variant1 != null)
	    return this.variant1.getBuild();
	else
	    return null;
    }
    
    public Map<String,String> getHPOterms() {
    	List<Hpoterm> lst = this.phen.getTerms();
    	Map<String,String> mp = new HashMap<String,String>();
    	for (Hpoterm h: lst) {
    		String id = h.getID();
    		String name = h.getName();
    		mp.put(id,name);
    	}
    	return mp;
    }
    	
   

    /**
     * We say that a mutation is compound het if it is heterozygous and there
     * is a second, distinct, heterozygous mutation that was found in the 
     * patient AND both mutations are regulatory. If the "second" mutation
     * is a "normal" coding mutation (missense, nonsense, etc), then we just
     * code the single regulatory mutation as simple heterozygous. The reason for 
     * this is that our database will only contain regulatory mutations. We will interlink
     * two compound het regulatory mutations in the Web database, but if the second 
     * mutation is coding, we will just state this in the Metadata field.
     */
    public boolean isCompoundHeterozygous() {
	return this.variant2 !=null;

    }

    /** @return a summary of variant 1 (and if present, of variant 2). */
    public String summary() {
	String returnString=null;
	String var = this.variant1.summary();
	returnString = String.format("%s\t%s\t%s\t%s\t%s",
			     var,
			     this.dis.toString(),
			     this.targetGeneSymbol,this.entrezID,
			     this.variant1.getVariantClass() );
	if (isVariant2initialized()) {
	    var = this.variant2.summary();
	    String second =  String.format("%s\t%s\t%s\t%s\t%s",
			     var,
			     this.dis.toString(),
			     this.targetGeneSymbol,this.entrezID,
			     this.variant1.getVariantClass() );
	    returnString = String.format("%s\n%s",returnString,second);
	}
	return returnString;
    }



    /**
     * Returns a summary of the mutation with the fields separated by a "pipe" symbol.
     * If there are two variants associated with this Mutation object, then it returns
     * two lines (the second line does not have a new line character).
     */
    public String toString() {
	String returnString=null;
	String var = this.variant1.toString();
	returnString = String.format("%s§%s§%s§%s§%s§%s§%s(%s)§%s§%s",
			     this.pub.shortForm(),var,
			     this.dis.toString(),this.phen.toString(),this.identifier,this.metadata,
			     this.targetGeneSymbol,this.entrezID,
			     this.variant1.getVariantClass(), this.variant1.getPathMech() );
	if (isVariant2initialized()) {
	    var = this.variant2.toString();
	    String second =  String.format("%s§%s§%s§%s§%s§%s§%s(%s)§%s§%s",
			     this.pub.shortForm(),var,
			     this.dis.toString(),this.phen.toString(),this.identifier,this.metadata,
			     this.targetGeneSymbol,this.entrezID,
			     this.variant1.getVariantClass(), this.variant2.getPathMech() );
	    returnString = String.format("%s\n%s",returnString,second);
	}
	return returnString;
    }
    

}
/* eof */
