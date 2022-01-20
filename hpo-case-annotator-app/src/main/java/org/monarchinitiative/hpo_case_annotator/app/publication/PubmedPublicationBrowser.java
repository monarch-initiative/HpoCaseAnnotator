package org.monarchinitiative.hpo_case_annotator.app.publication;

import org.monarchinitiative.hpo_case_annotator.app.UrlBrowser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PubmedPublicationBrowser implements PublicationBrowser {

    private static final Logger LOGGER = LoggerFactory.getLogger(PubmedPublicationBrowser.class);

    private static final String PUBMED_BASE_LINK = "https://www.ncbi.nlm.nih.gov/pubmed/%s";
    private static final Pattern PMID_PATTERN = Pattern.compile("\\d+");

    private final UrlBrowser urlBrowser;

    public PubmedPublicationBrowser(UrlBrowser urlBrowser) {
        this.urlBrowser = urlBrowser;
    }

    @Override
    public void browse(String pmid) {
        Matcher matcher = PMID_PATTERN.matcher(pmid);
        if (matcher.matches()) {
            try {
                URL url = new URL(String.format(PUBMED_BASE_LINK, pmid));
                urlBrowser.showDocument(url);
            } catch (MalformedURLException e) {
                LOGGER.warn("Unable to construct PubMed url for PMID {}: {}", pmid, e.getMessage(), e);
            }
        }
    }
}
