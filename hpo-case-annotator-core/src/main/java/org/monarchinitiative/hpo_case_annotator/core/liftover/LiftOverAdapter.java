package org.monarchinitiative.hpo_case_annotator.core.liftover;

import htsjdk.samtools.liftover.LiftOver;
import htsjdk.samtools.util.Interval;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LiftOverAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiftOverAdapter.class);

    private static final Pattern CHAIN_PATTERN = Pattern.compile("(?<from>\\w+)To(?<to>\\w+)\\.over.chain.gz");

    private final Map<GenomeAssembly, Map<GenomeAssembly, LiftOver>> chainMap;

    private LiftOverAdapter(Map<GenomeAssembly, Map<GenomeAssembly, LiftOver>> chainMap) {
        this.chainMap = chainMap;
    }

    public static LiftOverAdapter ofChainFolder(File chainFolder) {
        if (!chainFolder.isDirectory())
            throw new IllegalArgumentException("Not a directory " + chainFolder.getAbsolutePath());

        File[] chains = chainFolder.listFiles(f -> f.isFile() && f.getName().endsWith("chain.gz"));
        if (chains == null)
            throw new IllegalArgumentException("Error using chain directory " + chainFolder.getAbsolutePath());

        LOGGER.info("Found {} chain file(s) in directory '{}'", chains.length, chainFolder.getAbsolutePath());
        return ofChains(chains);
    }

    public static LiftOverAdapter ofChains(File... chains) {
        return new LiftOverAdapter(parseChains(chains));
    }

    private static Map<GenomeAssembly, Map<GenomeAssembly, LiftOver>> parseChains(File... chains) {
        Map<GenomeAssembly, Map<GenomeAssembly, LiftOver>> resultMap = new HashMap<>();
        if (chains == null) {
            return resultMap;
        }

        for (File chainFile : chains) {
            LOGGER.debug("Parsing chain with name '{}'", chainFile.getName());
            Matcher matcher = CHAIN_PATTERN.matcher(chainFile.getName());
            if (matcher.matches()) {
                GenomeAssembly from = decodeAssembly(matcher.group("from"));
                GenomeAssembly to = decodeAssembly(matcher.group("to"));
                LOGGER.debug("Found chain to liftover coordinates from '{}' to '{}'", from, to);
                resultMap.putIfAbsent(from, new HashMap<>());
                resultMap.get(from).put(to, new LiftOver(chainFile));
            } else {
                LOGGER.debug("Failed to find genome assembly codes in the chain `{}`", chainFile.getName());
            }
        }
        return resultMap;
    }

    private static GenomeAssembly decodeAssembly(String assembly) {
        switch (assembly.toUpperCase()) {
            case "HG18":
            case "NCBI36":
                return GenomeAssembly.HG_18;
            case "HG19":
            case "GRCH37":
                return GenomeAssembly.GRCH_37;
            case "HG38":
            case "GRCH38":
                return GenomeAssembly.GRCH_38;
            default:
                return GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY;
        }
    }

    public boolean supportsLiftover(GenomeAssembly from, GenomeAssembly to) {
        return chainMap.containsKey(from) && chainMap.get(from).containsKey(to);
    }

    public Map<GenomeAssembly, Set<GenomeAssembly>> supportedConversions() {
        Map<GenomeAssembly, Set<GenomeAssembly>> map = new HashMap<>();
        for (GenomeAssembly from : chainMap.keySet()) {
            map.put(from, new HashSet<>());
            chainMap.get(from).forEach((to, lo) -> map.get(from).add(to));
        }
        return map;
    }

    public Optional<Interval> liftOver(Interval interval, GenomeAssembly from, GenomeAssembly to) {
        if (supportsLiftover(from, to)) {
            return Optional.ofNullable(chainMap.get(from).get(to).liftOver(interval));
        } else {
            LOGGER.debug("Unable to lift over position from `{}` to `{}`. Chain file not available ", from, to);
            return Optional.empty();
        }
    }

}
