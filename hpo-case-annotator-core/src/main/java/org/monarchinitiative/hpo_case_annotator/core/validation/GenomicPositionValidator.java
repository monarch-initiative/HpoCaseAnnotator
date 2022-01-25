package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.monarchinitiative.hpo_case_annotator.core.reference.genome.GenomeAssemblies;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.SequenceDao;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

import static org.monarchinitiative.hpo_case_annotator.core.validation.VariantSyntaxValidator.SNIPPET_REGEXP;

/**
 * Validator for checking correct entry of variant positions & snippets.
 */
public final class GenomicPositionValidator implements Validator<Variant> {



    private final GenomeAssemblies genomeAssemblies;


    GenomicPositionValidator(GenomeAssemblies genomeAssemblies) {
        this.genomeAssemblies = genomeAssemblies;
    }

    /**
     * @param contig      String with chromosome contig, such as <code>chr2</code>
     * @param pos         integer with 1-based position of the first ref nucleotide
     * @param snippet     String that has already been matched against {@link VariantSyntaxValidator#SNIPPET_REGEXP}
     * @param sequenceDao {@link SequenceDao} used to fetch nucleotide sequence to validate snippet against
     * @return results of validation - an empty list if the snippet is valid
     */
    private static List<ValidationResult> validateSnippet(String contig, int pos, String snippet, SequenceDao sequenceDao) {
        List<ValidationResult> results = new ArrayList<>();

        // define anchor points and extract snippet subparts
        int openBracketIdx = snippet.indexOf("[");
        int closeBracketIdx = snippet.indexOf("]");
        int slashIdx = snippet.indexOf("/");

        String prefix = snippet.substring(0, openBracketIdx);
        String suffix = snippet.substring(closeBracketIdx + 1);
        String snippetRef = snippet.substring(openBracketIdx + 1, slashIdx);

        // SequenceDao operates using 0-based coordinates
        // 0-based position of the first nucleotide of the snippet's prefix (exclusive)
        int seqBeginPos = pos - prefix.length();
        // 0-based position of the last nucleotide of the snippet's suffix (inclusive)
        int seqEndPos = pos + snippetRef.length() + suffix.length();

        // get the sequence that we will need
        String actualSnippetSeq = sequenceDao.fetchSequence(contig, seqBeginPos, seqEndPos);

        // Now compare the prefix
        String actualPrefix = actualSnippetSeq.substring(0, prefix.length());
        if (!prefix.equalsIgnoreCase(actualPrefix)) {
            results.add(ValidationResult.fail(String.format("Prefix in snippet '%s' does not match the actual sequence '%s' observed at interval '%s:%d-%d'",
                    prefix, actualPrefix, contig, seqBeginPos, seqBeginPos + prefix.length())));
        }

        // Now compare the ref allele
        String actualRefAllele = actualSnippetSeq.substring(prefix.length(), prefix.length() + snippetRef.length());
        if (!snippetRef.equalsIgnoreCase(actualRefAllele)) {
            int refBeginPos = seqBeginPos + prefix.length();
            results.add(ValidationResult.fail(String.format("Ref sequence in snippet '%s' does not match the actual sequence '%s' observed at interval '%s:%d-%d'",
                    snippetRef, actualRefAllele, contig, refBeginPos, refBeginPos + snippetRef.length())));
        }
        // Now compare the suffix
        String actualSuffix = actualSnippetSeq.substring(prefix.length() + snippetRef.length());
        if (!suffix.equalsIgnoreCase(actualSuffix)) {
            int suffixBeginPos = seqBeginPos + prefix.length() + snippetRef.length();
            results.add(ValidationResult.fail(String.format("Suffix in snippet '%s' does not match the actual sequence '%s' observed at interval '%s:%d-%d'",
                    suffix, actualSuffix, contig, suffixBeginPos, suffixBeginPos + suffix.length())));
        }

        return results;
    }


    /**
     * @param variant {@link Variant}
     * @return {@link List} with {@link ValidationResult}s
     */
    @Override
    public List<ValidationResult> validate(Variant variant) {
        List<ValidationResult> results = new ArrayList<>();

        if (variant.getVariantClass().equals("structural")) {
            // we do not validate genomic position of structural variants, since we do not enter
            // ref, alt, and snippet
            return results;
        }
        GenomeAssembly assembly = variant.getVariantPosition().getGenomeAssembly();

        if (!genomeAssemblies.hasFastaForAssembly(assembly)) { // no validation is possible if the fasta file is not present
            results.add(ValidationResult.fail("Fasta file for genome assembly " + assembly.name() + " is not present"));
            return results;
        }

        Optional<SequenceDao> sequenceDaoOptional = genomeAssemblies.getSequenceDaoForAssembly(assembly);
        if (!sequenceDaoOptional.isPresent()) { // unknown error
            results.add(ValidationResult.fail("Unable to open Fasta file "
                    + genomeAssemblies.getAssemblyMap().get(assembly) + " for genome assembly " + assembly.name()));
            return results;
        }


        final VariantPosition varPos = variant.getVariantPosition();
        String chrom = varPos.getContig();
        int pos = varPos.getPos() -1; // to 0-based
        String ref = varPos.getRefAllele();
        String alt = varPos.getAltAllele();
        int to_pos = pos + ref.length();

        SequenceDao sequenceDao = sequenceDaoOptional.get();
        if (ref.isEmpty()) {
            results.add(ValidationResult.fail("Ref sequence not initialized (length=0)"));
        } else {
            String expectedRefAllele = sequenceDao.fetchSequence(chrom, pos, to_pos);
            if (!ref.equalsIgnoreCase(expectedRefAllele)) {
                results.add(ValidationResult.fail(String.format("Ref sequence '%s' does not match the sequence '%s' observed at '%s:%d-%d'",
                        ref, expectedRefAllele, chrom, pos, to_pos)));
            }
        }
        if (alt.isEmpty()) {
            results.add(ValidationResult.fail("Alt sequence not initialized (length=0)"));
        }

        /* The variant string is OK, and now we can validate the snippet string */
        String snippet = variant.getSnippet();
        Matcher matcher = SNIPPET_REGEXP.matcher(snippet);
        if (matcher.matches()) {
            results.addAll(validateSnippet(chrom, pos, snippet, sequenceDao));
        } else {
            results.add(ValidationResult.fail("Snippet sequence " + snippet + " does not have the required format (see help)"));
        }

        return results;
    }


    /**
     * Some variants have snippets (we are trying to add this to all the variants). A snippet string is like this:
     * CTACT[G/A]TCCAA The sequence surrounding the position of the mutation is shown. If the snippet string is null or
     * empty, return true, this means that the biocuration is not yet finished (it is a sanity check for Q/C). The
     * sequence given in the brackets is the actual mutation.
     */
//    private ValidationResult validateSnippet(String chrom, String ref, String alt, int pos, String snippet) {
/*
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
            *//* This means that the position we gave in the GUI was VCF format, i.e., the
               position is the first base BEFORE the mutation, and needs to be adjusted because the mutation is an insertion! *//*
            isInDel = true;
        } else if (middle.endsWith("-")) {
            isInDel = true; *//* this means mutation was a deletion *//*
        }


        // Now check that the ALT and the REF match the nucleotides indicated in the snippet
        x1 = middle.indexOf("/");
        if (x1 < 0) {
            return makeValidationResult(ValidationResult.FAILED, String.format("Could not find proper slash (/) in snippet: %s", snippet));
        }
        String sRef = middle.substring(0, x1);
        String sAlt = middle.substring(x1 + 1);

        if (sRef.length() > 1 && sAlt.length() > 1) {
            *//* We are in a complicated ins-del mutation. *//*
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
        *//* Case of insertion *//*
        if (sRef.equals("-")) {
            if (sAlt.equals(alt.substring(1))) {
                return makeValidationResult(ValidationResult.PASSED, OKAY);
            } else {
                return makeValidationResult(ValidationResult.FAILED, String.format("Mismatching ALT string for insertion mutation: %s", snippet));
            }
            *//* Case of deletion *//*
        } else if (sAlt.equals("-")) {
            if (sRef.equals(ref.substring(1))) {
                return makeValidationResult(ValidationResult.PASSED, OKAY);// for instance TAAG -> T in VCF
            } else {
                //System.out.println("Did not match ref sequence in deletion");
                return makeValidationResult(ValidationResult.FAILED, String.format("Mismatching REF string for deletion mutation ref=\"%s\", alt=\"%s\" in snippet: %s",
                        ref, alt, snippet));
            }
            *//* substitution *//*
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
            } else { *//* all is OK *//*
                return makeValidationResult(ValidationResult.PASSED, OKAY);
            }
        }*/
//        String e = String.format("Uncaught situation: snippet: \"%s\", prefix=\"%s\",middle=\"%s\",suff=\"%s\"", snippet, pref, middle, suff);

//        setErrorMessage(e);
//        return false;
//        return null;
//    }


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
