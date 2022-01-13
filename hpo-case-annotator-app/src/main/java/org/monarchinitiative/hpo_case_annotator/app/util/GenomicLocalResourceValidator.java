package org.monarchinitiative.hpo_case_annotator.app.util;

import htsjdk.samtools.reference.FastaSequenceIndex;
import org.monarchinitiative.hpo_case_annotator.app.io.Downloads;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResource;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicRemoteResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

public record GenomicLocalResourceValidator(Consumer<String> logger) {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenomicLocalResourceValidator.class);

    public static GenomicLocalResourceValidator of(Consumer<String> logger) {
        return new GenomicLocalResourceValidator(logger);
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
        if (!assemblyReport.toFile().isFile()) {
            try {
                logger.accept(String.format("The assembly report is not present at `%s`. Attempting to download from `%s`", assemblyReport.toAbsolutePath(), remote.assemblyReportUrl()));
                Downloads.download(remote.assemblyReportUrl(), assemblyReport);
            } catch (IOException e) {
                String msg = String.format("Error while downloading assembly report: %s", e.getMessage());
                logger.accept(msg);
                LOGGER.warn(msg, e);
                return false;
            }
        }
        return true;
    }

    private Optional<FastaSequenceIndex> indexFastaIfIndexIsMissing(GenomicLocalResource local) {
        Path fastaFai = local.getFastaFai();
        if (!fastaFai.toFile().isFile()) {
            Path fasta = local.getFasta();
            try {
                String msg = String.format("The FASTA index is not present at `%s`. Attempting to index FASTA at `%s`", fastaFai.toAbsolutePath(), fasta);
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
            return Optional.of(new FastaSequenceIndex(fastaFai));
        }
    }

    private Optional<? extends GenomicLocalResource> buildSequenceDictionaryIfMissing(FastaSequenceIndex idx, GenomicLocalResource local) {
        Path fastaDict = local.getFastaDict();
        if (!fastaDict.toFile().isFile()) {
            try {
                logger.accept("Building SAM sequence dictionary");
                Genome.buildSamSequenceDictionary(idx, fastaDict);
                logger.accept("Done");
            } catch (IOException e) {
                LOGGER.warn("Unable to build SAM sequence dictionary: {}", e.getMessage(), e);
                return Optional.empty();
            }
        }
        return Optional.of(local);
    }
}
