package org.monarchinitiative.hpo_case_annotator.core.publication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.core.TestUtils;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;

import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

public class PublicationDataParserPubmedEpubXmlTest {

    private PublicationDataParserPubmedEpubXml parser;

    @BeforeEach
    public void setUp() throws Exception {
        parser = new PublicationDataParserPubmedEpubXml();
    }

    @Test
    public void parse() throws Exception {
        String payload = TestUtils.readFile(Paths.get("src/test/resources/org/monarchinitiative/hpo_case_annotator/core/publication/pubmed_eutils_summary.xml"));
        Publication publication = parser.parse(payload);

        assertThat(publication, hasProperty("authorList", equalTo("Niiranen TJ, Vasan RS")));
        assertThat(publication, hasProperty("title", equalTo("Epidemiology of cardiovascular disease: recent novel outlooks on risk factors and clinical approaches.")));
        assertThat(publication, hasProperty("journal", equalTo("Expert Rev Cardiovasc Ther")));
        assertThat(publication, hasProperty("year", equalTo("2016")));
        assertThat(publication, hasProperty("pages", equalTo("855-69")));
        assertThat(publication, hasProperty("volume", equalTo("14(7)")));
        assertThat(publication, hasProperty("pmid", equalTo("27057779")));
    }
}