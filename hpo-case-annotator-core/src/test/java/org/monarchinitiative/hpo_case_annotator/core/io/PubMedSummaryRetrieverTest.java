package org.monarchinitiative.hpo_case_annotator.core.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


/**
 * This test class tests functionality of {@link PubMedSummaryRetriever} class. The tests are focused on retrieval of
 * html response containing summary text for specific pmid and subsequently decoding the html response into the summary
 * text.
 */
public class PubMedSummaryRetrieverTest {


    /**
     * Offline query for a PMID.
     *
     * @throws Exception bla
     */
    @Test
    public void offlineRetrievalOfPMID() throws Exception {
        String htmlResponse = "<?xml version=\"1.0\" encoding=\"utf-8\"?> <!DOCTYPE html PUBLIC \"-//W3C//DTD " +
                "XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> <pre> 1: Miao Q," +
                " Ping X, Tang X, Zhang L, Zhang X, Cheng Y, Shentu X. Experimental assessment of novel PAX6 splicing" +
                " mutations in two Chinese families with aniridia. Gene. 2017 Jul 28. pii: S0378-1119(17)30609-1. " +
                "doi: 10.1016/j.gene.2017.07.073. [Epub ahead of print] PubMed PMID: 28760551.  </pre>";

        Function<String, InputStream> connectionFactory = p -> new ByteArrayInputStream(htmlResponse.getBytes());
        PubMedSummaryRetriever instance = new PubMedSummaryRetriever(connectionFactory);
        String response = instance.getSummary("28760551");
        assertThat(response, is("1: Miao Q, Ping X, Tang X, Zhang L, Zhang X, Cheng Y, Shentu X. Experimental " +
                "assessment of novel PAX6 splicing mutations in two Chinese families with aniridia. Gene. 2017 Jul " +
                "28. pii: S0378-1119(17)30609-1. doi: 10.1016/j.gene.2017.07.073. [Epub ahead of print] PubMed PMID: " +
                "28760551."));
    }


    /**
     * Test that non-existing PMID throws IOException.
     */
    @Test(expected = IOException.class)
    public void getSummaryForUnexistingPmid() throws Exception {
        String unfoundResponse = "<?xml version=\"1.0\" encoding=\"utf-8\"?> <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML " +
                "1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> <pre> 1.  Error " +
                "occurred: cannot get document summary PMID: 1234567890123456 </pre>";
        PubMedSummaryRetriever retriever = new PubMedSummaryRetriever(p -> new ByteArrayInputStream(unfoundResponse.getBytes()));
        retriever.getSummary("1234567890123456");
    }
}