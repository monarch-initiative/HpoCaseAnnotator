package org.monarchinitiative.hpo_case_annotator.io;

import org.monarchinitiative.hpo_case_annotator.model.HPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Deprecated // ontologizer library is responsible for parsing HP obo file at the moment.
public class HPOParser {

    /**
     * Title of Pop-up window.
     */
    private static final String windowTitle = "Loading of HPO terms";

    private static Logger log = LoggerFactory.getLogger(HPOParser.class);

    /**
     * Path to the hp.obo file
     */
    private String hpo_path;

    private Map<String, HPO> hpoMap;

    /** Map used for autocompletion of HPO term names */
    private Map<String, String> hpoName2id;

    /**
     * Flag used to indicate that parsing was successful.
     */
    private boolean parsingOk = false;


    public HPOParser(String path) {
        this.hpo_path = path;
        this.hpoMap = new HashMap<>();
        this.hpoName2id = new HashMap<>();
        inputFile();
    }


    public Map<String, HPO> getHpoMap() {
        return this.hpoMap;
    }


    private void inputFile() {
        if (this.hpo_path == null) {
//            PopUps.showWarningDialog(windowTitle, "Warning, HPO file is not present in project directory.",
//                    "Download HPO file using Settings menu in order to enable\nautocompletion and validation.");
            return;
        }
        try {
            BufferedReader input = new BufferedReader(new FileReader(this.hpo_path));
            String line;
            boolean interm = false;
            String id = null;
            String name = null;
            while ((line = input.readLine()) != null) {
                if (line.startsWith("[Term]")) {
                    interm = true;
//                    continue;
                } else if (interm && line.startsWith("id:")) {
                    id = line.substring(4).trim();
                } else if (interm && line.startsWith("name:")) {
                    name = line.substring(6).trim();
                } else if (interm && line.isEmpty()) {
                    HPO hpo = new HPO();
                    hpo.setHpoId(id);
                    hpo.setHpoName(name);
                    this.hpoMap.put(id, hpo);
                    this.hpoName2id.put(name, id);
                    id = null;
                    name = null;
                    interm = false;
                }
                //System.out.println(line);
            }
            input.close();
        } catch (IOException ioe) {
//            PopUps.showException(windowTitle, String.format("Problem occurred during parsing HPO file at\n%s",
//                    this.hpo_path), "", ioe);
        }
        parsingOk = true;
    }


    public boolean isParsingOk() {
        return parsingOk;
    }


    public Map<String, String> getHpoName2id() {
        return this.hpoName2id;
    }

}
