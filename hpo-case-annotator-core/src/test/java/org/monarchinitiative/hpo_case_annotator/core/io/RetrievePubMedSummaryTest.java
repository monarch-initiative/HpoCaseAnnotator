package org.monarchinitiative.hpo_case_annotator.core.io;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URL;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * This test class tests functionality of {@link RetrievePubMedSummary} class. The tests are focused on retrieval of
 * html response containing summary text for specific pmid and subsequently decoding the html response into the summary
 * text.
 */
@Ignore("These tests are dependent on working internet connection. Run manually, when necessary")
public class RetrievePubMedSummaryTest {

    /**
     * Integration test where retrieval of summary text corresponding to PMID is being tested.
     */
    @Test(timeout = 10000)
    public void getSummary() throws Exception {
        String pmid = "28760551";
        String firstExpected = "1: Miao Q, Ping X, Tang X, Zhang L, Zhang X, Cheng Y, Shentu X. Experimental " +
                "assessment of novel PAX6 splicing mutations in two Chinese families with aniridia. Gene. 2017 Sep " +
                "30;630:44-48. doi: 10.1016/j.gene.2017.07.073. Epub 2017 Jul 29. PubMed PMID: 28760551.";
        Optional<String> actual = RetrievePubMedSummary.getSummary(pmid);
        actual.ifPresent(summary -> assertEquals(firstExpected, summary)); // it won't be present if we're offline.
    }


    /**
     * Test decoding the sample html responses received from PubMed into summary text.
     */
    @Test
    public void parseContent() throws Exception {
        String firstHtmlResponse = "<?xml version=\"1.0\" encoding=\"utf-8\"?> <!DOCTYPE html PUBLIC \"-//W3C//DTD " +
                "XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> <pre> 1: Miao Q," +
                " Ping X, Tang X, Zhang L, Zhang X, Cheng Y, Shentu X. Experimental assessment of novel PAX6 splicing" +
                " mutations in two Chinese families with aniridia. Gene. 2017 Jul 28. pii: S0378-1119(17)30609-1. " +
                "doi: 10.1016/j.gene.2017.07.073. [Epub ahead of print] PubMed PMID: 28760551.  </pre>";
        String firstExpected = "1: Miao Q, Ping X, Tang X, Zhang L, Zhang X, Cheng Y, Shentu X. Experimental " +
                "assessment of novel PAX6 splicing mutations in two Chinese families with aniridia. Gene. 2017 Jul " +
                "28. pii: S0378-1119(17)30609-1. doi: 10.1016/j.gene.2017.07.073. [Epub ahead of print] PubMed PMID: " +
                "28760551.";
        assertEquals(firstExpected, RetrievePubMedSummary.parseContent(firstHtmlResponse));

        String secondHtmlResponse = "<?xml version=\"1.0\" encoding=\"utf-8\"?> <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML" +
                " 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> <pre> 1: " +
                "Rauramo L, Lagerspetz K, Engblom P, Punnonen R. The effect of castration and peroral estrogen " +
                "therapy on some psychological functions. Front Horm Res. 1975;3:94-104. PubMed PMID: 1234567.  </pre>";
        String secondExpected = "1: Rauramo L, Lagerspetz K, Engblom P, Punnonen R. The effect of castration " +
                "and peroral estrogen therapy on some psychological functions. Front Horm Res. 1975;3:94-104. PubMed " +
                "PMID: 1234567.";
        assertEquals(secondExpected, RetrievePubMedSummary.parseContent(secondHtmlResponse));
    }


    /**
     * Test retrieval of HTML response from PubMed for selected PMIDs. Wait together maximum of 10s to get the
     * response.
     */
    @Test(timeout = 10000)
    public void getResponse() throws Exception {
        URL firstUrl = new URL(String.format(RetrievePubMedSummary.URL_TEMPLATE, 28760551));
        URL secondUrl = new URL(String.format(RetrievePubMedSummary.URL_TEMPLATE, 1234567));
        String firstActual = RetrievePubMedSummary.getResponse(firstUrl);
        String secondActual = RetrievePubMedSummary.getResponse(secondUrl);

        String firstExpected = "<?xml version=\"1.0\" encoding=\"utf-8\"?> <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML " +
                "1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> <pre> 1: Miao Q," +
                " Ping X, Tang X, Zhang L, Zhang X, Cheng Y, Shentu X. Experimental assessment of novel PAX6 splicing" +
                " mutations in two Chinese families with aniridia. Gene. 2017 Sep 30;630:44-48. doi: 10.1016/j" +
                ".gene.2017.07.073. Epub 2017 Jul 29. PubMed PMID: 28760551.  </pre>";
        String secondExpected = "<?xml version=\"1.0\" encoding=\"utf-8\"?> <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML" +
                " 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> <pre> 1: " +
                "Rauramo L, Lagerspetz K, Engblom P, Punnonen R. The effect of castration and peroral estrogen " +
                "therapy on some psychological functions. Front Horm Res. 1975;3:94-104. PubMed PMID: 1234567.  </pre>";

        if (!(firstActual == null && secondActual == null)) { // if we get null we're offline & don't want to fail
            assertEquals(firstExpected, firstActual);
            assertEquals(secondExpected, secondActual);
        }
    }


    /**
     * Test that non-existing PMID produces empty optional. Wait maximum of 10s to get the response.
     */
    @Test(timeout = 10000)
    public void getSummaryForUnexistingPmid() throws Exception {
        String pmid = "1234567890123456";
        Optional<String> actual = RetrievePubMedSummary.getSummary(pmid);
        assertFalse(actual.isPresent());
    }
}