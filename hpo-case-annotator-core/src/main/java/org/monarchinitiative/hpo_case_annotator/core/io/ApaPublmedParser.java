package org.monarchinitiative.hpo_case_annotator.core.io;

import java.util.Optional;

/**
 * Format is like this
 * "Burns, S. O., Zenner, H. L., Plagnol, V., Curtis, J., Mok, K., Eisenhut, M., Kumararatne, D., Doffinger, R., Thrasher, A. J., & Nejentsev, S. (2012). LRBA gene deletion in a patient presenting with autoimmunity without hypogammaglobulinemia. The Journal of allergy and clinical immunology, 130(6), 1428–1432. https://doi.org/10.1016/j.jaci.2012.07.035";
 * i.e.,
 * Authors (year). Title. Journal, vol(issue),pages. Otherstuff
 */
public class ApaPublmedParser extends PubMedParser2020 {

    public ApaPublmedParser(String pubmedText, String pmid){
        super(pubmedText, pmid);
    }

    @Override
    public Optional<Result> parsePubMed() throws PubMedParseException {
       // Try to split the string into the authors, the year and the rest
        // we expect ....(2012)....
        // Try to split the string into the authors, the year and the rest
        // we expect ....(2012)....
        int i = this.originalAbstractSummary.indexOf("(");
        if (i<=0) return Optional.empty();
        String authors = this.originalAbstractSummary.substring(0,i).trim();
        int j = this.originalAbstractSummary.indexOf(").", i);
        if (j < i) return Optional.empty();
        Integer year = Integer.parseInt(this.originalAbstractSummary.substring(i+1,j));
        String remaining = this.originalAbstractSummary.substring(j+2).trim();
        String [] fields = remaining.split("\\.");
        if (fields.length < 2) {
            System.err.println("Splitting on period did not yield two fields");
            return Optional.empty();
        }
        String title = fields[0];
        String []journalFields = fields[1].split(",");
        if (journalFields.length < 3) {
            System.err.println("Splitting on comma did not yield three fields");
            return Optional.empty();
        }
        String journal = journalFields[0].trim();
        String volume = journalFields[1].trim();
        String pages = journalFields[2].trim();
        return Optional.of(new Result(authors, title, journal, year.toString(), volume, pages, this.pmid));
    }

}
