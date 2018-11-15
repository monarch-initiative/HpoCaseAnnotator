package org.monarchinitiative.hpo_case_annotator.core.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class contains methods which can be used to retrieve PubMed summary text of publication which corresponds to
 * provided PMID. Use {@link #getSummary(String)} method to get the summary text.
 */
public class RetrievePubMedSummary {

    public static final String URL_TEMPLATE = "https://www.ncbi.nlm.nih.gov/pubmed/%s?report=docsum&format=text";

    public static final String NON_EXISTING_PMID = "cannot get document summary";

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrievePubMedSummary.class);


    /**
     * Enter PMID and get PubMed summary text with description of the corresponding publication.
     *
     * @param pmid String with PMID of the publication.
     * @return {@link Optional} with String of summary text if the retrieval was successful or empty optional in case of
     * failure.
     */
    public static Optional<String> getSummary(String pmid) {
        URL url;
        try {
            url = new URL(String.format(URL_TEMPLATE, pmid));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        String htmlResponse = getResponse(url);
        if (htmlResponse == null) { // this happens e.g. when you're offline
            return Optional.empty();
        } else if (htmlResponse.contains(NON_EXISTING_PMID)) { // the entry for submitted pmid doesn't exist.
            return Optional.empty();
        } else {
            return Optional.of(parseContent(htmlResponse));
        }
    }


    /**
     * Extract the content/summary text from PubMed summary html response.
     *
     * @param html String containing the html response.
     * @return String with summary text. The summary text is trimmed and returned as a single line.
     */
    static String parseContent(String html) {
        int start, stop;
        start = html.indexOf("<pre>") + 5;
        stop = html.indexOf("</pre>");
        String content = html.substring(start, stop);
        return content.replaceAll("\n", "").trim();
    }


    /**
     * Try to retrieve content of the html page localized at provided url. Return the content as a single line.
     *
     * @param url location of the html page.
     * @return String with the html content in single line.
     */
    static String getResponse(URL url) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(" "));
        } catch (IOException e) {
            LOGGER.warn("Unable to retrieve response from PubMed server", e);
            return null;
        }
    }

}
