package org.monarchinitiative.hpo_case_annotator.io;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
public class OMIMParser {

    private static final String omimResourceFile = "/dat/omim.tsv";

    /**
     * Map containing MIM IDs as keys and canonical names as values.
     */
    private final Map<String, String> mimid2canonicalName;

    /**
     * Map containing canonical names as keys and MIM IDs as values.
     */
    private final Map<String, String> canonicalName2mimid;


    public OMIMParser() {
        this.mimid2canonicalName = new HashMap<>();
        this.canonicalName2mimid = new HashMap<>();
        initialize();
    }


    /**
     * Parse content of omimResourceFile and populate maps with data.
     */
    private void initialize() {

        InputStream is = OMIMParser.class.getResourceAsStream(omimResourceFile);

        BufferedReader breader;

        try {
            breader = new BufferedReader(new InputStreamReader(is));

            String line = null;

            line = breader.readLine(); // Very first line is header (starting with '#' character)
            if (!line.startsWith("#")) {
                Alert a = new Alert(AlertType.INFORMATION);
                a.setTitle("Parse of OMIM file");
                a.setHeaderText("Bad format of OMIM file!");
                a.setContentText("First line does not start with '#' character");
                a.showAndWait();
                breader.close();
            }

            /* Here we are interested in populating the OMIM beans within 'mimid2OMIM'
             * map using only the FIRST occurence of MIMID. Therefore we expect
             * OMIM.tab to be sorted by MIMID.
             */
            String current = "-12548"; // dummy value
            while ((line = breader.readLine()) != null) {
                /* We are dealing with tsv file here. We try to split each line into 3 fields:
                 * 1st -> name; 2nd -> canonical name; 3rd -> mimID */

                String fields[] = line.split("\t");
                String omimName = fields[0];
                String omimCanonicalName = fields[1];
                String mimID = fields[2];

                if (mimID.equals(current)) {
                    continue;
                }

                current = mimID;
                mimid2canonicalName.put(mimID, omimCanonicalName);
                canonicalName2mimid.put(omimCanonicalName, mimID);

            }
            breader.close();
        } catch (IOException e) {
            System.err.println(String.format("Error occured during parsing of OMIM file. \n%s",
                    e.getMessage()));
            e.printStackTrace();
        }
    }


    public Map<String, String> getMimid2canonicalName() {
        return mimid2canonicalName;
    }


    public Map<String, String> getCanonicalName2mimid() {
        return canonicalName2mimid;
    }

}
