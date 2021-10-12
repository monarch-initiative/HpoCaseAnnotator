package org.monarchinitiative.hpo_case_annotator.export.test_resources;

import org.phenopackets.schema.v1.core.OntologyClass;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class PhenoPacketTestUtil {

    public static final OntologyClass FEMALE = ontologyClass("PATO:0000383", "female");

    public static final OntologyClass MALE = ontologyClass("PATO:0000384", "male");

    public static final OntologyClass HOMO_SAPIENS = ontologyClass("NCBITaxon:9606", "Homo sapiens");

    public static final OntologyClass TAS = ontologyClass("ECO:0000033", "author statement supported by traceable reference");

    // source https://bioportal.bioontology.org/ontologies/GENO/?p=classes&conceptid=http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FGENO_0000135
    public static final OntologyClass HET = ontologyClass("GENO:0000135", "heterozygous");

    public static OntologyClass ontologyClass(String id, String label) {
        return OntologyClass.newBuilder()
                .setId(id)
                .setLabel(label)
                .build();
    }

}
