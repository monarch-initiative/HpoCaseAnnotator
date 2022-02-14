package org.monarchinitiative.hpo_case_annotator.core.liftover;

import htsjdk.samtools.liftover.LiftOver;
import htsjdk.samtools.util.Interval;
import org.monarchinitiative.hpo_case_annotator.model.Hg18GenomicAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.svart.assembly.GenomicAssemblies;
import org.monarchinitiative.svart.assembly.GenomicAssembly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class LiftOverAdapter implements LiftOverService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiftOverAdapter.class);

    private static final GenomicAssembly HG18_ASSEMBLY = Hg18GenomicAssembly.hg18GenomicAssembly();
    private static final GenomicAssembly HG19_ASSEMBLY = GenomicAssemblies.GRCh37p13();
    private static final GenomicAssembly HG38_ASSEMBLY = GenomicAssemblies.GRCh38p13();

    private static final Pattern CHAIN_PATTERN = Pattern.compile("(?<from>\\w+)To(?<to>\\w+)\\.over.chain.gz");

    private final Map<String, Map<String, LiftOver>> chainMap;

    private LiftOverAdapter(Map<String, Map<String, LiftOver>> chainMap) {
        this.chainMap = chainMap;
    }

    @Deprecated
    public Map<GenomeAssembly, Set<GenomeAssembly>> supportedConversions() {
        Map<GenomeAssembly, Set<GenomeAssembly>> map = new HashMap<>();
//        for (GenomeAssembly from : chainMap.keySet()) {
//            map.put(from, new HashSet<>());
//            chainMap.get(from).forEach((to, lo) -> map.get(from).add(to));
//        }
        return map;
    }

    @Override
    public Set<GenomicAssembly> sourceGenomicAssemblies() {
        return chainMap.keySet().stream()
                .map(LiftOverAdapter::genomicAssemblyForName)
                .flatMap(Optional::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    private static Optional<? extends GenomicAssembly> genomicAssemblyForName(String genomicAssemblyName) {
        return switch (genomicAssemblyName) {
            case "NCBI36" -> Optional.of(HG18_ASSEMBLY);
            case "GRCh37.p13" -> Optional.of(HG19_ASSEMBLY);
            case "GRCh38.p13" -> Optional.of(HG38_ASSEMBLY);
            default -> Optional.empty();
        };
    }

    @Override
    public Set<String> supportedConversions(String source) {
        return chainMap.getOrDefault(source, Map.of())
                .keySet();
    }

    @Override
    public Optional<ContigPosition> liftOver(ContigPosition contigPosition, String from, String to) {
        if (supportsLiftover(from, to)) {
            Interval interval = toInterval(contigPosition);
            LiftOver liftOver = chainMap.get(from).get(to);

            return Optional.ofNullable(liftOver.liftOver(interval))
                    .map(LiftOverAdapter::fromInterval);
        }
        return Optional.empty();
    }

    public boolean supportsLiftover(String from, String to) {
        return chainMap.containsKey(from) && chainMap.get(from).containsKey(to);
    }

    private static Interval toInterval(ContigPosition contigPosition) {
        // Both of these should be 1-based coordinates
        return new Interval(contigPosition.contig(), contigPosition.position(), contigPosition.position());
    }

    private static ContigPosition fromInterval(Interval interval) {
        return new ContigPosition(interval.getContig(), interval.getStart());
    }

    @Deprecated
    public Optional<Interval> liftOver(Interval interval, GenomeAssembly from, GenomeAssembly to) {
//        if (supportsLiftover(from, to)) {
//            return Optional.ofNullable(chainMap.get(from).get(to).liftOver(interval));
//        } else {
//            LOGGER.debug("Unable to lift over position from `{}` to `{}`. Chain file not available ", from, to);
//            return Optional.empty();
//        }
        return Optional.empty();
    }

    public static LiftOverAdapter ofChainFolder(File chainFolder) {
        if (!chainFolder.isDirectory())
            throw new IllegalArgumentException("Not a directory " + chainFolder.getAbsolutePath());

        File[] chains = chainFolder.listFiles(f -> f.isFile() && f.getName().endsWith("chain.gz"));
        if (chains == null)
            throw new IllegalArgumentException("Error using chain directory " + chainFolder.getAbsolutePath());

        LOGGER.debug("Found {} chain file(s) in directory '{}'", chains.length, chainFolder.getAbsolutePath());
        return ofChains(chains);
    }

    public static LiftOverAdapter ofChains(File... chains) {
        return new LiftOverAdapter(parseChains(chains));
    }

    private static Map<String, Map<String, LiftOver>> parseChains(File... chains) {
        if (chains == null) {
            return Map.of();
        }

        Map<String, Map<String, LiftOver>> resultMap = new HashMap<>();
        for (File chainFile : chains) {
            LOGGER.debug("Parsing chain with name '{}'", chainFile.getName());
            Matcher matcher = CHAIN_PATTERN.matcher(chainFile.getName());
            if (matcher.matches()) {
                Optional<String> from = decodeAssembly(matcher.group("from"));
                Optional<String> to = decodeAssembly(matcher.group("to"));
                if (from.isPresent() && to.isPresent()) {
                    LOGGER.debug("Found chain to liftover coordinates from '{}' to '{}'", from, to);
                    resultMap.computeIfAbsent(from.get(), k -> new HashMap<>())
                            .put(to.get(), new LiftOver(chainFile));
                }
            } else {
                LOGGER.debug("Failed to find genome assembly codes in the chain `{}`", chainFile.getName());
            }
        }
        return Collections.unmodifiableMap(resultMap);
    }

    private static Optional<String> decodeAssembly(String assembly) {
        return switch (assembly.toUpperCase()) {
            case "HG18", "NCBI36" -> Optional.of(HG18_ASSEMBLY.name());
            case "HG19", "GRCH37" -> Optional.of(HG19_ASSEMBLY.name());
            case "HG38", "GRCH38" -> Optional.of(HG38_ASSEMBLY.name());
            default -> Optional.empty();
        };
    }

}
