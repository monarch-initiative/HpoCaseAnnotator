package org.monarchinitiative.hpo_case_annotator.io;


import org.monarchinitiative.hpo_case_annotator.model.Publication;

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

    private String errorString;


    /**
     * Expecting something like this:
     *
     * <code>1: Nanji MS, Nguyen VT, Kawasoe JH, Inui K, Endo F, Nakajima T, Anezaki T, Cox DW. Haplotype and mutation
     * analysis in Japanese patients with Wilson disease. Am  J Hum Genet. 1997 Jun;60(6):1423-9. PubMed PMID: 9199563;
     * PubMed Central PMCID: PMC1716137.</code>
     *
     * @param data String with PubMed summary text
     * @return {@link Optional} of {@link Publication} object.
     */
    public Optional<Publication> parsePubMed(String data) {
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
            return Optional.empty();
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
            title = "??";
            errorString = String.format("Unable to parse the title from the PubMed data (I attempted to find the title after the first and prior to the second period but failed): %s", data);
            return Optional.empty();
        }
        currentString = currentString.substring(x + 1);
        x = currentString.indexOf(".");
        if (x > 0) {
            journal = currentString.substring(0, x).trim();
        } else {
            errorString = String.format("Unable to parse the journal from the PubMed data: %s", data);
            return Optional.empty();
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
                return Optional.empty();
            }
            String doi = getDoi(currentString);
            publicationVolume = "[Epub ahead of print]";
            publicationPages = doi;
        } else {
            x = currentString.indexOf(";");
            if (x < 0) {
                errorString = String.format("Unable to parse the date substring (%s) in the pubmed entry %s (did not find \";\")", currentString, data);
                return Optional.empty();
            }
            String datestring = currentString.substring(0, x);
            String year = getYear(datestring);
            if (year == null) {
                return Optional.empty();
            } else {
                publicationYear = year;
            }
            currentString = currentString.substring(x + 1).trim();
            x = currentString.indexOf(".");
            if (x < 0) {
                errorString = String.format("Could not volume/pages in String %s", data);
                return Optional.empty();
            }
            String[] volumeAndPages = parseVolumeAndPages(currentString.substring(0, x));
            if (volumeAndPages[0] == null && volumeAndPages[1] == null) {
                errorString = String.format("Could not volume/pages in String %s", data);
                return Optional.empty();
            } else {
                publicationVolume = volumeAndPages[0];
                publicationPages = volumeAndPages[1];
            }
        }
        // We should now have something like this: PubMed PMID: 9199563; PubMed Central PMCID: PMC1716137.
        x = data.indexOf("PMID:");
        if (x < 0) {
            errorString = String.format("Could not identify PMID: substring in PubMed input %s", data);
            return Optional.empty();
        }
        data = data.substring(x + 5).trim();
        pos = 0;
        while (Character.isDigit(data.charAt(pos)))
            pos++;
        pmid = data.substring(0, pos);
        return Optional.of(new Publication(authorlist, title, journal, publicationYear,
                publicationVolume, publicationPages, pmid));
    }


    /**
     * Parse out the author list, removing first number (1: ) if necessary.
     *
     * @param s A string like  1: Nanji MS, Nguyen VT, Kawasoe JH, Inui K, Endo F, Nakajima T, Anezaki T, Cox DW.
     */
    private String parseAuthors(String s) {
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


    public String getErrorString() {
        return errorString;
    }


    /**
     * Here, we extract a DOI string from a pubmed entry. We are exprecting to get a string that looks like this
     * <pre>
     * 2014 Dec 25. doi: 10.1002/humu.22745. [Epub ahead of print]
     * </pre>
     */
    private String getDoi(String s) {
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
    private String[] parseVolumeAndPages(String s) {
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
    private String getYear(String str) {
        Pattern pattern = Pattern.compile("[12]\\d{3}");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return null;
        }
    }

}

/* eof */
