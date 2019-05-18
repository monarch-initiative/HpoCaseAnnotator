package org.monarchinitiative.hpo_case_annotator.core.io;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * These tests test {@link PubMedParser} and its parsing of PubMed summary (text) string. The parser doesn't perform
 * well in processing malformed strings yet so tests are focused on testing of good input.
 */
public class PubMedParserTest {

    /**
     * Test correctness of parsing of good input.
     *
     * @throws Exception
     */
    @Test
    public void testParsePubMedOne() throws Exception {
        final String sampleOne = "1: van Bon BW, Balciuniene J, Fruhman G, Nagamani SC, Broome DL, Cameron" +
                " E, Martinet D, Roulet E, Jacquemont S, Beckmann JS, Irons M, Potocki L, Lee B, Cheung SW, Patel A, " +
                "Bellini M, Selicorni A, Ciccone R, Silengo M, Vetro A, Knoers NV, de Leeuw N, Pfundt R, Wolf B, Jira P, " +
                "Aradhya S, Stankiewicz P, Brunner HG, Zuffardi O, Selleck SB, Lupski JR, de Vries BB. The phenotype of " +
                "recurrent 10q22q23 deletions and duplications. Eur J Hum Genet. 2011 Apr;19(4):400-8. doi: " +
                "10.1038/ejhg.2010.211. Epub 2011 Jan 19. PubMed PMID: 21248748; PubMed Central PMCID: PMC3060324.";

        PubMedParser.Result result = PubMedParser.parsePubMed(sampleOne);

        assertEquals("van Bon BW, Balciuniene J, Fruhman G, Nagamani SC, Broome DL, Cameron E, Martinet D, Roulet E, Jacquemont S, Beckmann JS, Irons M, Potocki L, Lee B, Cheung SW, Patel A, Bellini M, Selicorni A, Ciccone R, Silengo M, Vetro A, Knoers NV, de Leeuw N, Pfundt R, Wolf B, Jira P, Aradhya S, Stankiewicz P, Brunner HG, Zuffardi O, Selleck SB, Lupski JR, de Vries BB", result.getAuthorList());
//        assertEquals("van Bon", result.getAuthorList());
        assertEquals("The phenotype of recurrent 10q22q23 deletions and duplications", result.getTitle());
        assertEquals("Eur J Hum Genet", result.getJournal());
        assertEquals("2011", result.getYear());
        assertEquals("19(4)", result.getVolume());
        assertEquals("400-8", result.getPages());
        assertEquals("21248748", result.getPmid());
    }


    @Test
    public void testParsePubMedTwo() throws Exception {
        final String sampleTwo = "1: Wessagowit V, Nalla VK, Rogan PK, McGrath JA. Normal and abnormal " +
                "mechanisms of gene splicing and relevance to inherited skin diseases. J Dermatol Sci. 2005 Nov;40(2):73" +
                "-84. Epub 2005 Jul 27. Review. PubMed PMID: 16054339; PubMed Central PMCID: PMC1351063.";
        PubMedParser.Result result = PubMedParser.parsePubMed(sampleTwo);

        assertEquals("Wessagowit V, Nalla VK, Rogan PK, McGrath JA", result.getAuthorList());
//        assertEquals("Wessagowit", publication.getFirstAuthorSurname());
        assertEquals("Normal and abnormal mechanisms of gene splicing and relevance to inherited skin diseases", result.getTitle());
        assertEquals("J Dermatol Sci", result.getJournal());
        assertEquals("2005", result.getYear());
        assertEquals("40(2)", result.getVolume());
        assertEquals("73-84", result.getPages());
        assertEquals("16054339", result.getPmid());
    }


    @Test
    public void testParsePubMedThree() throws Exception {
        // The following string has an unusual format with pii:
        final String sampleThree = "1: Sandal S, Kaur A, Panigrahi I. Novel mutation in the CHST14 gene causing\n" +
                "musculocontractural type of Ehlers-Danlos syndrome. BMJ Case Rep. 2018 Sep\n" +
                "23;2018. pii: bcr-2018-226165. doi: 10.1136/bcr-2018-226165. PubMed PMID:\n" +
                "30249733.";
        PubMedParser.Result result = PubMedParser.parsePubMed(sampleThree);
        assertEquals("Sandal S, Kaur A, Panigrahi I", result.getAuthorList());
//        assertEquals("Wessagowit", publication.getFirstAuthorSurname());
        assertEquals("Novel mutation in the CHST14 gene causing musculocontractural type of Ehlers-Danlos syndrome", result.getTitle());
        assertEquals("BMJ Case Rep", result.getJournal());
        assertEquals("2018", result.getYear());
        assertEquals("pii: ", result.getVolume());
        assertEquals("bcr-2018-226165", result.getPages());
        assertEquals("30249733", result.getPmid());


    }

}