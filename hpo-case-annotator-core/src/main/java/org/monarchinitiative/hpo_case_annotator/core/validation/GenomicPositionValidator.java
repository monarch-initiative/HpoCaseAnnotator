package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.monarchinitiative.hpo_case_annotator.model.xml_model.SplicingVariant;
import org.monarchinitiative.hpo_case_annotator.model.proto.CrypticSpliceSiteType;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.SequenceDao;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.SingleFastaSequenceDao;

import java.io.File;

/**
 * Validator for checking correct entry of variant positions & snippets.
 */
public final class GenomicPositionValidator extends AbstractValidator {

    private final SequenceDao sequenceDao;


    public GenomicPositionValidator(SequenceDao sequenceDao) {
        this.sequenceDao = sequenceDao;
    }


    public GenomicPositionValidator(File fastaPath) {
        this.sequenceDao = new SingleFastaSequenceDao(fastaPath);
    }


    @Override
    public ValidationResult validateDiseaseCase(DiseaseCase model) {
        /* Expecting, that completeness validation was performed before so
         * there is at least one variant to validate and it contains all
         * required fields
         */

        return model.getVariantList().stream()
                .map(this::variantValid)
                .filter(vr -> vr != ValidationResult.PASSED)
                .findFirst() // find the first variant that failed validation
                .orElse(makeValidationResult(ValidationResult.PASSED, OKAY)); // or return PASSED if no variant had failed
    }


    /**
     * @return true if the variant and the snippet sequence matches the genome reference
     */
    ValidationResult variantValid(Variant variant) {
        int pos = variant.getPos();
        String chrom = variant.getContig();
        String ref = variant.getRefAllele();
        String alt = variant.getAltAllele();
        int len = ref.length();
        int to_pos = pos + len - 1;
        String expected = sequenceDao.fetchSequence(chrom, pos - 1, to_pos);

        boolean precondition = ref.equals(expected) && ref.length() > 0 && alt.length() > 0;

        if (!precondition) {
            if (ref.length() == 0) {
                return makeValidationResult(ValidationResult.FAILED, "REF sequence not initialized (length=0)");
            } else if (alt.length() == 0) {
                return makeValidationResult(ValidationResult.FAILED, "ALT sequence not initialized (length=0)");
            } else {
                String dx = String.format("genomic reference=\"%s\" at chr%s:%d-%d; entered ref=%s/alt=%s/len=%d",
                        expected, chrom, pos, to_pos, ref, alt, len);
                return makeValidationResult(ValidationResult.FAILED, dx);
            }
        }

        /* The variant string is OK, and now we can validate the snippet string */
        ValidationResult result = validateSnippet(chrom, ref, alt, pos, variant.getSnippet());
        if (result != ValidationResult.PASSED)
            return result;


        // Once again everything looks okay, now perform checks specific for variant subtypes
        switch (variant.getVariantValidation().getContext()) {
            case MENDELIAN:
                return makeValidationResult(ValidationResult.PASSED, OKAY);
            case SOMATIC:
                return makeValidationResult(ValidationResult.PASSED, OKAY);
            case SPLICING:
                return splicingAspectsOfVariantsAreValid(variant);
            default:
                return makeValidationResult(ValidationResult.UNAPPLICABLE, "Unknown variant validation context '" + variant.getVariantValidation().getContext() + "'");
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
     * @return {@link ValidationResult} with the validation result
     */
    private ValidationResult splicingAspectsOfVariantsAreValid(Variant variant) {
        // indicates position of nt in 5' direction before border (|) in VCF (1-based) numbering
        // ACGTACGTA|ACGTACGT -> position of 'A' (9th nucleotide, left of border).
        int pos = variant.getCrypticPosition();
        CrypticSpliceSiteType type = variant.getCrypticSpliceSiteType();
        String snippet = variant.getCrypticSpliceSiteSnippet();

        if (pos == 0 && (type.equals(CrypticSpliceSiteType.UNRECOGNIZED) || type.equals(CrypticSpliceSiteType.NO)) && isNullOrEmpty(snippet)) {
            // CSS data was not set and as it is not mandatory to set CSS data variant is valid.
            return makeValidationResult(ValidationResult.PASSED, OKAY);
        }

        if (countOccurence(snippet, '|') != 1) {
            return makeValidationResult(ValidationResult.FAILED, String.format("Unable to find '|' symbol in CSS snippet %s", snippet));
        }

        String chrom = variant.getContig();
        int border = snippet.indexOf('|');
        int prefixLen, suffixLen;
        // ACGT|ACGTAC - length -> 11, border -> 4, prefixLen -> 4, suffixLen -> 6
        prefixLen = border;
        suffixLen = snippet.length() - border - 1;
        String refGenomePrefixSeq, refGenomeSuffixSeq, enteredPrefixSeq, enteredSuffixSeq;


        // extract sequences from snippet
        enteredPrefixSeq = snippet.substring(0, border);
        enteredSuffixSeq = snippet.substring(border + 1, border + suffixLen + 1);

        // get sequences from reference genome
        refGenomePrefixSeq = sequenceDao.fetchSequence(chrom, pos - prefixLen, pos);
        refGenomeSuffixSeq = sequenceDao.fetchSequence(chrom, pos, pos + suffixLen);

        if (!enteredPrefixSeq.equalsIgnoreCase(refGenomePrefixSeq)) {
            return makeValidationResult(ValidationResult.FAILED, String.format("Prefix of CSS snippet: %s, Genomic sequence: %s",
                    enteredPrefixSeq, refGenomePrefixSeq));
        }

        if (!enteredSuffixSeq.equalsIgnoreCase(refGenomeSuffixSeq)) {
            return makeValidationResult(ValidationResult.FAILED, String.format("Suffix of CSS snippet: %ss, Genomic sequence: %s",
                    enteredSuffixSeq, refGenomeSuffixSeq));
        }
        return makeValidationResult(ValidationResult.PASSED, OKAY);
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


    private ValidationResult prefixMatches(String chrom, String snippet, String prefix, int pos) {
        //System.out.println("pref=" + pref + ", middle=" + middle + ", suff=" + suff + ", for snippet " + snippet);
        // Now compare the prefix
        int len = prefix.length();
        int zero_based_prefix_begin = pos - len - 1;
        int one_based_prefix_end = pos - 1;
        String expected = sequenceDao.fetchSequence(chrom, zero_based_prefix_begin, one_based_prefix_end);
        if (prefix.equals(expected)) {
            return makeValidationResult(ValidationResult.PASSED, OKAY);
        } else {
            return makeValidationResult(ValidationResult.FAILED, String.format("Prefix (%s) did not match with expected (%s) in snippet: %s (pos=%s:%d)",
                    prefix, expected, snippet, chrom, pos));
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
    private ValidationResult suffixMatches(String chrom, String snippet, String suffix, int pos) {
        // Now compare the suffix
        int zero_based_suffix_begin = pos - 1;
        int one_based_suffix_end = pos + suffix.length() - 1;
        String expected = sequenceDao.fetchSequence(chrom, zero_based_suffix_begin, one_based_suffix_end);
        if (suffix.equals(expected)) {
            return makeValidationResult(ValidationResult.PASSED, OKAY);
        } else {
            //System.out.println("Did not match suffix with expected=\"" + expected + "\"");
            //String msg = getSurroundingSequence(chrom,pos);
            return makeValidationResult(ValidationResult.FAILED, String.format("Suffix (%s) did not match with expected (%s: from:%d, to:%d) in snippet: %s. Expected: %s\n",
                    suffix, chrom, zero_based_suffix_begin, one_based_suffix_end, snippet, expected));
        }
    }


    /**
     * Some variants have snippets (we are trying to add this to all the variants). A snippet string is like this:
     * CTACT[G/A]TCCAA The sequence surrounding the position of the mutation is shown. If the snippet string is null or
     * empty, return true, this means that the biocuration is not yet finished (it is a sanity check for Q/C). The
     * sequence given in the brackets is the actual mutation. TODO--clean up the code (it works but is ugly!)
     */
    private ValidationResult validateSnippet(String chrom, String ref, String alt, int pos, String snippet) {

        if (snippet == null || snippet.length() == 0) {
            return makeValidationResult(ValidationResult.FAILED, "Snippet wasn't entered!");
        }
        int x1, x2;
        boolean isInDel = false;
        boolean combinedIndel = false;
        ValidationResult result;
        x1 = snippet.indexOf("["); // opening bracket
        if (x1 < 0) {
            return makeValidationResult(ValidationResult.FAILED, String.format("Could not find opening bracket in snippet: %s", snippet));
        }
        if (snippet.substring(0, x1).length() == 0) { // upstream sequence
            return makeValidationResult(ValidationResult.FAILED, String.format("Could not find upstream sequence in snippet: %s", snippet));
        }

        x2 = snippet.indexOf("]"); // closing bracket
        if (x2 < 0) {
            return makeValidationResult(ValidationResult.FAILED, String.format("Could not find closing bracket in snippet: %s", snippet));
        }

        if (snippet.substring(x2 + 1, snippet.length()).length() == 0) { // downstream sequence
            return makeValidationResult(ValidationResult.FAILED, String.format("Could not find downstream sequence in snippet: %s", snippet));
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
            return makeValidationResult(ValidationResult.FAILED, String.format("Could not find proper slash (/) in snippet: %s", snippet));
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
        if (isInDel)
            result = prefixMatches(chrom, snippet, pref, pos);
        else
            result = prefixMatches(chrom, snippet, pref, pos);

        if (result != ValidationResult.PASSED) return result; // Error msg has been set in method

        // Now compare the suffix
        result = suffixMatches(chrom, snippet, suff, pos + ref.length());
        if (result != ValidationResult.PASSED) return result;   // Error msg has been set in method


        if (sRef.length() == 0) {
            return makeValidationResult(ValidationResult.FAILED, String.format("Malformed (null) string for REF in snippet: %s", snippet));
        }
        if (sAlt.length() == 0) {
            return makeValidationResult(ValidationResult.FAILED, String.format("Malformed (null) string for ALT in snippet: %s", snippet));
        }
        /* Case of insertion */
        if (sRef.equals("-")) {
            if (sAlt.equals(alt.substring(1))) {
                return makeValidationResult(ValidationResult.PASSED, OKAY);
            } else {
                return makeValidationResult(ValidationResult.FAILED, String.format("Mismatching ALT string for insertion mutation: %s", snippet));
            }
            /* Case of deletion */
        } else if (sAlt.equals("-")) {
            if (sRef.equals(ref.substring(1))) {
                return makeValidationResult(ValidationResult.PASSED, OKAY);// for instance TAAG -> T in VCF
            } else {
                //System.out.println("Did not match ref sequence in deletion");
                return makeValidationResult(ValidationResult.FAILED, String.format("Mismatching REF string for deletion mutation ref=\"%s\", alt=\"%s\" in snippet: %s",
                        ref, alt, snippet));
            }
            /* substitution */
        } else if (combinedIndel) {
            if (sRef.equals(ref)) {
                ;//System.out.println("Ref matches in deletion case");
                return makeValidationResult(ValidationResult.PASSED, OKAY);
            } else {
                //System.out.println("Did not match ref sequence in deletion");
                return makeValidationResult(ValidationResult.FAILED, String.format("Combined indel: Mismatching REF string for deletion mutation ref=\"%s\", alt=\"%s\" in snippet: %s",
                        ref, alt, snippet));
            }
        } else {
            if (!sRef.equals(ref)) {
                return makeValidationResult(ValidationResult.FAILED, String.format("Reference sequence (%s) not given correctly in snippet (%s): %s", ref, sRef, snippet));
            } else if (!sAlt.equals(alt)) {
                System.out.println(String.format("ALT sequence (%s) not given correctly in snippet (%s): %s", alt, sAlt, snippet));
                return makeValidationResult(ValidationResult.FAILED, String.format("ALT sequence (%s) not given correctly in snippet: %s", alt, snippet));
            } else { /* all is OK */
                return makeValidationResult(ValidationResult.PASSED, OKAY);
            }
        }
//        String e = String.format("Uncaught situation: snippet: \"%s\", prefix=\"%s\",middle=\"%s\",suff=\"%s\"", snippet, pref, middle, suff);

//        setErrorMessage(e);
//        return false;
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
