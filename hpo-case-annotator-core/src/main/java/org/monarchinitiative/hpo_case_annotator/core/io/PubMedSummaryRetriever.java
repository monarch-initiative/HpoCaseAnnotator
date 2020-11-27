package org.monarchinitiative.hpo_case_annotator.core.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class contains methods which can be used to retrieve PubMed summary text of publication which corresponds to
 * provided PMID. Use {@link #getSummary(String)} method to get the summary text.
 * NO LONGER WORKS FOLLOWING 2020 PubMed changes!
 */
@Deprecated
public class PubMedSummaryRetriever {

    /**
     * This is a template for URL targeted for PubMed's REST API.
     */
    public static final String URL_TEMPLATE = "https://www.ncbi.nlm.nih.gov/pubmed/%s?report=docsum&format=text";

    /**
     * PubMed returns a response containing this String, if there is no record associated with given PMID.
     */
    public static final String NON_EXISTING_PMID = "cannot get document summary";

    private static final Logger LOGGER = LoggerFactory.getLogger(PubMedSummaryRetriever.class);

    private final Function<String, InputStream> connectionFactory;


    public PubMedSummaryRetriever(Function<String, InputStream> connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * Use default connection factory that is available through method {@link PubMedSummaryRetriever#getConnectionFactory()}.
     * <p>
     * This constructor should work in most cases.
     */
    public PubMedSummaryRetriever() {
        this(getConnectionFactory());
    }

    /**
     * Enter PMID and retrieve PubMed summary text with description of the corresponding publication.
     *
     * @param pmid String with PMID of the publication.
     * @return String with summary text if the retrieval was successful
     * @throws IOException if retrieval fails
     */
    public static String getSummary(InputStream is, String pmid) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String htmlResponse = reader.lines().collect(Collectors.joining(" "));

        if (htmlResponse.contains(NON_EXISTING_PMID)) { // the entry for submitted pmid doesn't exist.
            throw new IOException("PMID " + pmid + " is not associated with any publication on Pubmed");
        } else {
            return extractContent(htmlResponse);
        }
    }

    /**
     * PubMed API returns whole HTML page in response to the query. This method extracts the relevant part of the response.
     *
     * @param payload String with HTML page
     * @return String with extracted
     */
    private static String extractContent(String payload) {
        int start, stop;
        start = payload.indexOf("<pre>") + 5;
        stop = payload.indexOf("</pre>");
        String content = payload.substring(start, stop);
        return content.replaceAll("\n", "").trim();
    }


    public static Function<String, InputStream> getConnectionFactory() {
        return pmid -> {
            String urlString = String.format(URL_TEMPLATE, pmid);
            try {
                URL url = new URL(urlString);
                return url.openStream();
            } catch (IOException e) {
                LOGGER.warn("Error when trying to open URL '{}' for reading", urlString, e);
                return null;
            }
        };
    }

    public String getSummary(String pmid) throws IOException {
        try (InputStream is = connectionFactory.apply(pmid)) {
            return getSummary(is, pmid);
        }
    }
}
