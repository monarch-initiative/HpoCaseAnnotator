package org.monarchinitiative.hpo_case_annotator.core.io;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.core.Utils;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;

import java.nio.file.Paths;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

public class PublicationDataParserPubmedEpubXmlTest {

    private PublicationDataParserPubmedEpubXml parser;

    @Before
    public void setUp() throws Exception {
        parser = new PublicationDataParserPubmedEpubXml();
    }

    @Test
    public void name() throws Exception {
        String payload = Utils.readFile(Paths.get("src/test/resources/org/monarchinitiative/hpo_case_annotator/core/io/pubmed_eutils_summary.xml"));
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