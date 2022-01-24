package org.monarchinitiative.hpo_case_annotator.app.publication;

public interface PublicationBrowser {

    /**
     * Show details regarding publication in a browser.
     *
     * @param pmid PubMed ID of the publication
     */
    void browse(String pmid);

}
