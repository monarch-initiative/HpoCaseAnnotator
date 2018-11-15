package org.monarchinitiative.hpo_case_annotator.model.proto;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class Utils {

    public static String getNameFor(DiseaseCase diseaseCase) {
        Publication publication = diseaseCase.getPublication();
        String ye = (publication.getYear() == null) ? "year" : publication.getYear();
        String gn = (diseaseCase.getGene().getSymbol() == null) ? "genesymbol" : diseaseCase.getGene().getSymbol();
        return String.format("%s-%s-%s", getFirstAuthorsSurname(publication), ye, gn);
    }


    public static String getFirstAuthorsSurname(Publication publication) {
        String author;
        if (publication.getAuthorList() == null || publication.getAuthorList().isEmpty()) {
            author = "author";
        } else {
            String firstAuthor = publication.getAuthorList().split(",")[0];
            int lastindex = firstAuthor.lastIndexOf(' ');
            if (lastindex >= 0)
                author = firstAuthor.substring(0, lastindex).replaceAll("\\s", "_");
            else
                author = "author";
        }
        return author;
    }


    private Utils() {
        // private no-op
    }
}
