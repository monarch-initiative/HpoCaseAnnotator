package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize.variant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.*;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.monarchinitiative.svart.ConfidenceInterval;
import org.monarchinitiative.svart.assembly.GenomicAssemblies;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

public class CuratedVariantDeserializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SimpleModule module = new SimpleModule();

    @BeforeEach
    public void setUp() {
        module.addDeserializer(CuratedVariant.class, new CuratedVariantDeserializer(GenomicAssemblies.GRCh37p13()));
        module.addDeserializer(ConfidenceInterval.class, new ConfidenceIntervalDeserializer());
        module.addDeserializer(MendelianVariantMetadata.class, new MendelianVariantMetadataDeserializer());
        module.addDeserializer(SomaticVariantMetadata.class, new SomaticVariantMetadataDeserializer());
        module.addDeserializer(SplicingVariantMetadata.class, new SplicingVariantMetadataDeserializer());
        module.addDeserializer(StructuralVariantMetadata.class, new StructuralVariantMetadataDeserializer());
        module.addDeserializer(VariantMetadata.class, new DefaultVariantMetadataDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    public void deserializeMendelian() throws Exception {
        String payload = """
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
                  }""";

        CuratedVariant variant = objectMapper.readValue(payload, CuratedVariant.class);
        assertThat(variant, equalTo(TestData.V2.comprehensiveIndividualStudy().getVariants().get(0)));
    }

    @Test
    public void deserializeVariantWithUnknownMetadata() throws Exception {
        String payload = """
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
                }""";
        CuratedVariant variant = objectMapper.readValue(payload, CuratedVariant.class);
        VariantMetadata actual = variant.getVariantMetadata();
        VariantMetadata expected = VariantMetadata.emptyMetadata();
        assertThat(actual, equalTo(expected));
    }
}