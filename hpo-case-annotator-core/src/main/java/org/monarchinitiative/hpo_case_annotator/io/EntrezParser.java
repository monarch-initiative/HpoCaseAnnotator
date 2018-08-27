package org.monarchinitiative.hpo_case_annotator.io;

import org.monarchinitiative.hpo_case_annotator.model.TargetGene;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class EntrezParser {

    private final File entrezFile;

    private final Map<Integer, TargetGene> entrezId2gene;

    private final Map<String, String> entrezId2symbol, symbol2entrezId;


    /**
     * Initialize parser with given file. The file is not read during the initialization, method {@link #readFile()} must
     * be invoked to read the file.
     *
     * @param entrezFilePath Path to the Homo_sapiens.gene_info.gz file
     */
    public EntrezParser(File entrezFilePath) {
        entrezId2gene = new HashMap<>();
        entrezId2symbol = new HashMap<>();
        symbol2entrezId = new HashMap<>();
        this.entrezFile = entrezFilePath;
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


    /**
     * Read the file content and populate data maps which are empty before calling of this function.
     *
     * @throws IOException in case of error during file parsing (e.g. bad fileformat)
     */
    public void readFile() throws IOException {
        String encoding = "UTF-8";

        if (entrezFile == null)
            throw new IOException("Null instead of a valid file provided");
        else if (!entrezFile.exists()) {
            throw new IOException("Entrez file does not exist here: " + entrezFile.getPath());
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(entrezFile)), encoding))) {
            String line = br.readLine(); // Very first line is header
            if (!line.startsWith("#")) {
                throw new IOException(String.format("Warning, bad format of Entrez Gene file at %s.", entrezFile.getAbsolutePath()));
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
        }
    }
}
