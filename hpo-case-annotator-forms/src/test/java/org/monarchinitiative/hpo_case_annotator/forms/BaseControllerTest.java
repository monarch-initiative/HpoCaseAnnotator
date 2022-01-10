package org.monarchinitiative.hpo_case_annotator.forms;

import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifier;
import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifierService;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.test.ControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.test.TestGeneIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.test.TestGenomicAssemblyRegistry;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.util.List;
import java.util.Map;

public class BaseControllerTest {

    public static final File LOCAL_ONTOLOGY_OBO = new File("/home/ielis/data/ontologies/hpo/2021-06-08/hp.json");

    public static final Ontology ONTOLOGY = OntologyLoader.loadOntology(LOCAL_ONTOLOGY_OBO);

    public static final Resources RESOURCES = new Resources();

    public static final GenomicAssemblyRegistry GENOMIC_ASSEMBLY_REGISTRY = new TestGenomicAssemblyRegistry();

    public static final GeneIdentifierService GENE_IDENTIFIER_SERVICE = makeGeneIdService();

    public static final ControllerFactory CONTROLLER_FACTORY = new ControllerFactory(RESOURCES, GENOMIC_ASSEMBLY_REGISTRY, GENE_IDENTIFIER_SERVICE);

    private static TestGeneIdentifierService makeGeneIdService() {
        Map<GeneIdentifier, List<String>> genes = Map.of(
                new GeneIdentifier(TermId.of("NCBIGene:1"), "HNF4A"), List.of("NM_000001.1", "NM_000002.1"),
                new GeneIdentifier(TermId.of("NCBIGene:2200"), "FBN1"), List.of("NM_000003.1", "NM_000004.1"),
                new GeneIdentifier(TermId.of("NCBIGene:3"), "GCK"), List.of("NM_000005.1", "NM_000006.1")
        );
        return new TestGeneIdentifierService(genes);
    }
}