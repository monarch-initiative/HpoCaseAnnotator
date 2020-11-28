package org.monarchinitiative.hpo_case_annotator.core.io;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NlmPubMedParserTest {

    @Test
    public void testAbstract1() {
        String pmid1 = "17492313";
        String sample1 = "Mátyás G, Alonso S, Patrignani A, Marti M, Arnold E, Magyar I, Henggeler C, Carrel T, Steinmann B, Berger W. Large genomic fibrillin-1 (FBN1) gene deletions provide evidence for true haploinsufficiency in Marfan syndrome. Hum Genet. 2007 Aug;122(1):23-32. doi: 10.1007/s00439-007-0371-x. Epub 2007 May 10. PMID: 17492313.";
        PubMedParser parser = new NlmPubMedParser(sample1, pmid1);
        Optional<PubMedParser.Result> opt = parser.parsePubMed();
        assertTrue(opt.isPresent());
        PubMedParser.Result result = opt.get();
        String authors = "Mátyás G, Alonso S, Patrignani A, Marti M, Arnold E, Magyar I, Henggeler C, Carrel T, Steinmann B, Berger W";
        assertEquals(authors, result.getAuthorList());
        //Large genomic fibrillin-1 (FBN1) gene deletions provide evidence for true haploinsufficiency in Marfan syndrome.
        // Hum Genet. 2007 Aug;:23-32.
        String title = "Large genomic fibrillin-1 (FBN1) gene deletions provide evidence for true haploinsufficiency in Marfan syndrome";
        String journal = "Hum Genet";
        String year = "2007";
        String volume = "122(1)";
        String pages = "23-32";
        assertEquals(title, result.getTitle());
        assertEquals(journal, result.getJournal());
        assertEquals(year, result.getYear());
        assertEquals(volume, result.getVolume());
        assertEquals(pages, result.getPages());
        assertEquals(pmid1, result.getPmid());

    }
}
