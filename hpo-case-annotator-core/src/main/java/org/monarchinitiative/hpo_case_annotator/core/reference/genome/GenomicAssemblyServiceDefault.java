package org.monarchinitiative.hpo_case_annotator.core.reference.genome;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.variant.utils.SAMSequenceDictionaryExtractor;
import org.monarchinitiative.svart.Contig;
import org.monarchinitiative.svart.GenomicAssembly;
import org.monarchinitiative.svart.GenomicRegion;
import org.monarchinitiative.svart.SequenceRole;
import org.monarchinitiative.svart.parsers.GenomicAssemblyParser;
import org.monarchinitiative.svart.util.Seq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class GenomicAssemblyServiceDefault implements GenomicAssemblyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenomicAssemblyServiceDefault.class);

    private final GenomicAssembly genomicAssembly;
    private final IndexedFastaSequenceFile fasta;
    /**
     * True if all chromosomes in FASTA are prefixed with `chr` and false if all chromosomes are not prefixed.
     */
    private final boolean usesPrefix;

    static GenomicAssemblyServiceDefault of(Path assemblyReportPath, Path fastaPath, Path fastaFai, Path fastaDict) throws InvalidFastaFileException {
        GenomicAssembly assembly = GenomicAssemblyParser.parseAssembly(assemblyReportPath);
        IndexedFastaSequenceFile fasta = new IndexedFastaSequenceFile(fastaPath, new FastaSequenceIndex(fastaFai));
        SAMSequenceDictionary sequenceDictionary = buildSequenceDictionary(fastaDict);
        boolean usesPrefix = figureOutPrefix(sequenceDictionary);
        check(assembly, sequenceDictionary);
        return new GenomicAssemblyServiceDefault(assembly, fasta, usesPrefix);
    }


    private static SAMSequenceDictionary buildSequenceDictionary(Path dictPath) {
        return SAMSequenceDictionaryExtractor.extractDictionary(dictPath);
    }

    private static boolean figureOutPrefix(SAMSequenceDictionary sequenceDictionary) throws InvalidFastaFileException {
        Predicate<SAMSequenceRecord> prefixed = e -> e.getSequenceName().startsWith("chr");
        boolean allPrefixed = sequenceDictionary.getSequences().stream().allMatch(prefixed);
        boolean nonePrefixed = sequenceDictionary.getSequences().stream().noneMatch(prefixed);

        if (allPrefixed) return true;
        else if (nonePrefixed) return false;
        else {
            String msg = String.format("Found prefixed and unprefixed contigs among fasta dictionary entries - %s",
                    sequenceDictionary.getSequences().stream()
                            .map(SAMSequenceRecord::getSequenceName).collect(Collectors.joining(",", "{", "}")));
            if (LOGGER.isErrorEnabled())
                LOGGER.error(msg);
            throw new InvalidFastaFileException(msg);
        }
    }

    private static void check(GenomicAssembly assembly, SAMSequenceDictionary sequenceDictionary) throws InvalidFastaFileException {
        // we require assembly contigs with `SequenceRole.ASSEMBLED_MOLECULE` to be present in the FASTA file
        Set<String> assemblyContigNames = assembly.contigs().stream()
                .filter(c -> c.sequenceRole().equals(SequenceRole.ASSEMBLED_MOLECULE))
                .map(c -> List.of(c.name(), c.refSeqAccession(), c.genBankAccession(), c.ucscName()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        Set<String> dictContigNames = sequenceDictionary.getSequences().stream()
                .map(SAMSequenceRecord::getSequenceName)
                .filter(name -> !(name.startsWith("chrUn") || name.contains("random") || name.contains("hap") || name.endsWith("alt")))
                .collect(Collectors.toSet());
        if (!assemblyContigNames.containsAll(dictContigNames)) {
            throw new InvalidFastaFileException("Required contigs are missing in FASTA file");
        }

        // check that contig lengths match
        Map<String, Integer> assemblyContigLengths = assemblyContigNames.stream()
                .collect(Collectors.toMap(Function.identity(), contigName -> assembly.contigByName(contigName).length()));

        Map<String, Integer> dictionaryContigLengths = dictContigNames.stream()
                .collect(Collectors.toMap(Function.identity(), contigName -> sequenceDictionary.getSequence(contigName).getSequenceLength()));

        boolean lengthMismatch = false;
        for (String dictContig : dictionaryContigLengths.keySet()) {
            int dictContigLength = dictionaryContigLengths.get(dictContig);
            int assemblyContigLength = assemblyContigLengths.get(dictContig);
            if (dictContigLength != assemblyContigLength) {
                LOGGER.warn("Contig length mismatch {}!={} between `{}` (genome assembly report)  and `{}` (FASTA sequence dictionary)", assemblyContigLength, dictContigLength, dictContig, dictContig);
                lengthMismatch = true;
            }
        }
        if (lengthMismatch) throw new InvalidFastaFileException("Contig length mismatch");
    }

    private GenomicAssemblyServiceDefault(GenomicAssembly genomicAssembly, IndexedFastaSequenceFile fasta, boolean usesPrefix) {
        this.genomicAssembly = Objects.requireNonNull(genomicAssembly, "Genomic assembly must not be null");
        this.fasta = Objects.requireNonNull(fasta, "Indexed FASTA sequence file must not be null");
        this.usesPrefix = usesPrefix;
    }


    @Override
    public GenomicAssembly genomicAssembly() {
        return genomicAssembly;
    }

    @Override
    public Optional<StrandedSequence> referenceSequence(GenomicRegion query) {
        Contig contig = genomicAssembly.contigByName(query.contigName());
        if (contig.equals(Contig.unknown())) {
            LOGGER.warn("Unknown chromosome `{}`", query.contigName());
            return Optional.empty();
        }

        // the name we use for contig in FASTA file
        GenomicRegion onPositive = query.toPositiveStrand().toOneBased();
        String contigName = usesPrefix ? contig.ucscName() : contig.name();
        String seq;
        try {
            synchronized (this) {
                ReferenceSequence referenceSequence = fasta.getSubsequenceAt(contigName, onPositive.start(), onPositive.end());
                seq = new String(referenceSequence.getBases());
            }
        } catch (SAMException e) {
            LOGGER.warn("Error getting sequence for query `{}:{}-{}`: {}", onPositive.contigName(), onPositive.start(), onPositive.end(), e.getMessage());
            return Optional.empty();
        }
        return Optional.of(StrandedSequence.of(query, query.strand().isPositive() ? seq : Seq.reverseComplement(seq)));
    }

    @Override
    public void close() throws Exception {
        fasta.close();
    }
}
