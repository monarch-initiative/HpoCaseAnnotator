package org.monarchinitiative.hpo_case_annotator.validation;

import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.gui.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.model.SplicingVariant;
import org.monarchinitiative.hpo_case_annotator.model.Variant;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

/**
 * Validator for checking correct entry of variant positions & snippets.
 */
public final class GenomicPositionValidator extends AbstractValidator {

    //
    private final ObjectProperty<File> refGenomeDir = new SimpleObjectProperty<>(this, "refGenomeDir");

    /** Object representing the fasta index file */
    private FastaSequenceIndex fai = null;

    /** Object representing the fasta file */
    private IndexedFastaSequenceFile fa = null;

    /**
     * Use this to keep track of the current chromosome as we are iterating over variants.
     */
    private String currentChromosome = "";

    @Inject
    public GenomicPositionValidator(OptionalResources optionalResources) {
        refGenomeDir.bind(optionalResources.refGenomeDirProperty());
    }

    public GenomicPositionValidator(File refGenomeDir) {
        this.refGenomeDir.set(refGenomeDir);
    }


    @Override
    public ValidationResult validateDiseaseCase(DiseaseCaseModel model) {
        /* Expecting, that completeness validation was performed before so
         * there is at least one variant to validate and it contains all
         * required fields
         */
        List<Variant> variantList = model.getVariants();

        if (!variantList.stream().allMatch(this::variantValid)) {
            return ValidationResult.FAILED;
        }
        setErrorMessage(OKAY);
        return ValidationResult.PASSED;
    }


    /**
     * @return true if the variant and the snippet sequence matches the genome reference
     */
    boolean variantValid(Variant variant) {
        int pos = Integer.parseInt(variant.getPosition());
        String chrom = getChromosomeString(variant);
        String ref = variant.getReferenceAllele();
        String alt = variant.getAlternateAllele();
        int len = ref.length();
        int to_pos = pos + len - 1;
        String expected = getGenomicReferenceSequence(chrom, pos, to_pos);

        boolean precondition = ref.equals(expected) && ref.length() > 0 && alt.length() > 0;

        if (!precondition) {
            if (ref.length() == 0) {
                setErrorMessage("REF sequence not initialized (length=0)");
            } else if (alt.length() == 0) {
                setErrorMessage("ALT sequence not initialized (length=0)");
            } else {
                String dx = String.format("genomic reference=\"%s\" at %s:%d-%d; entered ref=%s/alt=%s/len=%d",
                        expected, chrom, pos, to_pos, ref, alt, len);
                setErrorMessage(dx);
            }
            return false;
        }

        /* The variant string is OK, and now we can validate the snippet string */
        if (!validateSnippet(chrom, ref, alt, pos, variant.getSnippet())) {
            return false;
        }

        // Once again everything looks okay, now perform checks specific for variant subtypes
        switch (variant.getVariantMode()) {
            case MENDELIAN:
                return true;
            case SOMATIC:
                return true;
            case SPLICING:
                return variantValid((SplicingVariant) variant);
            default:
                return false;
        }
    }


    /**
     * This method should validate all aspects which are different between SplicingVariant and Variant classes. At the
     * moment it validates CSS position, snippet format & sequence. These attributes must obey these criteria: <ul>
     * <li>CSS position <em>always</em> indicates the genomic position of nucleotide which is immediate 5' neighbor of
     * newly created splice site. In case of the following splice site: <em>TTGAGCCAG|gtgtag</em> the position of 9th
     * nucleotide (G) is indicated. Please note that  VCF (1-based) numbering is used.</li> <li>The snippet sequence is
     * <b>always</b> the sequence of <b>FWD strand</b> of the reference genome regardless of the gene strand.</li>
     * <li>Character case is ignored</li> </ul>
     *
     * @param variant {@link SplicingVariant} to be validated
     * @return true if the variant is valid.
     */
    private boolean variantValid(SplicingVariant variant) {
        // indicates position of nt in 5' direction before border (|) in VCF (1-based) numbering
        // ACGTACGTA|ACGTACGT -> position of 'A' (9th nucleotide, left of border).
        String position = variant.getCrypticPosition();
        String type = variant.getCrypticSpliceSiteType();
        String snippet = variant.getCrypticSpliceSiteSnippet();

        if (isNullOrEmpty(position) && isNullOrEmpty(type) && isNullOrEmpty(snippet)) {
            // CSS data was not set and as it is not mandatory to set CSS data variant is valid.
            setErrorMessage(OKAY);
            return true;
        }

        if (countOccurence(snippet, '|') != 1) {
            setErrorMessage(String.format("Unable to find '|' symbol in CSS snippet %s", snippet));
            return false;
        }

        String chrom = getChromosomeString(variant);
        int border = snippet.indexOf('|');
        int prefixLen, suffixLen;
        // ACGT|ACGTAC - length -> 11, border -> 4, prefixLen -> 4, suffixLen -> 6
        prefixLen = border;
        suffixLen = snippet.length() - border - 1;
        String refGenomePrefixSeq, refGenomeSuffixSeq, enteredPrefixSeq, enteredSuffixSeq;

        int pos;
        try {
            pos = Integer.parseInt(position);
        } catch (NumberFormatException nfe) {
            setErrorMessage(String.format("CSS position '%s' is not valid integer", position));
            return false;
        }

        // extract sequences from snippet
        enteredPrefixSeq = snippet.substring(0, border);
        enteredSuffixSeq = snippet.substring(border + 1, border + suffixLen + 1);

        // get sequences from reference genome
        refGenomePrefixSeq = getGenomicReferenceSequence(chrom, pos - prefixLen + 1, pos);
        refGenomeSuffixSeq = getGenomicReferenceSequence(chrom, pos + 1, pos + suffixLen);

        if (!enteredPrefixSeq.equalsIgnoreCase(refGenomePrefixSeq)) {
            setErrorMessage(String.format("Prefix of CSS snippet: %s, Genomic sequence: %s",
                    enteredPrefixSeq, refGenomePrefixSeq));
            return false;
        }

        if (!enteredSuffixSeq.equalsIgnoreCase(refGenomeSuffixSeq)) {
            setErrorMessage(String.format("Suffix of CSS snippet: %ss, Genomic sequence: %s",
                    enteredSuffixSeq, refGenomeSuffixSeq));
            return false;
        }
        return true;
    }


    /**
     * Count how many times is character <em>counted</em> present in given <em>string</em>.
     *
     * @param string  that is being searched
     * @param counted character that is being counted.
     * @return number of occurences of <em>counted</em> in <em>string</em>
     */
    private int countOccurence(String string, char counted) {
        int counter = 0;
        char[] chars = string.toCharArray();
        for (char t : chars) {
            if (t == counted) {
                counter++;
            }
        }
        return counter;
    }


    /**
     * Note that this method has the side effect of setting the corresponding chromosome for the htsjdk library by means
     * of the function {@link #openChromosomeFaAndFai}.
     *
     * @return a string such as chr3 or chrX
     */
    private String getChromosomeString(Variant v) {
        String c = v.getChromosome();
        /* Note that the HRMDgui gives us "4" instead of "chr4". We add that last bit back here.
           In case this ever changes elsewhere in the code, we also just check here.
           .*/
        String chrom = null;
        if (c.startsWith("chr"))
            chrom = c;
        else
            chrom = String.format("chr%s", c);
        if (!this.currentChromosome.equals(chrom)) {
            openChromosomeFaAndFai(chrom);
            this.currentChromosome = chrom;
        }
        return chrom;
    }


    /**
     * This function is used to construct the fasta and the fai objects that the HTSJDK library will use to extract the
     * sequence. It initializes the class variables {@link #fai} and {@link #fa}.
     * <p>
     */
    private void openChromosomeFaAndFai(String chrom) {
        File faPath = new File(refGenomeDir.get(), chrom + ".fa");
        File faiPath = new File(refGenomeDir.get(), chrom + ".fa.fai");
        this.fai = new FastaSequenceIndex(faiPath);
        this.fa = new IndexedFastaSequenceFile(faPath, fai);
    }


    private boolean prefixMatches(String chrom, String snippet, String prefix, int pos) {
        //System.out.println("pref=" + pref + ", middle=" + middle + ", suff=" + suff + ", for snippet " + snippet);
        // Now compare the prefix
        int len = prefix.length();
        int pos2 = pos - len;
        int to_pos = pos - 1;
        String expected = getGenomicReferenceSequence(chrom, pos2, to_pos);
        if (prefix.equals(expected)) {
            return true;
        } else {
            //System.out.println("Did not match pref with expected=\"" + expected + "\"");
            setErrorMessage(String.format("Prefix (%s) did not match with expected (%s) in snippet: %s (pos=%s:%d)",
                    prefix, expected, snippet, chrom, pos));
            return false;
        }
    }


    /**
     * Compare the suffix part of the snippet with the expected genome sequence.
     *
     * @param chrom   the chromosome on which the mutation is located
     * @param snippet A string such as ACTG[G/T]TTGA showing the mutation (G>T) and the surrounding sequence
     * @param suffix  The suffix of the snippet
     * @param pos     The position
     * @return true if the suffix is correct.
     */
    private boolean suffixMatches(String chrom, String snippet, String suffix, int pos) {
        // Now compare the suffix
        //pos2=pos + ref.length();
        int to_pos = pos + suffix.length() - 1;
        String expected = getGenomicReferenceSequence(chrom, pos, to_pos);
        if (suffix.equals(expected)) {
            return true;
        } else {
            //System.out.println("Did not match suffix with expected=\"" + expected + "\"");
            //String msg = getSurroundingSequence(chrom,pos);
            setErrorMessage(String.format("Suffix (%s) did not match with expected (%s: from:%d, to:%d) in snippet: %s. Expected: %s\n",
                    suffix, chrom, pos, to_pos, snippet, expected));
            return false;
        }
    }


    /**
     * Some variants have snippets (we are trying to add this to all the variants). A snippet string is like this:
     * CTACT[G/A]TCCAA The sequence surrounding the position of the mutation is shown. If the snippet string is null or
     * empty, return true, this means that the biocuration is not yet finished (it is a sanity check for Q/C). The
     * sequence given in the brackets is the actual mutation. TODO--clean up the code (it works but is ugly!)
     */
    private boolean validateSnippet(String chrom, String ref, String alt, int pos, String snippet) {

        if (snippet == null || snippet.length() == 0) {
            setErrorMessage("Snippet wasn't entered!");
            return false;
        }
        int x1, x2;
        boolean isInDel = false;
        boolean combinedIndel = false;
        boolean OK;
        x1 = snippet.indexOf("["); // opening bracket
        if (x1 < 0) {
            setErrorMessage(String.format("Could not find opening bracket in snippet: %s", snippet));
            return false; // malformed
        }
        if (snippet.substring(0, x1).length() == 0) { // upstream sequence
            setErrorMessage(String.format("Could not find upstream sequence in snippet: %s", snippet));
            return false; // malformed
        }

        x2 = snippet.indexOf("]"); // closing bracket
        if (x2 < 0) {
            setErrorMessage(String.format("Could not find closing bracket in snippet: %s",
                    snippet));
            return false; // malformed.
        }

        if (snippet.substring(x2 + 1, snippet.length()).length() == 0) { // downstream sequence
            setErrorMessage(String.format("Could not find downstream sequence in snippet: %s", snippet));
            return false; // malformed
        }

        String pref = snippet.substring(0, x1);
        String suff = snippet.substring(x2 + 1);
        String middle = snippet.substring(x1 + 1, x2);
        if (middle.startsWith("-")) {
            /* This means that the position we gave in the GUI was VCF format, i.e., the
               position is the first base BEFORE the mutation, and needs to be adjusted because the mutation is an insertion! */
            isInDel = true;
        } else if (middle.endsWith("-")) {
            isInDel = true; /* this means mutation was a deletion */
        }


        // Now check that the ALT and the REF match the nucleotides indicated in the snippet
        x1 = middle.indexOf("/");
        if (x1 < 0) {
            setErrorMessage(String.format("Could not find proper slash (/) in snippet: %s", snippet));
            return false; // malformed.
        }
        String sRef = middle.substring(0, x1);
        String sAlt = middle.substring(x1 + 1);

        if (sRef.length() > 1 && sAlt.length() > 1) {
            /* We are in a complicated ins-del mutation. */
            isInDel = true;
            combinedIndel = true;
        }
        //System.out.println("pref=" + pref + ", middle=" + middle + ", suff=" + suff + ", for snippet " + snippet);
        // Now compare the prefix
        if (isInDel) {
//            OK = prefixMatches(chrom, snippet, pref, pos + 1);
            OK = prefixMatches(chrom, snippet, pref, pos);
        } else {
            OK = prefixMatches(chrom, snippet, pref, pos);
        }
        if (!OK) {
//          Error msg has been set in method
            return false;
        }

        // Now compare the suffix
        OK = suffixMatches(chrom, snippet, suff, pos + ref.length());
        if (!OK) {
//          Error msg has been set in method
            return false;
        }


        if (sRef.length() == 0) {
            setErrorMessage(String.format("Malformed (null) string for REF in snippet: %s", snippet));
            return false; // malformed.
        }
        if (sAlt.length() == 0) {
            setErrorMessage(String.format("Malformed (null) string for ALT in snippet: %s", snippet));
            return false; // malformed.
        }
        /* Case of insertion */
        if (sRef.equals("-")) {
            if (sAlt.equals(alt.substring(1))) {
                setErrorMessage(OKAY);
                return true;
            } else {
                setErrorMessage(String.format("Mismatching ALT string for insertion mutation: %s", snippet));
                return false;
            }
            /* Case of deletion */
        } else if (sAlt.equals("-")) {
            if (sRef.equals(ref.substring(1))) {
                return true;// for instance TAAG -> T in VCF
            } else {
                //System.out.println("Did not match ref sequence in deletion");
                setErrorMessage(String.format("Mismatching REF string for deletion mutation ref=\"%s\", alt=\"%s\" in snippet: %s",
                        ref, alt, snippet));
                return false;
            }
            /* substitution */
        } else if (combinedIndel) {
            if (sRef.equals(ref)) {
                ;//System.out.println("Ref matches in deletion case");
                return true;
            } else {
                //System.out.println("Did not match ref sequence in deletion");
                setErrorMessage(String.format("Combined indel: Mismatching REF string for deletion mutation ref=\"%s\", alt=\"%s\" in snippet: %s",
                        ref, alt, snippet));
                return false;
            }
        } else {
            if (!sRef.equals(ref)) {
                setErrorMessage(String.format("Reference sequence (%s) not given correctly in snippet (%s): %s",
                        ref, sRef, snippet));
                return false;
            } else if (!sAlt.equals(alt)) {
                System.out.println(String.format("ALT sequence (%s) not given correctly in snippet (%s): %s", alt, sAlt, snippet));
                setErrorMessage(String.format("ALT sequence (%s) not given correctly in snippet: %s",
                        alt, snippet));
                return false;
            } else { /* all is OK */
                setErrorMessage(OKAY);
                return true;
            }
        }
//        String e = String.format("Uncaught situation: snippet: \"%s\", prefix=\"%s\",middle=\"%s\",suff=\"%s\"", snippet, pref, middle, suff);

//        setErrorMessage(e);
//        return false;
    }


    /**
     * Get genomic reference sequence present in given region.
     *
     * @param chrString String with chromosome ID
     * @param from_pos  1-based inclusive start coordinate of the region
     * @param to_pos    1-based inclusive end coordinate of the region
     * @return String with reference sequence in upper case
     */
    private String getGenomicReferenceSequence(String chrString, long from_pos, long to_pos) {
        ReferenceSequence rf = fa.getSubsequenceAt(chrString, from_pos, to_pos);
        //System.out.println("RF=" + rf);
        byte[] t = rf.getBases();
        String reference = new String(t);
        return reference.toUpperCase();
    }
}


//    *************************** LEGACY CODE ****************************** //
/*
  Use to sort the mutation objects according to chromosome and position. Note that we use string sorting for the
  chromosome because the actual order doesnt matter, but we want to do all of the mutations for a given chromosome at
  the same time. we sort mutations withn a certain chromosome according to position because this might affect
  performance.
 */
//    public static Comparator<MendelianDiseaseCaseModel> MutationByChromComparator 
//        = new Comparator<MendelianDiseaseCaseModel>() {
//            public int compare(MendelianDiseaseCaseModel model1, MendelianDiseaseCaseModel model2) {
//                Variant v1 = model1.getVariants().getVariantOne();
//                Variant v2 = model2.getVariants().getVariantTwo();
//                String chr1 = v1.getChromosome();
//                String chr2 = v2.getChromosome();
//                if (! chr1.equals(chr2))
//                return chr1.compareTo(chr2);
//                
//                int pos1, pos2;
//                try {
//                    pos1 = Integer.parseInt(v1.getPosition());
//                    pos2 = Integer.parseInt(v2.getPosition());
//                } catch (NumberFormatException nfe) {
//                    // TODO - perform input checking to be sure that every input is in valid format!
//                    PopUps.showErrorMessage(String.format("Variant's position is not valid integer!\nPosition 1: %s\nPosition 2: %s",
//                            v1.getPosition(), v2.getPosition())); return -1;
//                }
//                if (pos1<pos2)
//                    return -1;
//                else if (pos1==pos2)
//                    return 0;
//                else
//                    return 1;
//                return 0;
//            }
//    };


//        private String getSurroundingSequence(String chrom, int pos) {
//        int x = pos - (pos%10) - 10;
//        x = x>0?x:1;
//        String seq = getGenomicReferenceSequence(chrom,x,x+40);
//        String seqA = String.format("%s %s %s %s\n",seq.substring(0,10),seq.substring(10,20),seq.substring(20,30),seq.substring(30,40));
//        return String.format("%s\n\t(VCF-position of mutation %s:%d) Sequence shown here begins at %d",seqA,chrom,pos,x);
//    }


//    /**
//     * Check whether the genomic coordinates match the sequences of the REF and ALT. If the
//     * mutation has a snippet, also check this.
//     */
//    public void validateMutations(List<MendelianDiseaseCaseModel> mutation_list) {
//        if (this.directory==null) {
//            String err = "Error: The directory with the Genome FASTA and fasta index (FAI) files is not correctly initialized. Check the path";
//    //        GuiUtil. showTextResults("Genomic Position Q/C check", err);
//            return;
//        }
//        // Sort according to chromosome to avoid repeated reopening some file
//        try {
//            Collections.sort(mutation_list,GenomicPositionValidator.MutationByChromComparator);
//        } catch (IllegalArgumentException e) {
//            String err = String.format("Error in [GenomicPositionValidator.java]: Problems sorting the mutations.\n\n%s\n",e.getMessage());
//    //        GuiUtil. showTextResults("Error", err);
//            return;
//    }
//
//        StringBuilder sb = new StringBuilder();
//        int good=0; // Number of mutations whose reference sequence "matches"
//        int bad=0;
//        String currentChrom="uninitiated";
//        for (MendelianDiseaseCaseModel mut : mutation_list) {
//            //System.out.println(mut);
//            Variant v1 = mut.getVariants().getVariantOne();
//            if (variantValid(v1)) {
//            good++;
//            } else {
//            bad++;
//            String err = String.format(bad + ": [%s] %s\n", mut.getFirstAuthorAndYear(),this.currentVariantErrorString); 
//            sb.append(err);
//        
//            }
//            if (mut.isVariant2initialized()) {
//            Variant v2 = mut.getVariant(1);
//            if (variantValid(v2)) {
//                good++;
//            } else {
//                bad++;
//                String err = String.format(bad + ": [%s] %s\n", mut.getFirstAuthorAndYear(),this.currentVariantErrorString); 
//                sb.append(err);
//               
//            }
//        }
//    }
//    sb.append(good + " mutations had matching reference sequences.\n");
//    if (bad==0)
//        sb.append("GOOD WORK! All chromosomal positions look OK");
//    else
//        sb.append(bad + " mutations need to be revised! Go get to work!\n");
////    GuiUtil.showTextResults("Genomic Position Q/C check", sb.toString());
//    }
