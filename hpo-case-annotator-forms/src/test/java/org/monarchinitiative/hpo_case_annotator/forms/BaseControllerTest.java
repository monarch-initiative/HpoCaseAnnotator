package org.monarchinitiative.hpo_case_annotator.forms;

import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifier;
import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifierService;
import org.monarchinitiative.hpo_case_annotator.core.reference.functional.JannovarFunctionalAnnotationService;
import org.monarchinitiative.hpo_case_annotator.model.HpoCaseAnnotatorException;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseIdentifier;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.svart.assembly.GenomicAssemblies;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class BaseControllerTest {

    private static final Path TEST_BASE_DIR = Path.of("src/test/resources/org/monarchinitiative/hpo_case_annotator/forms");
    private static final Path HPO_MODULE_PATH = TEST_BASE_DIR.resolve("hp.module.json");

    public static final Ontology HPO = OntologyLoader.loadOntology(HPO_MODULE_PATH.toFile());
    public static final DiseaseIdentifierService DISEASES = createDiseaseIdentifierService();

    public static final GenomicAssemblyRegistry GENOMIC_ASSEMBLY_REGISTRY = createGenomicAssemblyRegistry();

    public static final FunctionalAnnotationRegistry FUNCTIONAL_ANNOTATION_REGISTRY = createFunctionalAnnotationRegistry();

    private static FunctionalAnnotationRegistry createFunctionalAnnotationRegistry() {
        try {
            FunctionalAnnotationRegistry registry = new FunctionalAnnotationRegistry();
            registry.setHg19Service(JannovarFunctionalAnnotationService.of(TEST_BASE_DIR.resolve("hg19").resolve("hg19_refseq.small.ser")));
            registry.setHg38Service(JannovarFunctionalAnnotationService.of(TEST_BASE_DIR.resolve("hg38").resolve("hg38_refseq.small.ser")));
            return registry;
        } catch (HpoCaseAnnotatorException e) {
            throw new RuntimeException(e);
        }
    }

    public static final GeneIdentifierService GENE_IDENTIFIER_SERVICE = makeGeneIdService();
    public static final ControllerFactory CONTROLLER_FACTORY = new ControllerFactory(GENOMIC_ASSEMBLY_REGISTRY, FUNCTIONAL_ANNOTATION_REGISTRY, GENE_IDENTIFIER_SERVICE);

    private static DiseaseIdentifierService createDiseaseIdentifierService() {
        List<DiseaseIdentifier> diseases = List.of(
                DiseaseIdentifier.of(TermId.of("OMIM:219700"), "CYSTIC FIBROSIS; CF"),
                DiseaseIdentifier.of(TermId.of("OMIM:256000"), "LEIGH SYNDROME; LS"),
                DiseaseIdentifier.of(TermId.of("OMIM:154700"), "MARFAN SYNDROME; MFS"),
                DiseaseIdentifier.of(TermId.of("OMIM:301500"), "FABRY DISEASE")
        );
        return new TestDiseaseIdentifierService(diseases);
    }

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
