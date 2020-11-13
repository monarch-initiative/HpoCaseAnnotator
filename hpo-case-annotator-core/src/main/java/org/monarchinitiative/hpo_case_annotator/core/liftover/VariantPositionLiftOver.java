package org.monarchinitiative.hpo_case_annotator.core.liftover;

import htsjdk.samtools.liftover.LiftOver;
import htsjdk.samtools.util.Interval;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariantPositionLiftOver {

    private static final Logger LOGGER = LoggerFactory.getLogger(VariantPositionLiftOver.class);

    private static final Pattern CHAIN_PATTERN = Pattern.compile("(\\w+)To(\\w+)\\.over.chain.gz");

    private final Map<LiftOverTuple, LiftOver> chainMap;


    public VariantPositionLiftOver(File chainDirectory) {
        chainMap = parseChainDirectory(chainDirectory);
    }

    private static Map<LiftOverTuple, LiftOver> parseChainDirectory(File chainDirectory) {
        Map<LiftOverTuple, LiftOver> resultMap = new HashMap<>();
        if (chainDirectory.isDirectory()) {
            final File[] chains = chainDirectory.listFiles(f -> f.isFile() && f.getName().endsWith("chain.gz"));
            if (chains == null) {
                LOGGER.warn("I/O error occured when trying to find chain files in '{}'", chainDirectory);
                return resultMap;
            }
            LOGGER.debug("Found {} chain file(s) in directory '{}'", chains.length, chainDirectory.getAbsolutePath());
            for (File chainFile : chains) {
                LOGGER.debug("Parsing chain with name '{}'", chainFile.getName());
                final Matcher matcher = CHAIN_PATTERN.matcher(chainFile.getName());
                if (matcher.matches() && matcher.groupCount() == 2) {
                    String fromAssembly = matcher.group(1);
                    String toAssembly = matcher.group(2);
                    GenomeAssembly from = figureOutAssembly(fromAssembly);
                    GenomeAssembly to = figureOutAssembly(toAssembly);
                    LiftOver lift = new LiftOver(chainFile);
                    resultMap.put(new LiftOverTuple(from, to), lift);
                    LOGGER.debug("Found chain lifting from '{}' to '{}'", from, to);
                } else {
                    LOGGER.debug("Failed to find genome assembly codes in the chain. Skipping");
                }
            }
        } else {
            LOGGER.warn("Path '{}' is not valid directory", chainDirectory);
        }
        return resultMap;
    }

    private static GenomeAssembly figureOutAssembly(String assemblyString) {
        switch (assemblyString.toUpperCase()) {
            case "HG19":
            case "GRCH37":
                return GenomeAssembly.GRCH_37;
            case "HG18":
            case "NCBI36":
                return GenomeAssembly.HG_18;
            case "HG38":
            case "GRCH38":
                return GenomeAssembly.GRCH_38;
            default:
                return GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY;
        }
    }


    public Optional<VariantPosition> liftOver(VariantPosition position, GenomeAssembly from, GenomeAssembly to) {
        if (!position.getGenomeAssembly().equals(from)) {
            LOGGER.warn("Rejecting to convert position on '{}' from '{}' to '{}'", position.getGenomeAssembly(), from, to);
            return Optional.empty();
        }

        final LiftOverTuple liftOverTuple = new LiftOverTuple(from, to);
        if (chainMap.containsKey(liftOverTuple)) {
            Interval interval = new Interval(position.getContig(), position.getPos(), position.getPos());
            final Interval lifted = chainMap.get(liftOverTuple).liftOver(interval);
            if (lifted == null) {
                LOGGER.info("Lift over is not possible");
                return Optional.empty();
            } else {
                return Optional.of(position.toBuilder()
                        .setGenomeAssembly(to)
                        .setPos(lifted.getStart())
                        .setContig(lifted.getContig())
                        .build());
            }
        } else {
            LOGGER.info("Unable to lift over position from {} to {}. Chain file not available ", from, to);
            return Optional.empty();
        }
    }

    public static class LiftOverTuple {

        private final GenomeAssembly from;

        private final GenomeAssembly to;

        private LiftOverTuple(GenomeAssembly from, GenomeAssembly to) {
            this.from = from;
            this.to = to;
        }

        public GenomeAssembly getFrom() {
            return from;
        }

        public GenomeAssembly getTo() {
            return to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LiftOverTuple that = (LiftOverTuple) o;
            return from == that.from &&
                    to == that.to;
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }

        @Override
        public String toString() {
            return "LiftOverTuple{" +
                    "from=" + from +
                    ", to=" + to +
                    '}';
        }
    }
}
