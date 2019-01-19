package org.monarchinitiative.hpo_case_annotator.core.io;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * This parser is designed for /dat/OMIM.tsv file. The file is indeed in TSV format with following structure:
 * 1st column -> name of entry
 * 2nd column -> canonical name of entry
 * 3rd column -> mim ID of entry
 * <p>
 * Each OMIM entry has single mim ID, single canonical name, but more "names" (synonyms). We expect tsv file
 * to be sorted by MIMID.
 *
 * @author Daniel Danis
 */
public final class OMIMParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(OMIMParser.class);

    /**
     * Map containing MIM IDs as keys and canonical names as values.
     */
    private final Map<String, String> mimid2canonicalName = new HashMap<>();

    /**
     * Map containing canonical names as keys and MIM IDs as values.
     */
    private final Map<String, String> canonicalName2mimid = new HashMap<>();

//
//    public OMIMParser(File file) throws IOException {
//        this(new FileReader(file));
//    }
//
//
//    public OMIMParser(URL url) throws IOException, URISyntaxException {
//        this(Files.newBufferedReader(Paths.get(url.toURI())));
//    }

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
            Alert a = new Alert(AlertType.INFORMATION);
            a.setTitle("Parse of OMIM file");
            a.setHeaderText("Bad format of OMIM file!");
            a.setContentText("First line does not start with '#' character");
            a.showAndWait();
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


    public Map<String, String> getMimid2canonicalName() {
        return mimid2canonicalName;
    }


    public Map<String, String> getCanonicalName2mimid() {
        return canonicalName2mimid;
    }

}
