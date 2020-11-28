package org.monarchinitiative.hpo_case_annotator.core.io;

/**
 * Parse the 2020 and onwards PubMed summary format. There are options, and this parser is for the APA citation
 * obtained if the user presses the CITE button and sets it to NLM
 * Format is like this
 * "Mátyás G, Alonso S, Patrignani A, Marti M, Arnold E, Magyar I, Henggeler C, Carrel T, Steinmann B, Berger W. Large genomic fibrillin-1 (FBN1) gene deletions provide evidence for true haploinsufficiency in Marfan syndrome. Hum Genet. 2007 Aug;122(1):23-32. doi: 10.1007/s00439-007-0371-x. Epub 2007 May 10. PMID: 17492313.";
 * i.e.,
 * Authors. Title. Journal. Year Month;volume:pages. Otherstuff
 *
 * This works just like the old parser, so we just pass it through. However, we will still demand that the user
 * enters the PMID by hand, because not all formats have a PMID.
 */
public class NlmPubMedParser extends PubMedParser {

    public NlmPubMedParser(String pubmedText, String pmid){
        super(pubmedText, pmid);
    }
}
