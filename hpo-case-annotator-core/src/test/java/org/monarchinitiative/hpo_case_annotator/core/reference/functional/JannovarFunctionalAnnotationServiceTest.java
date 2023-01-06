package org.monarchinitiative.hpo_case_annotator.core.reference.functional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.svart.CoordinateSystem;
import org.monarchinitiative.svart.Coordinates;
import org.monarchinitiative.svart.GenomicVariant;
import org.monarchinitiative.svart.Strand;
import org.monarchinitiative.svart.assembly.GenomicAssemblies;
import org.monarchinitiative.svart.assembly.GenomicAssembly;

import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class JannovarFunctionalAnnotationServiceTest {

    private static final Path SMALL_DB_PATH = Path.of("src/test/resources/org/monarchinitiative/hpo_case_annotator/core/reference/functional/jannovar-SURF1_SURF2_FBN1.refseq.ser");
    private static final GenomicAssembly ASSEMBLY = GenomicAssemblies.GRCh38p13();

    private static JannovarFunctionalAnnotationService SERVICE;

    @BeforeAll
    public static void setUp() throws Exception {
        SERVICE = JannovarFunctionalAnnotationService.of(SMALL_DB_PATH);
    }

    @Test
    public void annotate_SURF2() {
        GenomicVariant variant = GenomicVariant.of(ASSEMBLY.contigByName("chr9"), "IN_SURF2", Strand.POSITIVE, Coordinates.of(CoordinateSystem.oneBased(), 133_361_057, 133_361_057), "A", "C");
        List<FunctionalAnnotation> annotations = SERVICE.annotate(variant);

        assertThat(annotations, hasSize(2));
        assertThat(annotations, hasItem(FunctionalAnnotation.of("SURF2", "NM_017503.4", List.of("MISSENSE_VARIANT", "SPLICE_REGION_VARIANT"), "c.689A>C", "p.(K230T)")));
        assertThat(annotations, hasItem(FunctionalAnnotation.of("SURF2", "NM_001278928.1", List.of("SPLICE_ACCEPTOR_VARIANT", "CODING_TRANSCRIPT_INTRON_VARIANT"), "c.688-2A>C", "p.?")));
    }

    @Test
    public void annotate_FBN1() {
        GenomicVariant variant = GenomicVariant.of(ASSEMBLY.contigByName("chr15"), "IN_FBN1", Strand.POSITIVE, Coordinates.of(CoordinateSystem.oneBased(), 48_441_768, 48_441_768), "A", "AT");
        List<FunctionalAnnotation> annotations = SERVICE.annotate(variant);

        assertThat(annotations, hasSize(1));
        assertThat(annotations, hasItem(FunctionalAnnotation.of("FBN1", "NM_000138.4", List.of("FRAMESHIFT_ELONGATION"), "c.6115_6116insA", "p.(L2039Hfs*28)")));
    }
}