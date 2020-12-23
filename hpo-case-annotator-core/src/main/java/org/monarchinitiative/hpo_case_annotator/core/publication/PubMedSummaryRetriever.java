package org.monarchinitiative.hpo_case_annotator.core.publication;

import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class contains methods which can be used to retrieve {@link Publication} data which corresponds to
 * provided PMID. Use {@link #getPublication(String)} method to get the {@link Publication}.
 */
public class PubMedSummaryRetriever {

    /**
     * This is a template for URL targeted for PubMed's REST API.
     */
    private static final String URL_TEMPLATE = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi";

    /**
     * PubMed returns a response containing this String, if there is no record associated with given PMID.
     */
    private static final String NON_EXISTING_PMID = "Sorry, can't find the page or item you're requesting.";

    private static final Logger LOGGER = LoggerFactory.getLogger(PubMedSummaryRetriever.class);

    private static final PubMedSummaryRetriever INSTANCE = builder().build();

    private final Function<String, InputStream> connectionFactory;

    private final PublicationDataParser parser;

    private PubMedSummaryRetriever(Builder builder) {
        this.connectionFactory = Objects.requireNonNull(builder.connectionFactory);
        this.parser = Objects.requireNonNull(builder.publicationDataParser);
    }

    public static PubMedSummaryRetriever defaultInstance() {
        return INSTANCE;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * PubMed API returns whole HTML page in response to the query. This method extracts the relevant part of the response.
     *
     * @param payload String with HTML content
     * @return String with the relevant content
     */
    private static String extractContent(String payload) {
        // extract the visible content enclosed in tags:
        // <pre class="article-details" id="article-details">ARTICLE_TEXT_HERE</pre>
        int start, stop;
        start = payload.indexOf("<pre class=\"article-details\" id=\"article-details\">") + 50;
        stop = payload.indexOf("</pre>");
        return payload.substring(start, stop);
    }


    private static Function<String, InputStream> getConnectionFactory() {
        return pmid -> {
            Map<String, String> params = new HashMap<>();
            params.put("id", pmid);
            params.put("db", "pubmed");
            params.put("version", "2.0");
            try {
                URL url = new URL(URL_TEMPLATE);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(getParamsString(params));
                out.flush();
                out.close();

                return conn.getInputStream();
            } catch (IOException e) {
                LOGGER.warn("Error when trying to open URL for reading", e);
                return null;
            }
        };
    }


    private static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }


    public Publication getPublication(String pmid) throws IOException, PubMedParseException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connectionFactory.apply(pmid)))) {
            String payload = reader.lines().collect(Collectors.joining("\n"));
            if (payload.contains(NON_EXISTING_PMID)) {
                throw new IOException("PMID " + pmid + " is not associated with any publication on Pubmed");
            }
            return parser.parse(payload);
        }
    }

    public static class Builder {

        private Function<String, InputStream> connectionFactory = getConnectionFactory();

        private PublicationDataParser publicationDataParser = PublicationDataParser.forFormat(PublicationDataFormat.EUTILS);

        private Builder() {
        }

        public Builder connectionFactory(Function<String, InputStream> connectionFactory) {
            this.connectionFactory = connectionFactory;
            return this;
        }

        public Builder publicationDataParser(PublicationDataParser parser) {
            this.publicationDataParser = parser;
            return this;
        }

        public PubMedSummaryRetriever build() {
            return new PubMedSummaryRetriever(this);
        }
    }
}
