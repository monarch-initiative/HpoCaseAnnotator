package org.monarchinitiative.hpo_case_annotator.core.publication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.core.Utils;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;

import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

public class PublicationDataParserPubmedFormatTest {

    private PublicationDataParserPubmedFormat parser;

    @BeforeEach
    public void setUp() throws Exception {
        parser = new PublicationDataParserPubmedFormat();
    }

    @Test
    public void parse_epubAheadOfPrint() throws Exception {
        String payload = Utils.readFile(Paths.get("src/test/resources/org/monarchinitiative/hpo_case_annotator/core/publication/pubmed_fmt_epub.txt"));
        Publication publication = parser.parse(payload);

        assertThat(publication, hasProperty("authorList", equalTo("Pauper M, Kucuk E, Wenger AM, Chakraborty S, Baybayan P, Kwint M, van der Sanden B, Nelen MR, Derks R, Brunner HG, Hoischen A, Vissers LELM, Gilissen C")));
        assertThat(publication, hasProperty("title", equalTo("Long-read trio sequencing of individuals with unsolved intellectual disability.")));
        assertThat(publication, hasProperty("journal", equalTo("Eur J Hum Genet")));
        assertThat(publication, hasProperty("year", equalTo("2020")));
        assertThat(publication, hasProperty("pages", equalTo("N/A")));
        assertThat(publication, hasProperty("volume", equalTo("N/A")));
        assertThat(publication, hasProperty("pmid", equalTo("33257779")));

    }

    @Test
    public void parse_fullPublicationRecord() throws Exception {
        String payload = Utils.readFile(Paths.get("src/test/resources/org/monarchinitiative/hpo_case_annotator/core/publication/pubmed_fmt_full.txt"));
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