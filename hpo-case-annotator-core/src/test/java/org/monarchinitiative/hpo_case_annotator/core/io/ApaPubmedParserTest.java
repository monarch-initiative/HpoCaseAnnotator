package org.monarchinitiative.hpo_case_annotator.core.io;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApaPubmedParserTest {

    @Test
    public void testAbstract1() throws PubMedParseException {
        String pmid1 = "22981790";
        String sample1 = "Burns, S. O., Zenner, H. L., Plagnol, V., Curtis, J., Mok, K., Eisenhut, M., Kumararatne, D., Doffinger, R., Thrasher, A. J., & Nejentsev, S. (2012). LRBA gene deletion in a patient presenting with autoimmunity without hypogammaglobulinemia. The Journal of allergy and clinical immunology, 130(6), 1428–1432. https://doi.org/10.1016/j.jaci.2012.07.035";
        PubMedParser parser = new ApaPubMedParser(sample1, pmid1);

        Optional<PubMedParser.Result> opt = parser.parsePubMed();
        assertTrue(opt.isPresent());
        PubMedParser.Result result = opt.get();
        String authors = "Burns, S. O., Zenner, H. L., Plagnol, V., Curtis, J., Mok, K., Eisenhut, M., Kumararatne, D., Doffinger, R., Thrasher, A. J., & Nejentsev, S.";
        assertEquals(authors, result.getAuthorList());
        String year = "2012";
        assertEquals(year, result.getYear());
        String title = "LRBA gene deletion in a patient presenting with autoimmunity without hypogammaglobulinemia";
        String journal = "The Journal of allergy and clinical immunology";
        String volume = "130(6)";
        String pages = "1428–1432";
        assertEquals(title, result.getTitle());
        assertEquals(journal, result.getJournal());
        assertEquals(volume, result.getVolume());
        assertEquals(pages, result.getPages());
        assertEquals(pmid1, result.getPmid());
    }


}
