package org.monarchinitiative.hpo_case_annotator.forms;

import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifier;
import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifierService;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.svart.assembly.GenomicAssemblies;

import java.io.File;
import java.util.List;
import java.util.Map;

public class BaseControllerTest {

    // TODO - remove
    public static final File LOCAL_ONTOLOGY_OBO = new File("/home/ielis/data/ontologies/hpo/2021-06-08/hp.json");

    public static final Ontology ONTOLOGY = OntologyLoader.loadOntology(LOCAL_ONTOLOGY_OBO);

    public static final GenomicAssemblyRegistry GENOMIC_ASSEMBLY_REGISTRY = createGenomicAssemblyRegistry();
    public static final FunctionalAnnotationRegistry FUNCTIONAL_ANNOTATION_REGISTRY = new FunctionalAnnotationRegistry();
    public static final GeneIdentifierService GENE_IDENTIFIER_SERVICE = makeGeneIdService();
    public static final ControllerFactory CONTROLLER_FACTORY = new ControllerFactory(GENOMIC_ASSEMBLY_REGISTRY, FUNCTIONAL_ANNOTATION_REGISTRY, GENE_IDENTIFIER_SERVICE);

    private static GenomicAssemblyRegistry createGenomicAssemblyRegistry() {
        GenomicAssemblyRegistry genomicAssemblyRegistry = new GenomicAssemblyRegistry();
        genomicAssemblyRegistry.setHg19Service(new TestGenomicAssemblyService(GenomicAssemblies.GRCh37p13()));
        genomicAssemblyRegistry.setHg38Service(new TestGenomicAssemblyService(GenomicAssemblies.GRCh38p13()));
        return genomicAssemblyRegistry;
    }

    private static TestGeneIdentifierService makeGeneIdService() {
        Map<GeneIdentifier, List<String>> genes = Map.of(
                new GeneIdentifier(TermId.of("NCBIGene:1"), "HNF4A"), List.of("NM_000001.1", "NM_000002.1"),
                new GeneIdentifier(TermId.of("NCBIGene:2200"), "FBN1"), List.of("NM_000003.1", "NM_000004.1"),
                new GeneIdentifier(TermId.of("NCBIGene:3"), "GCK"), List.of("NM_000005.1", "NM_000006.1")
        );
        return new TestGeneIdentifierService(genes);
    }
}
