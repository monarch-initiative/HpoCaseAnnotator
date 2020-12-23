package org.monarchinitiative.hpo_case_annotator.core.io;

import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PublicationDataParserPubmedEpubXml implements PublicationDataParser {


    private static final Pattern AUTHORS = Pattern.compile("<Author>(?<payload>\\p{all}*?)</Author>");
    private static final Pattern AUTHOR_NAME = Pattern.compile("<Name>(?<payload>\\p{all}*)</Name>");
    private static final Pattern TITLE = Pattern.compile("<Title>(?<payload>\\p{all}*?)</Title>");
    private static final Pattern JOURNAL = Pattern.compile("<Source>(?<payload>.*)</Source>");
    private static final Pattern PAGES = Pattern.compile("<Pages>(?<payload>.*)</Pages>");
    private static final Pattern ARTICLE_ID = Pattern.compile("<ArticleId>[\\p{all}]*</ArticleId>");
    private static final Pattern PMID = Pattern.compile("<Value>(?<payload>\\d+)</Value>");
    private static final Pattern VOLUME = Pattern.compile("<Volume>(?<payload>.*)</Volume>");
    private static final Pattern ISSUE = Pattern.compile("<Issue>(?<payload>.*)</Issue>");
    private static final Pattern PUB_DATE = Pattern.compile("<PubDate>(?<year>\\d{4}).*</PubDate>");
    private static final Pattern EPUB_DATE = Pattern.compile("<EPubDate>(?<year>\\d{4}).*</EPubDate>");

    @Override
    public Publication parse(String payload) throws PubMedParseException {
        Publication.Builder builder = Publication.newBuilder();
        // author list
        List<String> authors = new ArrayList<>();
        Matcher authorsMatcher = AUTHORS.matcher(payload);
        while (authorsMatcher.find()) {
            Matcher authorNameMatcher = AUTHOR_NAME.matcher(authorsMatcher.group("payload"));
            if (authorNameMatcher.find()) {
                authors.add(authorNameMatcher.group("payload"));
            }
        }
        builder.setAuthorList(String.join(", ", authors));

        // title
        Matcher titleMatcher = TITLE.matcher(payload);
        if (titleMatcher.find()) {
            builder.setTitle(titleMatcher.group("payload").replace('\n', ' ').replaceAll("\\s{2,}", " ").trim());
        }

        // journal
        Matcher journalMatcher = JOURNAL.matcher(payload);
        if (journalMatcher.find()) {
            builder.setJournal(journalMatcher.group("payload"));
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
        builder.setYear(year);

        // volume
        Matcher volumeMatcher = VOLUME.matcher(payload);
        Matcher issueMatcher = ISSUE.matcher(payload);
        if (volumeMatcher.find() && issueMatcher.find()) {
            builder.setVolume(volumeMatcher.group("payload") + '(' + issueMatcher.group("payload") + ')');
        }

        // pages
        Matcher pagesMatcher = PAGES.matcher(payload);
        if (pagesMatcher.find()) {
            builder.setPages(pagesMatcher.group("payload"));
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
        builder.setPmid(pmid);

        return builder.build();
    }
}
