package org.monarchinitiative.hpo_case_annotator.export;

import org.monarchinitiative.hpo_case_annotator.model.ModelUtils;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Gene;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class Utils {
    private Utils() {
    }

    static String getPhenopacketIdFor(DiseaseCase model) {
        /*
        Make sure the phenopacket ID can be used as a valid path (no chars like / & < > | etc.)
         */
        Publication publication = model.getPublication();
        String firstAuthorsSurname = ModelUtils.getFirstAuthorsSurname(publication.getAuthorList());

        String year = (publication.getYear().isEmpty()) ? "no_year" : publication.getYear();
        String pmid = (publication.getPmid().isEmpty()) ? "no_pmid" : publication.getPmid();

        Gene gene = model.getGene();
        String geneSymbol = (gene.getSymbol().isEmpty()) ? "no_gene" : gene.getSymbol();

        String familyProband = model.getFamilyInfo().getFamilyOrProbandId();
        String probandId = familyProband.isEmpty() ? "no_proband_id" : familyProband;

        List<String> tokens = new LinkedList<>();
        for (String item : Arrays.asList(firstAuthorsSurname, year, pmid, geneSymbol, probandId)) {
            String noDash = item.replaceAll("-", "_");
            String normalized = ModelUtils.normalizeAsciiText(noDash);
            String legal = ModelUtils.makeLegalFileNameWithNoWhitespace(normalized, '_');
            tokens.add(legal);
        }

        return String.join("-", tokens);
    }
}
