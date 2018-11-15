package org.monarchinitiative.hpo_case_annotator.model.first_model;

import org.monarchinitiative.hpo_case_annotator.model.first_model.mutation.Mutation;
import org.monarchinitiative.hpo_case_annotator.model.first_model.mutation.Validation;
import org.monarchinitiative.hpo_case_annotator.model.first_model.mutation.Variant;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * This class parses the XML files representing individual regulatory
 * mutations using SAX. It corresponds to the schemes (DTD) that are
 * found in the mutation/xml directories and are slightly different for
 * Mendelian mutations and cancer.
 * @version 0.2.67 (31 December, 2015)
 * @author Peter Robinson
 */
public class MutationContentHandler implements ContentHandler {

    /** The result of parsing is a {@link Mutation Mutation} object */
    private Mutation mutation=null;
    /** Flag to indicate we are traversing a "publication" XML node */
    private boolean inPublicationElement=false;
    /** Flag to indicate we are traversing a "variant" XML node */
    private boolean inVariant=false;
    /** Flag to indicate we are traversing a "validation" XML node */
    private boolean inValidation=false;
    /** Flag to indicate we are traversing a "disease" XML node */
    private boolean inDisease=false;
    /** Flag to indicate we are traversing a "phenotypelist" XML node */
    private boolean inPhenotypeList=false;
    /** Flag to indicate we are traversing a "phenotype" XML node */
    private boolean inPhenotype=false;
    /** Flag to indicate we are traversing a "identifier" XML node */
    private boolean inIdentifier=false;
    /** Flag to indicate we are traversing a "metadata" XML node */
    private boolean inMetadata=false;
    /** Flag to indicate we are traversing a "targetgene" XML node */
    private boolean inTargetgene=false;
    /** temporary variable to store the current HPO name, used when we are traversing an HPO XML node */
    private String currentHpoName=null;
    /** temporary variable to store the current HPO id, used when we are traversing an HPO XML node */
    private String currentHpoId=null;
    /** A temporary object to hold the current variant */
    private Variant currentVariant=null;
    /** A counter to keep track of whether we need to set variant 1 or variant 2 */
    private int n_variant=1;
    /** A termporary object to hold the current validation object. Note that it belongs to the
	{@link #currentVariant} object. */
    private Validation currentValidation=null;

    
    
    
    private final StringBuilder characters = new StringBuilder(64);


    public void characters(char[] ch, int start, int length)
	throws SAXException {
	characters.append(new String(ch,start,length));
    }
  
    public Mutation getMutation() { return this.mutation; }



    /**
     * We expect the other direction attribute to be one of up, down, or direction
     * extract this, and if we do not find one of the three options throw an exception
     */
    private String getOtherAttribute(Attributes attributes) throws SAXException {
	int length = attributes.getLength();
	for (int i=0; i<length; i++) {
	    String name = attributes.getQName(i);
	    String value = attributes.getValue(i);
	    if (value.equals("up") || value.equals("down") || value.equals("demonstrated"))
		return value;
	}
	String s = String.format("[ERROR(MutationContentHandler)]: Unknown other-direction attribute");
	throw new SAXException(s);
    }


    private boolean checkIfReporterIsUp( Attributes attributes ) throws SAXException {
	int length = attributes.getLength();
	for (int i=0; i<length; i++) {
	    String name = attributes.getQName(i);
	    String value = attributes.getValue(i);
	    if (name.equals("direction")) {
		if (value.equals("up"))
		    return true;
		else if (value.equals("down"))
		    return false;
		else {
		    String s = String.format("[ERROR(MutationContentHandler)]: Unknown direction attribute: %s:%s",
					     name, value);
		    throw new SAXException(s);
		}
	    }
	}
	String e="Did not find \"direction\" attribute for effect";
	throw new SAXException(e);
    }

    /**
     * Checks a set of Attributes for whether we have present=\"yes\"
     */
    private boolean checkIfPresent( Attributes attributes ) throws SAXException {
	int length = attributes.getLength();
	for (int i=0; i<length; i++) {
	    String name = attributes.getQName(i);
	    String value = attributes.getValue(i);
	    if (name.equals("present")) {
		if (value.equals("yes"))
		    return true;
		else if (value.equals("no"))
		    return false;
		else {
		    String s = String.format("[ERROR(MutationContentHandler)]: Unknown present attribute: %s:%s",
					     name, value);
		    throw new SAXException(s);
		}
	    }
	}
	String e="Did not find \"present\" attribute for cosgregation/compatibility";
	throw new SAXException(e);
    }
    
    public void startElement(String uri, String localName, String qName,
			     Attributes attributes) throws SAXException {
	if (localName.equals("mutation")) {
	    mutation = new Mutation();
	} else if (localName.equals("publication")) {
	    this.inPublicationElement=true; 
	} else if (localName.equals("variant")) {
	    this.inVariant=true;
	    this.currentVariant = new Variant();
	    /* Variant can have type and id as attributes. id is required. */
	    int length = attributes.getLength();
	    for (int i=0; i<length; i++) {
		String name = attributes.getQName(i);
		String value = attributes.getValue(i);
		if (name.equals("genotype")) {
		    this.currentVariant.setGenotype(value);
		} else if (name.equals("class")) {
		    this.currentVariant.setVariantClass(value);
		} else {
		    String s = String.format("[ERROR(MutationContentHandler)]: Unknown attribute for variant: %s:%s",
					     name, value);
		    throw new SAXException(s);
		}
	    }
	} else if (localName.equals("frequency")) {
	    int length = attributes.getLength();
	    for (int i=0; i<length; i++) {
		String name = attributes.getQName(i);
		String value = attributes.getValue(i);
		if (name.equals("m")) {
		    try {
			Integer M = Integer.parseInt(value);
			this.currentValidation.setM(M);
		    } catch (NumberFormatException e) {
			e.printStackTrace();
			continue;
		    }
		} else if (name.equals("n")) {
		    try {
			Integer N = Integer.parseInt(value);
			this.currentValidation.setN(N);
		    } catch (NumberFormatException e) {
			e.printStackTrace();
			continue;
		    }
		} else {
		    String s = String.format("[ERROR(MutationContentHandler)]: Unknown attribute for variant: %s:%s",
					     name, value);
		    throw new SAXException(s);
		}
	    }
	} else if (localName.equals("disease")) {
	    this.inDisease=true;
	} else if (this.inValidation) {
	    if (localName.equals("reporter")) {
		boolean isUp = checkIfReporterIsUp(attributes);
		if (isUp)
		    this.currentValidation.setReporterIncreased();
		else
		    this.currentValidation.setReporterDecreased();
	    } else if (localName.equals("cosegregation")) {
		boolean present = checkIfPresent(attributes);
		this.currentValidation.setCosegregation(present);
	    }  else if (localName.equals("comparability")) {
		boolean present = checkIfPresent(attributes);
		this.currentValidation.setComparability(present);
	    }  else if (localName.equals("other")) {
		String oth=getOtherAttribute(attributes);
		if (oth.equals("up")) {
		    this.currentValidation.setOtherIncreased();
		} else if (oth.equals("down")) {
		    this.currentValidation.setOtherDecreased();
		} else if (oth.equals("demonstrated")) {
		    this.currentValidation.setOtherDemonstrated();
		}
	    }  

	} else if (localName.equals("validation")) {
	    this.currentValidation = new Validation();
	    this.inValidation=true;
	   
	} else if (localName.equals("identifier")) {
	    this.inIdentifier=true;
	}  else if (localName.equals("metadata")) {
	    this.inMetadata=true;
	} else if (localName.equals("phenotypelist")) {
	    this.inPhenotypeList=true;
	} else if (this.inPhenotypeList && localName.equals("phenotype")) {
	    this.inPhenotype=true;
	} else if (localName.equals("targetgene")) {
	    this.inTargetgene=true;
	}
	//System.out.println("StartElement: " + localName);
    }

  
  public void endElement(String uri, String localName, String qName)
      throws SAXException {
	// trim, if necessary
	String currentValue = characters.toString().trim();
	currentValue = currentValue.replace("\n", "").replace("\r", "");

    if (this.inPublicationElement) {
	if (localName.equals("title"))
	    mutation.setPublicationTitle(currentValue);
	else if (localName.equals("authorlist")) 
	    mutation.setPublicationAuthorlist(currentValue);
      	else if (localName.equals("year"))
	    mutation.setPublicationYear(currentValue);
      	else if (localName.equals("volume"))
	    mutation.setPublicationVolume(currentValue);
      	else if (localName.equals("pages")) 
	    mutation.setPublicationPages(currentValue);
      	else if (localName.equals("journal")) 
	    mutation.setPublicationJournal(currentValue);
      	else if (localName.equals("pmid"))
	    mutation.setPMID(currentValue);
      	else if (localName.equals("publication"))
	    this.inPublicationElement=false;
      	else 
	    throw new SAXException(String.format("Could not find publication element called: %s",localName));
    } else if (this.inVariant) {
    	if (localName.equals("build"))
	    this.currentVariant.setBuild(currentValue);
    	else if (localName.equals("chromosome"))
	    this.currentVariant.setChromosome(currentValue);
    	else if (localName.equals("pos"))
	    this.currentVariant.setPosition(currentValue);
    	else if (localName.equals("ref"))
	    this.currentVariant.setRef(currentValue);
    	else if (localName.equals("alt"))
	    this.currentVariant.setAlt(currentValue);
	else if (localName.equals("snippet"))
	    this.currentVariant.setSnippet(currentValue);
	else if (localName.equals("regulator"))
	    this.currentVariant.setRegulator(currentValue);
	else if (localName.equals("pathomechanism")) 
	    this.currentVariant.setPathomechanism(currentValue);
	else if (this.inValidation) {
	    if( localName.equals("entrezid")) {
		this.currentValidation.setEmsaGeneID(currentValue);
	    } else if (localName.equals("genesymbol")) {
		this.currentValidation.setEmsaSymbol(currentValue);
	    } else if (localName.equals("reporter")) {
		this.currentValidation.setReporterEffectSize(currentValue);
	    } else if (localName.equals("other")) {
		this.currentValidation.setOtherReason(currentValue);
	    } else if (localName.equals("validation")) {
		this.inValidation=false;
		if (this.n_variant==2) {
		    this.mutation.setValidation2(this.currentValidation);
		} else {
		    this.mutation.setValidation1(this.currentValidation);
		}
		this.currentValidation=null;
	    }
	} else if (localName.equals("variant")) {
	    this.inVariant=false;
	    /* We are finished with the current variant */
	    if (this.n_variant==2) {
		this.mutation.setVariant2(this.currentVariant);
		this.n_variant++;
	    } else if (this.n_variant==1) {
		this.mutation.setVariant1(this.currentVariant);
		this.n_variant++;
	    } else {
		/* This should really NEVER happen, so just quit - there must be a major error */
		System.err.println("Error: Attempting to add mutation number " + n_variant +". Exiting program");
		System.exit(1);
	    }
	} else 
	    throw new SAXException(String.format("Could not find variant element called: %s",localName));    
    } else if (this.inDisease) {
    	if (localName.equals("name"))
    		mutation.setDiseaseName(currentValue);
    	else if (localName.equals("database"))
    		mutation.setDiseaseDatabase(currentValue);
    	else if (localName.equals("id"))
    		mutation.setDiseaseID(currentValue);
    	else if (localName.equals("disease"))
    		this.inDisease=false;
    	else 
      		throw new SAXException(String.format("Could not find disease element called: %s",localName));   
    } else if (this.inPhenotypeList) {
    	if (localName.equals("phenotypelist"))
    		this.inPhenotypeList=false;
    	else if (this.inPhenotype) {
    		if (localName.equals("hponame"))
    			this.currentHpoName=currentValue;
    		else if (localName.equals("hpoid"))
    			this.currentHpoId=currentValue;
    		else if (localName.equals("phenotype")) {
    			this.inPhenotype=false;
    			mutation.addHPOTerm(currentHpoName,currentHpoId);
    		}
    	
    	}
    } else if (this.inIdentifier && localName.equals("identifier")) {
    	this.inIdentifier=false;
    	mutation.setIdentifier(currentValue);
    }
    else if (this.inMetadata && localName.equals("metadata")) {
    	this.inMetadata=false;
    	mutation.setMetadata(currentValue);
    } else if (this.inTargetgene) {
    	if (localName.equals("entrezid")) {
    		mutation.setTargetGeneEntrezID(currentValue);
    	} else if (localName.equals("genesymbol")) {
    		mutation.setTargetGeneSymbol(currentValue);
    	} else if (localName.equals("targetgene")) {
    		this.inTargetgene=false;
    	}    
    } else if (localName.equals("biocurator")) {
    	mutation.setBiocurator(currentValue);
    }  else if (localName.equals("datecreated")) {
    	mutation.setCreatedDate(currentValue);
    } else if (localName.equals("datemodified")) {
	mutation.setLastModifiedDate(currentValue);
    } 
    characters.setLength(0);
  }

    /* The following are required to fulfil the interface, but we can use the default implementations */
    public void endDocument() throws SAXException {}
    public void endPrefixMapping(String prefix) throws SAXException {}
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}
    public void processingInstruction(String target, String data) throws SAXException {}
    public void setDocumentLocator(Locator locator) {   }
    public void skippedEntity(String name) throws SAXException {}
    public void startDocument() throws SAXException {}
    public void startPrefixMapping(String prefix, String uri)throws SAXException {}
}

/* eof */
