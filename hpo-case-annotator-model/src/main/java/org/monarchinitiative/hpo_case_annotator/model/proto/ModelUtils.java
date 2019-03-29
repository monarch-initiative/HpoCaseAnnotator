package org.monarchinitiative.hpo_case_annotator.model.proto;

/**
 * Utility functions for manipulation with {@link DiseaseCase} instances.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class ModelUtils {

    private ModelUtils() {
        // private no-op
    }

    /**
     * Make String that is usable as file name of <code>model</code>. No suffix is added!
     *
     * @param model {@link DiseaseCase} to get filename for
     * @return String suitable to be used as filename of <code>model</code>
     */
    public static String getFileNameFor(DiseaseCase model) {
        Publication publication = model.getPublication();
        String ye = (publication.getYear() == null) ? "year" : publication.getYear();
        String gn = (model.getGene().getSymbol() == null) ? "genesymbol" : model.getGene().getSymbol();
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

    /**
     * Create a summary string representing the {@link Publication}
     *
     * @param publication {@link Publication} to be represented
     * @return String like '<em>Ben Mahmoud A, Siala O, Mansour RB, Driss F, Baklouti-Gargouri S, Mkaouar-Rebai E,
     * Belguith N, Fakhfakh F. First functional analysis of a novel splicing mutation ... . Gene. 2013 532(1):13-7.
     * PMID:23954224</em>'
     */
    public static String getPublicationSummary(Publication publication) {
        return new StringBuilder()
                .append(publication.getAuthorList()).append(". ")
                .append(publication.getTitle()).append(". ")
                .append(publication.getJournal()).append(". ")
                .append(publication.getYear()).append(" ")
                .append(publication.getVolume()).append(":")
                .append(publication.getPages()).append(". ")
                .append("PMID:").append(publication.getPmid())
                .toString();
    }
}
