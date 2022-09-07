package org.monarchinitiative.hpo_case_annotator.observable.v2;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.MendelianVariantMetadata;
import org.monarchinitiative.svart.*;
import org.monarchinitiative.svart.assembly.GenomicAssemblies;
import org.monarchinitiative.svart.assembly.GenomicAssembly;
import org.monarchinitiative.svart.util.VariantTrimmer;
import org.monarchinitiative.svart.util.VcfConverter;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@Disabled
public class ObservableGenomicBreakendTest {

    private static final MendelianVariantMetadata DUMMY_METADATA = MendelianVariantMetadata.of("", "", "", true, false, "", "", "", false, "", "", "", "");

    private final GenomicAssembly genomicAssembly = GenomicAssemblies.GRCh37p13();
    private final VcfConverter vcfConverter = new VcfConverter(genomicAssembly, VariantTrimmer.leftShiftingTrimmer(VariantTrimmer.retainingCommonBase()));

    @Test
    public void vcfSpecsExamples_bnd_U() {
        // TODO - finish implementing input of breakend coordinates.
        GenomicBreakendVariant bnd_U = vcfConverter.convertBreakend(
                vcfConverter.parseContig("1"),
                "bnd_U",
                123_456,
                ConfidenceInterval.precise(),
                "C",
                "C[2:321682[",
                ConfidenceInterval.precise(),
                "bnd_V",
                "tra2");

        ObservableCuratedVariant ocv = new ObservableCuratedVariant(CuratedVariant.of(genomicAssembly.name(), bnd_U, DUMMY_METADATA));

        assertThat(ocv.getGenomicAssembly(), equalTo("GRCh37.p13"));
        assertThat(ocv.getId(), equalTo("tra2"));

        ObservableGenomicBreakend left = ocv.getLeft();
        assertThat(left.getContig().name(), equalTo("1"));
        assertThat(left.getPos(), equalTo(123_456));
        assertThat(left.getId(), equalTo("bnd_U"));
        assertThat(left.getStrand(), equalTo(Strand.POSITIVE));

        ObservableGenomicBreakend right = ocv.getRight();
        assertThat(right.getContig().name(), equalTo("2"));
        assertThat(right.getPos(), equalTo(321_682));
        assertThat(right.getId(), equalTo("bnd_V"));
        assertThat(right.getStrand(), equalTo(Strand.POSITIVE));
    }

    @Test
    public void vcfSpecsExamples_bnd_Y() {
        GenomicBreakendVariant bnd_Y = vcfConverter.convertBreakend(
                vcfConverter.parseContig("17"),
                "bnd_Y",
                198_982,
                ConfidenceInterval.precise(),
                "A",
                "A]2:321681]",
                ConfidenceInterval.precise(),
                "bnd_W",
                "tra1");

        ObservableCuratedVariant ocv = new ObservableCuratedVariant(CuratedVariant.of(genomicAssembly.name(), bnd_Y, DUMMY_METADATA));

        assertThat(ocv.getGenomicAssembly(), equalTo("GRCh37.p13"));
        assertThat(ocv.getId(), equalTo("tra1"));

        ObservableGenomicBreakend left = ocv.getLeft();
        assertThat(left.getContig().name(), equalTo("1"));
        assertThat(left.getPos(), equalTo(123_456));
        assertThat(left.getId(), equalTo("bnd_U"));
        assertThat(left.getStrand(), equalTo(Strand.POSITIVE));

        ObservableGenomicBreakend right = ocv.getRight();
        assertThat(right.getContig().name(), equalTo("2"));
        assertThat(right.getPos(), equalTo(321682));
        assertThat(right.getId(), equalTo("bnd_V"));
        assertThat(right.getStrand(), equalTo(Strand.POSITIVE));
    }

}