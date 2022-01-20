package org.monarchinitiative.hpo_case_annotator.core.io;

import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseIdentifier;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * This parser is designed for /dat/OMIM.tsv file. The file is indeed in TSV format with following structure:
 * 1st column -> name of entry
 * 2nd column -> canonical name of entry
 * 3rd column -> mim ID of entry
 * <p>
 * Each OMIM entry has single mim ID, single canonical name, but more "names" (synonyms).
 *
 * @author Daniel Danis
 */
public final class OMIMParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(OMIMParser.class);
    private static final String OMIM_PREFIX = "OMIM";

    /**
     * Map containing MIM IDs as keys and canonical names as values.
     */
    private final Map<String, String> mimid2canonicalName = new HashMap<>();

    /**
     * Map containing canonical names as keys and MIM IDs as values.
     */
    private final Map<String, String> canonicalName2mimid = new HashMap<>();

    public OMIMParser(InputStream inputStream, Charset charset) throws IOException {
        this(new InputStreamReader(inputStream, charset));
    }

    /**
     * Parse content of omimResourceFile and populate maps with data.
     */
    public OMIMParser(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine(); // Very first line is header (starting with '#' character)
        // TODO - remove gui code from parser
        if (!line.startsWith("#")) {
            LOGGER.warn("Did not see header line in OMIM file");
        }

        /* Here we are interested in populating the OMIM beans within 'mimid2OMIM'
         * map using only the FIRST occurence of MIMID. Therefore we expect
         * OMIM.tab to be sorted by MIMID.
         */
        String current = "-12548"; // dummy value
        while ((line = bufferedReader.readLine()) != null) {
            /* We are dealing with tsv file here. We try to split each line into 3 fields:
             * 1st -> name; 2nd -> canonical name; 3rd -> mimID */

            String[] fields = line.split("\t");
//                String omimName = fields[0];
            String omimCanonicalName = fields[1];
            String mimID = fields[2];

            if (mimID.equals(current)) {
                continue;
            }

            current = mimID;
            mimid2canonicalName.put(mimID, omimCanonicalName);
            canonicalName2mimid.put(omimCanonicalName, mimID);
        }
    }

    /**
     * Read OMIM file from given path using UTF-8.
     * @param path omim file path
     * @return list of disease identifiers
     * @throws IOException if anything goes wrong!
     */
    public static List<DiseaseIdentifier> loadDiseaseIdentifiers(Path path) throws IOException {
        try (InputStream inputStream = Files.newInputStream(path)) {
            return loadDiseaseIdentifiers(inputStream, StandardCharsets.UTF_8);
        }
    }

    public static List<DiseaseIdentifier> loadDiseaseIdentifiers(InputStream is, Charset charset) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, charset));
        return br.lines()
                .map(toDiseaseIdentifier())
                .flatMap(Optional::stream)
                .distinct()
                .toList();
    }

    private static Function<String, Optional<DiseaseIdentifier>> toDiseaseIdentifier() {
        return line -> {
            if (line.startsWith("#"))
                return Optional.empty(); // skip header

            String[] fields = line.split("\t");
            String omimCanonicalName = fields[1];
            String mimID = fields[2];

            DiseaseIdentifier diseaseIdentifier = DiseaseIdentifier.of(TermId.of(OMIM_PREFIX, mimID), omimCanonicalName);
            return Optional.of(diseaseIdentifier);
        };
    }


    @Deprecated(since = "2.0.0", forRemoval = true)
    public Map<String, String> getMimid2canonicalName() {
        return mimid2canonicalName;
    }


    @Deprecated(since = "2.0.0", forRemoval = true)
    public Map<String, String> getCanonicalName2mimid() {
        return canonicalName2mimid;
    }

}
