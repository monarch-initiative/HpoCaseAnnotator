package org.monarchinitiative.hpo_case_annotator.app.util;

import htsjdk.samtools.reference.FastaSequenceIndex;
import org.monarchinitiative.hpo_case_annotator.app.io.Downloads;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResource;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicRemoteResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public final class GenomicLocalResourceValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenomicLocalResourceValidator.class);
    private final Consumer<String> logger;

    public GenomicLocalResourceValidator(Consumer<String> logger) {
        this.logger = logger;
    }

    public Optional<? extends GenomicLocalResource> verify(GenomicLocalResource local, GenomicRemoteResource remote) {
        if (!downloadAssemblyReportIfMissing(local, remote)) {
            return Optional.empty();
        }
        return indexFastaIfIndexIsMissing(local)
                .flatMap(idx -> buildSequenceDictionaryIfMissing(idx, local));
    }

    private boolean downloadAssemblyReportIfMissing(GenomicLocalResource local, GenomicRemoteResource remote) {
        Path assemblyReport = local.getAssemblyReport();
        if (!Files.isRegularFile(assemblyReport)) {
            try {
                logger.accept(String.format("The assembly report is not present at '%s'. Attempting to download from '%s'", assemblyReport.toAbsolutePath(), remote.assemblyReportUrl()));
                Downloads.download(remote.assemblyReportUrl(), assemblyReport);
            } catch (IOException e) {
                String msg = String.format("Error while downloading assembly report: %s", e.getMessage());
                logger.accept(msg);
                LOGGER.warn(msg, e);
                return false;
            }
        } else {
            logger.accept(String.format("Found assembly report at '%s'", assemblyReport.toAbsolutePath()));
        }
        return true;
    }

    private Optional<FastaSequenceIndex> indexFastaIfIndexIsMissing(GenomicLocalResource local) {
        Path fastaFai = local.getFastaFai();
        if (!Files.isRegularFile(fastaFai)) {
            Path fasta = local.getFasta();
            try {
                String msg = String.format("The FASTA index is not present at '%s'. Attempting to index FASTA at '%s'", fastaFai.toAbsolutePath(), fasta);
                logger.accept(msg);
                FastaSequenceIndex index = Genome.indexFastaFile(fasta);
                index.write(fastaFai);
                logger.accept("Done");
                return Optional.of(index);
            } catch (IOException e) {
                String err = String.format("Unable to index FASTA file: %s", e.getMessage());
                logger.accept(err);
                LOGGER.warn(err, e);
                return Optional.empty();
            }
        } else {
            logger.accept(String.format("Found FASTA index at '%s'", fastaFai.toAbsolutePath()));
            return Optional.of(new FastaSequenceIndex(fastaFai));
        }
    }

    private Optional<? extends GenomicLocalResource> buildSequenceDictionaryIfMissing(FastaSequenceIndex idx, GenomicLocalResource local) {
        Path fastaDict = local.getFastaDict();
        if (!Files.isRegularFile(fastaDict)) {
            try {
                logger.accept("Building SAM sequence dictionary");
                Genome.buildSamSequenceDictionary(idx, fastaDict);
                logger.accept("Done");
            } catch (IOException e) {
                LOGGER.warn("Unable to build SAM sequence dictionary: {}", e.getMessage(), e);
                return Optional.empty();
            }
        } else {
            logger.accept("Found SAM sequence dictionary at '" + local.getFastaDict().toAbsolutePath() + "'");
        }
        return Optional.of(local);
    }

    public Consumer<String> logger() {
        return logger;
    }

    @Override
    public int hashCode() {
        return Objects.hash(logger);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GenomicLocalResourceValidator) obj;
        return Objects.equals(this.logger, that.logger);
    }

    @Override
    public String toString() {
        return "GenomicLocalResourceValidator[" +
                "logger=" + logger + ']';
    }

    public static GenomicLocalResourceValidator of(Consumer<String> logger) {
        return new GenomicLocalResourceValidator(logger);
    }

}
