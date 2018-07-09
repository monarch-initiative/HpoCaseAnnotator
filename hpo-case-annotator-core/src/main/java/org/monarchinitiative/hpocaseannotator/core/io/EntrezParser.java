package org.monarchinitiative.hpocaseannotator.core.io;

import org.monarchinitiative.hpocaseannotator.core.model.proto.Gene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class EntrezParser {

    public static final String DEFAULT_ENTREZ_FILE_NAME = "Homo_sapiens.gene_info.gz";

    private static final Logger LOGGER = LoggerFactory.getLogger(EntrezParser.class);

    /**
     * Flag that indicates successful parsing. Set to true only if all steps were successful.
     */
    private boolean parsingOk = false;

    private Map<Integer, Gene> entrezId2gene;

    private Map<String, String> entrezId2symbol, symbol2entrezId;


    /**
     * Parse Entrez file from given path.
     *
     * @param path Path to the Homo_sapiens.gene_info.gz file
     */
    public EntrezParser(File path) throws IOException {
        entrezId2gene = new HashMap<>();
        entrezId2symbol = new HashMap<>();
        symbol2entrezId = new HashMap<>();
        readFile(path);
    }


    public Map<Integer, Gene> getEntrezMap() {
        return this.entrezId2gene;
    }


    public Map<String, String> getEntrezId2symbol() {
        return this.entrezId2symbol;
    }


    public Map<String, String> getSymbol2entrezId() {
        return this.symbol2entrezId;
    }


    private void readFile(File path) throws IOException {

        if (path == null || !path.isFile()) {
            throw new IOException("Not valid path to entrez gene file: " + path);
        }

        String encoding = "UTF-8";
        try (BufferedReader br =
                     new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path)), encoding))) {

            String line = br.readLine(); // Very first line is header
            if (!line.startsWith("#")) {
                throw new IOException(String.format("Warning, bad format of Entrez Gene file at %s.", path));
            }
            while ((line = br.readLine()) != null) {

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
                    Gene eg = Gene.newBuilder().setId(entrezidString).setSymbol(symbol).build();

                    this.entrezId2symbol.put(entrezidString, symbol);
                    this.symbol2entrezId.put(symbol, entrezidString);
                    this.entrezId2gene.put(entrez, eg);
                } catch (NumberFormatException nfe) {
                    LOGGER.warn(nfe.getMessage());
                }
            }
        }

        parsingOk = true;
    }


    public boolean isParsingOk() {
        return parsingOk;
    }
}
