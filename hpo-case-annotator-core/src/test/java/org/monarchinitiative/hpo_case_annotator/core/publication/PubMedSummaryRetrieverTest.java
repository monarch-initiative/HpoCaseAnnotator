package org.monarchinitiative.hpo_case_annotator.core.publication;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.core.TestUtils;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertThrows;


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
        String htmlResponse = TestUtils.readFile(Paths.get("src/test/resources/org/monarchinitiative/hpo_case_annotator/core/publication/pubmed_eutils_summary.xml"));

        Function<String, InputStream> connectionFactory = p -> new ByteArrayInputStream(htmlResponse.getBytes());
        PubMedSummaryRetriever<Publication> instance = PubMedSummaryRetriever.<Publication>builder()
                .publicationDataParser(PublicationDataParser.forV1PublicationFormat(PublicationDataFormat.EUTILS))
                .connectionFactory(connectionFactory).build();
        Publication publication = instance.getPublication("27057779");

        assertThat(publication, hasProperty("authorList", equalTo("Niiranen TJ, Vasan RS")));
        assertThat(publication, hasProperty("title", equalTo("Epidemiology of cardiovascular disease: recent novel outlooks on risk factors and clinical approaches.")));
        assertThat(publication, hasProperty("journal", equalTo("Expert Rev Cardiovasc Ther")));
        assertThat(publication, hasProperty("year", equalTo("2016")));
        assertThat(publication, hasProperty("pages", equalTo("855-69")));
        assertThat(publication, hasProperty("volume", equalTo("14(7)")));
        assertThat(publication, hasProperty("pmid", equalTo("27057779")));
    }


    /**
     * Test that non-existing PMID throws IOException.
     */
    @Test
    public void getSummaryForUnexistingPmid() throws Exception {
        String unfoundResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE eSummaryResult PUBLIC \"-//NLM//DTD esummary pubmed 20160808//EN\" \"https://eutils.ncbi.nlm.nih.gov/eutils/dtd/20160808/esummary_pubmed.dtd\">\n" +
                "<eSummaryResult>\n" +
                "<DocumentSummarySet status=\"OK\">\n" +
                "<DbBuild>Build201222-2212m.3</DbBuild>\n" +
                "<DocumentSummary uid=\"12346643234253432\">\n" +
                "<error>cannot get document summary</error>\n" +
                "</DocumentSummary>\n" +
                "\n" +
                "</DocumentSummarySet>\n" +
                "</eSummaryResult>\n";
        PubMedSummaryRetriever<Publication> retriever = PubMedSummaryRetriever.<Publication>builder()
                .connectionFactory(p -> new ByteArrayInputStream(unfoundResponse.getBytes()))
                .publicationDataParser(PublicationDataParser.forV1PublicationFormat(PublicationDataFormat.EUTILS))
                .build();
        assertThrows(IOException.class, () -> retriever.getPublication("12346643234253432"));
    }
}