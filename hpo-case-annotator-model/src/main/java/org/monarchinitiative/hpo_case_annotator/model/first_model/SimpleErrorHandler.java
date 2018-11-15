package org.monarchinitiative.hpo_case_annotator.model.first_model;


import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.InputStream;


/**
 * This is the error handler that is used for the parsing of the
 * mutation XML files. Note that some errors may come from the DTD file if
 * entries in the Java code are changed without corresponding changes in the DTD.
 * @author Peter Robinson
 * @version 0.1.03 (11 Dec 2015)
 */
public class SimpleErrorHandler implements ErrorHandler {
//    private String xmlfile=null;
    private InputStream inputStream;
    private StringBuilder sb=new StringBuilder();
    
    public SimpleErrorHandler(InputStream inputStream) {this.inputStream=inputStream;}
    
    public boolean parseSuccesful() {return sb.toString().equals("");}
    
    public String getError() { return sb.toString(); }
    
    public void warning(SAXParseException e) throws SAXException {
	System.out.println(String.format("[WARNING: SimpleErrorHandler.java] %s: %s",inputStream, e.getMessage()));
	sb.append(e.getMessage());
    }
    
    public void error(SAXParseException e) throws SAXException {
	System.out.println(String.format("[ERROR:SimpleErrorHandler.java:l.33] %s: %s",inputStream, e.getMessage()));
	sb.append(e.getMessage());
    }
    
    public void fatalError(SAXParseException e) throws SAXException {
	System.out.println(String.format("[FATAL ERROR:SimpleErrorHandler.java:l.38] %s: %s",inputStream, e.getMessage()));
	sb.append(e.getMessage());
	
    }
}
/* eof */

    
