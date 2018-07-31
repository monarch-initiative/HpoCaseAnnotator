package org.monarchinitiative.hpo_case_annotator.io;

import org.monarchinitiative.hpo_case_annotator.model.TargetGene;
import org.monarchinitiative.hpo_case_annotator.util.PopUps;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class EntrezParser {

    /**
     * Window title of Alert pop-ups.
     */
    private static final String windowTitle = "Loading Entrez gene file";

    /**
     * Flag that indicates successful parsing. Set to true only if all steps were successful.
     */
    private boolean parsingOk = false;

    private Map<Integer, TargetGene> entrezId2gene;

    private Map<String, String> entrezId2symbol, symbol2entrezId;


    /**
     * Parse Entrez file from given path.
     *
     * @param path Path to the Homo_sapiens.gene_info.gz file
     */
    public EntrezParser(String path) {
        entrezId2gene = new HashMap<>();
        entrezId2symbol = new HashMap<>();
        symbol2entrezId = new HashMap<>();
        readFile(path);
    }


    public Map<Integer, TargetGene> getEntrezMap() {
        return this.entrezId2gene;
    }


    public Map<String, String> getEntrezId2symbol() {
        return this.entrezId2symbol;
    }


    public Map<String, String> getSymbol2entrezId() {
        return this.symbol2entrezId;
    }


    private void readFile(String path) {
        String encoding = "UTF-8";

        if (path == null) {
            PopUps.showWarningDialog(windowTitle, "Entrez gene file is not present in data directory",
                    "Autocompletions of gene fields are not enabled.\nDownload file in Settings menu.");
            return;
        }

        File entrezFile = new File(path);
        if (!entrezFile.exists()) {
            PopUps.showWarningDialog(windowTitle, "Entrez gene file is not present in data directory",
                    "Autocompletion of Gene ID & Symbol is not enabled. Download file using Settings menu.");
            return;
        }

        try {
            InputStream fileStream = new FileInputStream(path);
            InputStream gzipStream = new GZIPInputStream(fileStream);
            Reader decoder = new InputStreamReader(gzipStream, encoding);
            BufferedReader br = new BufferedReader(decoder);

            String line = br.readLine(); // Very first line is header
            if (!line.startsWith("#")) {
                PopUps.showWarningDialog(windowTitle,
                        String.format("Warning, bad format of Entrez Gene file at %s.", path),
                        "Download a new file using Settings menu.");
                br.close();
                return;
            }
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                // Field 0: taxon id (must be 9606; there are a few entries for other homo sapiens subspecies)
                // Field 1: the Entrez ID
                // Field 2: the Gene symbol
                //tax_id GeneID Symbol LocusTag Synonyms dbXrefs chromosome map_location
                //Field 6: chromosome
                if (!line.startsWith("9606")) {
                    break; // We are done with Homo sapiens sapiens, just the Neanderthals etc come after, skip them
                }
                String fields[] = line.split("\t");
                String entrezidString = fields[1];
                String symbol = fields[2];
                String chromosome = fields[6];

                try {
                    Integer entrez = Integer.parseInt(entrezidString);
                    TargetGene eg = new TargetGene();
                    eg.setEntrezID(entrezidString);
                    eg.setGeneName(symbol);
                    eg.setChromosome(chromosome);

                    this.entrezId2symbol.put(entrezidString, symbol);
                    this.symbol2entrezId.put(symbol, entrezidString);
                    this.entrezId2gene.put(entrez, eg);
                } catch (NumberFormatException nfe) {
                    System.err.println(nfe.getMessage());
                }
            }
            br.close();
        } catch (IOException e) {
            PopUps.showException(windowTitle, "ERROR occured during parsing Entrez gene file", "", e);
        }
        parsingOk = true;
    }


    public boolean isParsingOk() {
        return parsingOk;
    }
}
