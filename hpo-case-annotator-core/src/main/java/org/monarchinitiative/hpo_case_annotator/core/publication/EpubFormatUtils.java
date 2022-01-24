package org.monarchinitiative.hpo_case_annotator.core.publication;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class EpubFormatUtils {

    static final Pattern AUTHORS = Pattern.compile("<Author>(?<payload>\\p{all}*?)</Author>");
    static final Pattern AUTHOR_NAME = Pattern.compile("<Name>(?<payload>\\p{all}*)</Name>");
    static final Pattern TITLE = Pattern.compile("<Title>(?<payload>\\p{all}*?)</Title>");
    static final Pattern JOURNAL = Pattern.compile("<Source>(?<payload>.*)</Source>");
    static final Pattern PAGES = Pattern.compile("<Pages>(?<payload>.*)</Pages>");
    static final Pattern ARTICLE_ID = Pattern.compile("<ArticleId>[\\p{all}]*</ArticleId>");
    static final Pattern PMID = Pattern.compile("<Value>(?<payload>\\d+)</Value>");
    static final Pattern VOLUME = Pattern.compile("<Volume>(?<payload>.*)</Volume>");
    static final Pattern ISSUE = Pattern.compile("<Issue>(?<payload>.*)</Issue>");
    static final Pattern PUB_DATE = Pattern.compile("<PubDate>(?<year>\\d{4}).*</PubDate>");
    static final Pattern EPUB_DATE = Pattern.compile("<EPubDate>(?<year>\\d{4}).*</EPubDate>");


    static PublicationData parse(String payload) throws PubMedParseException {
        // author list
        List<String> authors = new ArrayList<>();
        Matcher authorsMatcher = AUTHORS.matcher(payload);
        while (authorsMatcher.find()) {
            Matcher authorNameMatcher = AUTHOR_NAME.matcher(authorsMatcher.group("payload"));
            if (authorNameMatcher.find()) {
                authors.add(authorNameMatcher.group("payload"));
            }
        }

        // title
        String title = null;
        Matcher titleMatcher = TITLE.matcher(payload);
        if (titleMatcher.find()) {
            title = processTitle(titleMatcher.group("payload"));
        }

        // journal
        String journal = null;
        Matcher journalMatcher = JOURNAL.matcher(payload);
        if (journalMatcher.find()) {
            journal = journalMatcher.group("payload");
        }

        // year
        String year = null;
        Matcher pubMatcher = PUB_DATE.matcher(payload);
        if (pubMatcher.find()) {
            year = pubMatcher.group("year");
        }

        if (year == null) {
            // try ePub
            Matcher ePubMatcher = EPUB_DATE.matcher(payload);
            if (ePubMatcher.find()) {
                year = ePubMatcher.group("year");
            }
        }
        if (year == null) {
            throw new PubMedParseException("Missing year");
        }

        // volume
        Matcher volumeMatcher = VOLUME.matcher(payload);
        Matcher issueMatcher = ISSUE.matcher(payload);
        String volume = null;
        if (volumeMatcher.find() && issueMatcher.find()) {
            volume = volumeMatcher.group("payload") + '(' + issueMatcher.group("payload") + ')';
        }

        // pages
        String pages = null;
        Matcher pagesMatcher = PAGES.matcher(payload);
        if (pagesMatcher.find()) {
            pages = pagesMatcher.group("payload");
        }

        // pmid
        String pmid = null;
        Matcher articleIdMatcher = ARTICLE_ID.matcher(payload);
        while (articleIdMatcher.find()) {
            String articleData = payload.substring(articleIdMatcher.start(), articleIdMatcher.end());
            if (articleData.contains("pubmed")) {
                Matcher pmidMatcher = PMID.matcher(articleData);
                if (pmidMatcher.find()) {
                    pmid = pmidMatcher.group("payload");
                    break;
                }
            }
        }
        if (pmid == null) {
            throw new PubMedParseException("PMID not found");
        }

        return new PublicationData(authors, title, journal, year, volume, pages, pmid);
    }

    private static String processTitle(String title) {
        return title.replace('\n', ' ').replaceAll("\\s{2,}", " ").trim();
    }

}
