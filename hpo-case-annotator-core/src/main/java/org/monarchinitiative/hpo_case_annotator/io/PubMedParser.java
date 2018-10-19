package org.monarchinitiative.hpo_case_annotator.io;


import org.monarchinitiative.hpo_case_annotator.model.PubMed;
import org.monarchinitiative.hpo_case_annotator.model.Publication;

import java.text.ParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class is intended to parse the PubMed abstract short form entered via the GUI such as
 * <pre>
 * 1: Nanji MS, Nguyen VT, Kawasoe JH, Inui K, Endo F, Nakajima T, Anezaki T, Cox DW.
 * Haplotype and mutation analysis in Japanese patients with Wilson disease.
 * Am  J Hum Genet. 1997 Jun;60(6):1423-9. PubMed PMID: 9199563; PubMed Central PMCID: PMC1716137.
 * </pre>
 *
 * @author Peter Robinson
 * @version 0.0.3 (16 June, 2016)
 */
public class PubMedParser {

    /**
     * Expecting something like this:
     *
     * <code>1: Nanji MS, Nguyen VT, Kawasoe JH, Inui K, Endo F, Nakajima T, Anezaki T, Cox DW. Haplotype and mutation
     * analysis in Japanese patients with Wilson disease. Am  J Hum Genet. 1997 Jun;60(6):1423-9. PubMed PMID: 9199563;
     * PubMed Central PMCID: PMC1716137.</code>
     *
     * @param data String with PubMed summary text
     * @return {@link Result} object.
     */
    public static Result parsePubMed(String data) throws PubMedParseException {
        String errorString = null;
        String authorlist, title, journal, publicationYear, publicationVolume,
                publicationPages, pmid;

        data = data.replaceAll("\n", " ");
        data = data.replaceAll("  ", " ");
        String currentString;
        /* First element: The authors list, goes up to the first period */
        int x = data.indexOf(".");
        int pos = 0;
        if (x > 0) {
            authorlist = parseAuthors(data.substring(0, x).trim());
        } else {
            throw new PubMedParseException(String.format("Error parsing author list '%s'", data.substring(0, x).trim()));
        }

        /* Second element: The title */
        currentString = data.substring(x + 1);
        x = currentString.indexOf(".");
        int y = currentString.indexOf("?");
        if (x > 0) {
            title = currentString.substring(0, x).trim();
        } else if (y > 0) { /* title ends in question mark */
            title = currentString.substring(0, y + 1).trim();
            x = y;
        } else {
            errorString = String.format("Unable to parse the title from the PubMed data (I attempted to find the title after the first and prior to the second period but failed): %s", data);
            throw new PubMedParseException(errorString);
        }
        currentString = currentString.substring(x + 1);
        x = currentString.indexOf(".");
        if (x > 0) {
            journal = currentString.substring(0, x).trim();
        } else {
            errorString = String.format("Unable to parse the journal from the PubMed data: %s", data);
            throw new PubMedParseException(errorString);
        }
        /* Now get the year. Note there is a difference for newer entries with Epub ahead of print */
        currentString = currentString.substring(x + 1).trim();
        if (currentString.contains("Epub ahead of print")) {
            // There is a string like this
            // 2014 Dec 25. doi: 10.1002/humu.22745. [Epub ahead of print]
            //PubMed PMID: 25546334.
            publicationYear = getYear(currentString);
            if (publicationYear == null) {
                errorString = String.format("Unable to parse the year from the PubMed data (I attempted to find a String like [12]\\d+{3} but failed): %s", data);
                throw new PubMedParseException(errorString);
            }
            String doi = getDoi(currentString);
            publicationVolume = "[Epub ahead of print]";
            publicationPages = doi;
        } else {
            x = currentString.indexOf(";");
            if (x < 0) {
                errorString = String.format("Unable to parse the date substring (%s) in the pubmed entry %s (did not find \";\")", currentString, data);
                throw new PubMedParseException(errorString);
            }
            String datestring = currentString.substring(0, x);
            String year = getYear(datestring);
            if (year == null) {
                throw new PubMedParseException(errorString);
            } else {
                publicationYear = year;
            }
            currentString = currentString.substring(x + 1).trim();
            x = currentString.indexOf(".");
            if (x < 0) {
                errorString = String.format("Could not volume/pages in String %s", data);
                throw new PubMedParseException(errorString);
            }
            String[] volumeAndPages = parseVolumeAndPages(currentString.substring(0, x));
            if (volumeAndPages[0] == null && volumeAndPages[1] == null) {
                errorString = String.format("Could not volume/pages in String %s", data);
                throw new PubMedParseException(errorString);
            } else {
                publicationVolume = volumeAndPages[0];
                publicationPages = volumeAndPages[1];
            }
        }
        // We should now have something like this: PubMed PMID: 9199563; PubMed Central PMCID: PMC1716137.
        x = data.indexOf("PMID:");
        if (x < 0) {
            errorString = String.format("Could not identify PMID: substring in PubMed input %s", data);
            throw new PubMedParseException(errorString);
        }
        data = data.substring(x + 5).trim();
        pos = 0;
        while (Character.isDigit(data.charAt(pos)))
            pos++;
        pmid = data.substring(0, pos);
        return new Result(authorlist, title, journal, publicationYear,
                publicationVolume, publicationPages, pmid);
    }


    /**
     * Parse out the author list, removing first number (1: ) if necessary.
     *
     * @param s A string like  1: Nanji MS, Nguyen VT, Kawasoe JH, Inui K, Endo F, Nakajima T, Anezaki T, Cox DW.
     */
    private static String parseAuthors(String s) {
        int pos = 0;
        if (s.length() == 0) {
            return "??";
        }
        /* the following code removes the leading '1: ', if possible. */
        while (Character.isDigit(s.charAt(pos))) {
            pos++;
        }
        if (s.charAt(pos) == ':' && s.charAt(pos + 1) == ' ') {
            return s.substring(pos + 2);
        } else {
            return s;
        }

    }


    /**
     * Here, we extract a DOI string from a pubmed entry. We are exprecting to get a string that looks like this
     * <pre>
     * 2014 Dec 25. doi: 10.1002/humu.22745. [Epub ahead of print]
     * </pre>
     */
    private static String getDoi(String s) {
        int len = s.length();
        int i = s.indexOf("doi: ");
        if (i < 0)
            return null;
        int pos = i + 6;
        while (s.charAt(pos) != ' ' && pos < len)
            pos++;
        if (s.charAt(pos - 1) == '.')
            pos--;
        return s.substring(i, pos);
    }


    /**
     * Look for this:10(11):e1004578.#
     */
    private static String[] parseVolumeAndPages(String s) {
        String a[] = s.split(":");
        if (a.length == 2) {
            return a;
        }
        Pattern pattern = Pattern.compile("(\\d+?)\\(\\d+?\\):(.*?)\\.");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            return new String[]{matcher.group(0), matcher.group(1)};
        } else {
            return new String[]{null, null};
        }
    }


    /**
     * Use a regex to parse a String that starts with either "1" or "2" and contains a total of 4 digits, i.e.,
     * represents the year of a publication.
     */
    private static String getYear(String str) {
        Pattern pattern = Pattern.compile("[12]\\d{3}");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return null;
        }
    }


    private PubMedParser() {
        // private no-op
    }


    public static class Result {

        private final String authorList;

        private final String title;

        private final String journal;

        private final String year;

        private final String volume;

        private final String pages;

        private final String pmid;


        private Result(String authorList, String title, String journal, String year, String volume, String pages, String pmid) {
            this.authorList = authorList;
            this.title = title;
            this.journal = journal;
            this.year = year;
            this.volume = volume;
            this.pages = pages;
            this.pmid = pmid;
        }


        public String getAuthorList() {
            return authorList;
        }


        public String getTitle() {
            return title;
        }


        public String getJournal() {
            return journal;
        }


        public String getYear() {
            return year;
        }


        public String getVolume() {
            return volume;
        }


        public String getPages() {
            return pages;
        }


        public String getPmid() {
            return pmid;
        }
    }
}

/* eof */
