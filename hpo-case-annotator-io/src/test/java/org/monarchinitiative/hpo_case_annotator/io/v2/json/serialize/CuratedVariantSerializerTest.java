package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.monarchinitiative.svart.Coordinates;
import org.monarchinitiative.svart.GenomicVariant;
import org.monarchinitiative.svart.Strand;
import org.monarchinitiative.svart.assembly.GenomicAssemblies;
import org.monarchinitiative.svart.assembly.GenomicAssembly;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class CuratedVariantSerializerTest {

    private static final GenomicAssembly GRCH37 = GenomicAssemblies.GRCh37p13();
    private static final Version VERSION = new Version(1, 0, 0, null, null, null);
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        SimpleModule module = new SimpleModule("TestSerializer", VERSION);
        module.addSerializer(CuratedVariant.class, new CuratedVariantSerializer());

        mapper.registerModule(module);
    }

    @Test
    public void serializeMendelianVariant() throws Exception {
        CuratedVariant curatedVariant = TestData.V2.comprehensiveIndividualStudy().getVariants().get(0);

        String encoded = mapper.writeValueAsString(curatedVariant);

        assertThat(encoded, equalTo("""
                {
                  "variantType" : "SNV",
                  "genomicAssemblyAccession" : "GRCh37.p13",
                  "contigGenBankAccession" : "CM000671.1",
                  "strand" : "+",
                  "start" : 123737056,
                  "end" : 123737057,
                  "changeLength" : 0,
                  "id" : "mendelian",
                  "md5Hex" : "c9dda67d707ab3c69142d891d6a0a4e1",
                  "ref" : "C",
                  "alt" : "G",
                  "validationContext" : "MendelianVariantMetadata",
                  "validationMetadata" : {
                    "variantMetadataContext" : "MENDELIAN",
                    "snippet" : "TTCATTTAC[C/G]TCTACTGG",
                    "variantClass" : "promoter",
                    "pathomechanism" : "promoter|reduced-transcription",
                    "cosegregation" : false,
                    "comparability" : false,
                    "regulator" : "TEST_REGULATOR",
                    "reporterRegulation" : "no",
                    "reporterResidualActivity" : "RES_ACT",
                    "emsaValidationPerformed" : true,
                    "emsaTfSymbol" : "TF_SYMBOL_TEST",
                    "emsaGeneId" : "TF_GENEID_TEST",
                    "otherChoices" : "down",
                    "otherEffect" : "in vitro mRNA expression assay"
                  }
                }"""));
    }

    @Test
    public void serializeUnknownMetadata() throws Exception {
        GenomicVariant gv = GenomicVariant.of(GRCH37.contigByName("1"), "abc", Strand.POSITIVE, Coordinates.zeroBased(10, 20), "N", "<DEL>", -20);
        CuratedVariant curatedVariant = CuratedVariant.of("GRCh37.p13", gv, VariantMetadata.emptyMetadata());
        String encoded = mapper.writeValueAsString(curatedVariant);

        assertThat(encoded, equalTo("""
                {
                  "variantType" : "DEL",
                  "genomicAssemblyAccession" : "GRCh37.p13",
                  "contigGenBankAccession" : "CM000663.1",
                  "strand" : "+",
                  "start" : 10,
                  "end" : 20,
                  "changeLength" : -20,
                  "id" : "abc",
                  "md5Hex" : "1324de4d574dc6bc9d387cc04de7ff43",
                  "ref" : "N",
                  "alt" : "<DEL>",
                  "validationContext" : "VariantMetadataDefault",
                  "validationMetadata" : {
                    "variantMetadataContext" : "UNKNOWN",
                    "snippet" : null,
                    "variantClass" : "N/A",
                    "pathomechanism" : "N/A",
                    "cosegregation" : false,
                    "comparability" : false
                  }
                }"""));

    }
}