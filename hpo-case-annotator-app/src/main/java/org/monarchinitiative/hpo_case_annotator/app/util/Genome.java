package org.monarchinitiative.hpo_case_annotator.app.util;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceDictionaryCodec;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.FastaSequenceIndexCreator;
import htsjdk.samtools.reference.FastaSequenceIndexEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Genome {

    private static final Logger LOGGER = LoggerFactory.getLogger(Genome.class);

    private Genome() {
    }

    public static FastaSequenceIndex indexFastaFile(Path fastaPath, Path fastaFai) throws IOException {
        FastaSequenceIndex index = indexFastaFile(fastaPath);
        index.write(fastaFai);
        return index;
    }

    public static FastaSequenceIndex indexFastaFile(Path fastaPath) throws IOException {
        return FastaSequenceIndexCreator.buildFromFasta(fastaPath);
    }

    public static void buildSamSequenceDictionary(FastaSequenceIndex index, Path fastaDict) throws IOException {
        SAMSequenceDictionary sequenceDictionary = buildSamSequenceDictionary(index);
        try (BufferedWriter writer = Files.newBufferedWriter(fastaDict)) {
            SAMSequenceDictionaryCodec codec = new SAMSequenceDictionaryCodec(writer);
            codec.encode(sequenceDictionary);
        }
    }

    public static SAMSequenceDictionary buildSamSequenceDictionary(FastaSequenceIndex index) {
        List<SAMSequenceRecord> records = new ArrayList<>(index.size());
        for (FastaSequenceIndexEntry entry : index) {
            records.add(new SAMSequenceRecord(entry.getContig(), Math.toIntExact(entry.getSize())));
        }

        return new SAMSequenceDictionary(records);
    }


}
