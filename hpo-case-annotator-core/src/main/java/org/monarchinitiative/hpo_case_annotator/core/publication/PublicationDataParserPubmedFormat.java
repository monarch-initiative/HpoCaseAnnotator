package org.monarchinitiative.hpo_case_annotator.core.publication;

import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class for parsing content such as shown <a href="https://pubmed.ncbi.nlm.nih.gov/33257779/?format=pubmed">here</a>
 * into a {@link org.monarchinitiative.hpo_case_annotator.model.proto.Publication}.
 */
class PublicationDataParserPubmedFormat implements PublicationDataParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicationDataParserPubmedFormat.class);

    private static Map<String, List<String>> preprocess(String payload) {
        Pattern tagPattern = Pattern.compile("\\p{Upper}+\\s*-\\s*");
        Matcher tagMatcher = tagPattern.matcher(payload);
        List<Integer> begin = new ArrayList<>(), end = new ArrayList<>();
        boolean isFirst = true;
        while (tagMatcher.find()) {
            if (isFirst) {
                begin.add(tagMatcher.start());
                isFirst = false;
                continue;
            }
            begin.add(tagMatcher.start());
            end.add(tagMatcher.start());
        }
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < end.size(); i++) {
            String line = payload.substring(begin.get(i), end.get(i)).replaceAll("\\s{2,}", " ").replace('\n', ' ').trim();
            lines.add(line);
        }

        Map<String, List<String>> values = new HashMap<>();
        Pattern elementPattern = Pattern.compile("^(?<key>\\p{Upper}+)\\s*-\\s*(?<value>[\\p{Graph}\\p{Punct}\\p{Blank}]+)$");
        for (String line : lines) {
            Matcher elementMatcher = elementPattern.matcher(line);
            if (!elementMatcher.matches()) {
                LOGGER.warn("Unable to match line `{}`", line);
                continue;
            }
            String key = elementMatcher.group("key");
            String value = elementMatcher.group("value");
            if (!values.containsKey(key)) {
                values.put(key, new ArrayList<>());
            }
            values.get(key).add(value);
        }
        return values;
    }

    @Override
    public Publication parse(String payload) throws PubMedParseException {
        Publication.Builder builder = Publication.newBuilder();

        Map<String, List<String>> elements = preprocess(payload);

        // author list
        if (elements.containsKey("AU")) {
            builder.setAuthorList(String.join(", ", elements.get("AU")));
        } else {
            throw new PubMedParseException("No author found");
        }

        // title
        if (elements.containsKey("TI")) {
            builder.setTitle(elements.get("TI").get(0));
        } else {
            throw new PubMedParseException("Title not found");
        }

        // journal
        if (elements.containsKey("TA")) {
            builder.setJournal(elements.get("TA").get(0));
        } else {
            throw new PubMedParseException("Journal not found");
        }

        // year
        if (elements.containsKey("DP")) {
            String dateString = elements.get("DP").get(0);

            Set<DateTimeFormatter> formatters = new HashSet<>();
            formatters.add(DateTimeFormatter.ofPattern("yyyy MMM dd"));
            formatters.add(DateTimeFormatter.ofPattern("yyyy MMM"));

            String year = null;
            for (DateTimeFormatter formatter : formatters) {
                try {
                    year = String.valueOf(LocalDate.parse(dateString, formatter).getYear());
                    break;
                } catch (DateTimeParseException ignored) {
                }
            }
            if (year == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Trying to perform naive parse of date string `{}`", dateString);
                }
                String yearString = dateString.substring(0, 4);
                if (yearString.matches("\\d{4}")) {
                    year = yearString;
                } else {
                    throw new PubMedParseException("Date not found");
                }
            }
            builder.setYear(year);
        }

        // volume
        String volume = "N/A";
        if (elements.containsKey("VI") && elements.containsKey("IP")) {
            volume = elements.get("VI").get(0) + '(' + elements.get("IP").get(0) + ')';
        }
        builder.setVolume(volume);

        // pages
        String pages = null;
        if (elements.containsKey("PG")) {
            pages = elements.get("PG").get(0);
        }
        builder.setPages(pages == null ? "N/A" : pages);

        // PMID
        if (elements.containsKey("PMID")) {
            builder.setPmid(elements.get("PMID").get(0));
        } else {
            throw new PubMedParseException("PMID not found");
        }

        return builder.build();
    }

}
