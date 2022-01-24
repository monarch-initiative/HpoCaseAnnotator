package org.monarchinitiative.hpo_case_annotator.model;

import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Utility functions for manipulation with {@link DiseaseCase} instances.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class ModelUtils {

    private static final Pattern DIACRITICS_AND_FRIENDS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

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
        String firstAuthorsSurname = getFirstAuthorsSurname(publication.getAuthorList());
        String ye = (publication.getYear().isEmpty()) ? "year" : publication.getYear();
        String gn = (model.getGene().getSymbol().isEmpty()) ? "genesymbol" : model.getGene().getSymbol();
        String candidate = String.format("%s-%s-%s", firstAuthorsSurname, ye, gn);
        return makeLegalFileNameWithNoWhitespace(candidate);
    }

    /**
     * Make string that includes:
     * <ul>
     * <li>the first author surname</li>
     * <li>publication year</li>
     * <li>affected gene symbol</li>
     * <li>id of proband/family within the publication</li>
     * </ul>
     *
     * @param model {@link DiseaseCase} to get filename for
     * @return String suitable for using as <code>model</code> filename
     */
    public static String getFileNameWithSampleId(DiseaseCase model) {
        Publication publication = model.getPublication();
        String ye = (publication.getYear().isEmpty()) ? "year" : publication.getYear();
        String gn = (model.getGene().getSymbol().isEmpty()) ? "genesymbol" : model.getGene().getSymbol();
        String sid = model.getFamilyInfo().getFamilyOrProbandId();
        String candidate = String.format("%s-%s-%s-%s", getFirstAuthorsSurname(publication.getAuthorList()), ye, gn, sid);

        candidate = normalizeAsciiText(candidate);

        return makeLegalFileNameWithNoWhitespace(candidate);
    }

    /**
     * Replace non ASCII characters like <code>í</code> with <code>i</code>, etc..
     *
     * @param string to be normalized
     * @return normalized string
     */
    public static String normalizeAsciiText(String string) {
        String candidate = Normalizer.normalize(string, Normalizer.Form.NFD);
        return DIACRITICS_AND_FRIENDS.matcher(candidate).replaceAll("");
    }

    public static String getFirstAuthorsSurname(String authorList) {
        String author;
        if (authorList.isEmpty()) {
            author = "author";
        } else {
            String firstAuthor = authorList.split(",")[0];
            int lastindex = firstAuthor.lastIndexOf(' ');
            if (lastindex >= 0)
                author = firstAuthor.substring(0, lastindex).replaceAll("\\s", "_");
            else
                author = firstAuthor;
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
    private static String getPublicationSummary(Publication publication) {
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

    public static String makeLegalFileNameWithNoWhitespace(String name) {
        return makeLegalFileNameWithNoWhitespace(name, '_');
    }

    public static String makeLegalFileNameWithNoWhitespace(String name, char replacement) {
        char[] chars = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            if (ILLEGAL_CHARACTERS.contains(chars[i])) {
                chars[i] = replacement;
            }
        }
        return new String(chars).replaceAll("\\s+", String.valueOf(replacement));
    }

    /**
     * I don't want these characters in filenames
     */
    private static final Set<Character> ILLEGAL_CHARACTERS = new HashSet<>(Arrays.asList('/', '\n', '\r', '\t',
            '\0', '\f', '`', '?', '*', '\\', '<', '>', '|'));

    public static boolean isLegalFileName(String fileName) {
        if (fileName == null) {
            return false;
        }
        char[] chars = fileName.toCharArray();
        for (int i = 0; i < fileName.length(); i++) {
            if (ILLEGAL_CHARACTERS.contains(chars[i])) {
                return false;
            }
        }

        /* Remove all whitespace characters.
         * http://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java#5455809 */
        String noWhitespace = fileName.replaceAll("\\s+", "");
        return !noWhitespace.equals("");
    }
}
