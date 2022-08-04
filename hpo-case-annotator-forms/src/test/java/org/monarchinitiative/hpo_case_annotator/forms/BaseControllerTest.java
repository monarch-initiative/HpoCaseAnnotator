package org.monarchinitiative.hpo_case_annotator.forms;

import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifier;
import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifierService;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.svart.assembly.GenomicAssemblies;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class BaseControllerTest {

    public static final Path TEST_BASE_PATH = Path.of("src/test/resources/org/monarchinitiative/hpo_case_annotator/forms");
    /**
     * HPO module containing all ancestors of `HP:0001166` Arachnodactyly and complete sub-hierarchies
     * except from `HP:0000118` Phenotypic abnormality.
     */
    public static final Path HPO_MODULE_PATH = TEST_BASE_PATH.resolve("hp.module.json");

    public static final Ontology HPO = OntologyLoader.loadOntology(HPO_MODULE_PATH.toFile());

    public static final GenomicAssemblyRegistry GENOMIC_ASSEMBLY_REGISTRY = createGenomicAssemblyRegistry();
    public static final FunctionalAnnotationRegistry FUNCTIONAL_ANNOTATION_REGISTRY = new FunctionalAnnotationRegistry();
    public static final GeneIdentifierService GENE_IDENTIFIER_SERVICE = makeGeneIdService();
    public static final ControllerFactory CONTROLLER_FACTORY = new ControllerFactory(GENOMIC_ASSEMBLY_REGISTRY, FUNCTIONAL_ANNOTATION_REGISTRY, GENE_IDENTIFIER_SERVICE, HPO);

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
