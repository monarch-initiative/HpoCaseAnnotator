package org.monarchinitiative.hpo_case_annotator.io.v2.json;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.MendelianVariantMetadata;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.SplicingVariantMetadata;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.StructuralVariantMetadata;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.svart.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class JsonStudyParserTest {

    private static FamilyStudy getFamilyStudy() {
        Publication publication = getPublication();
        List<CuratedVariant> variants = getVariants();
        Pedigree pedigree = getPedigree();
        StudyMetadata metadata = getMetadata();
        return FamilyStudy.of(publication, variants, pedigree, metadata);
    }

    private static CohortStudy getCohortStudy() {
        Publication publication = getPublication();
        List<CuratedVariant> variants = getVariants();
        List<Individual> members = getPedigreeMembers().stream()
                .map(k -> (Individual) k)
                .toList();
        StudyMetadata metadata = getMetadata();
        return CohortStudy.of(publication, variants, members, metadata);
    }

    private static Pedigree getPedigree() {
        return Pedigree.of(getPedigreeMembers());
    }

    private static List<PedigreeMember> getPedigreeMembers() {
        PedigreeMember abel = PedigreeMember.of("Abel", "Adam", "Eve",
                Period.of(21, 5, 23),
                List.of(Disease.of(TermId.of("OMIM:123456"), "Jealousy", true)),
                Map.of(
                        "a", Genotype.HETEROZYGOUS,
                        "b", Genotype.HETEROZYGOUS,
                        "c", Genotype.HOMOZYGOUS_REFERENCE
                ),
                List.of(
                        PhenotypicObservation.of(
                                AgeRange.of(
                                        Period.of(10, 5, 0),
                                        Period.of(20, 0, 0)
                                ),
                                Set.of(
                                        PhenotypicFeature.of(TermId.of("PP:0000001", "Jealousy"), true),
                                        PhenotypicFeature.of(TermId.of("PP:0000002", "Generosity"), false)
                                )
                        ),
                        PhenotypicObservation.of(
                                AgeRange.point(Period.of(15, 10, 0)),
                                Set.of(
                                        PhenotypicFeature.of(TermId.of("PP:0000003", "Shagginess"), false))
                        )
                ),
                true, Sex.MALE);
        PedigreeMember cain = PedigreeMember.of("Cain", "Adam", "Eve",
                Period.of(25, 0, 10),
                List.of(Disease.of(TermId.of("OMIM:123456"), "Jealousy", true)),
                Map.of(
                        "a", Genotype.HETEROZYGOUS,
                        "b", Genotype.HOMOZYGOUS_ALTERNATE,
                        "c", Genotype.HOMOZYGOUS_ALTERNATE
                ),
                List.of(),
                false, Sex.MALE);
        PedigreeMember adam = PedigreeMember.of("Adam", null, null,
                Period.of(48, 3, 14),
                List.of(Disease.of(TermId.of("OMIM:123456"), "Jealousy", false)),
                Map.of(
                        "a", Genotype.HETEROZYGOUS,
                        "b", Genotype.HETEROZYGOUS,
                        "c", Genotype.HETEROZYGOUS
                ),
                List.of(
                        PhenotypicObservation.of(
                                AgeRange.of(
                                        Period.of(14, 5, 0),
                                        Period.of(58, 0, 0)
                                ),
                                Set.of(
                                        PhenotypicFeature.of(TermId.of("PP:0000001", "Jealousy"), false),
                                        PhenotypicFeature.of(TermId.of("PP:0000002", "Generosity"), true)
                                )
                        ),
                        PhenotypicObservation.of(
                                AgeRange.point(Period.of(18, 10, 0)),
                                Set.of(
                                        PhenotypicFeature.of(TermId.of("PP:0000003", "Shagginess"), false))
                        )
                ),
                false, Sex.MALE);
        PedigreeMember eve = PedigreeMember.of("Eve", null, null,
                null,
                List.of(),
                Map.of(
                        "a", Genotype.HOMOZYGOUS_REFERENCE,
                        "b", Genotype.HETEROZYGOUS,
                        "c", Genotype.HETEROZYGOUS
                ),
                List.of(),
                false, Sex.FEMALE);

        return List.of(cain, abel, adam, eve);
    }

    private static StudyMetadata getMetadata() {
        String freeText = "Two Tunisian neonates presented with low birth weight, disproportionate short stature, microcephaly and facial dysmorphism including dolichocephaly, round face and bilateral corneal opacity.\n" +
                "gDNA, mRNA and minigene analysis revealed heterozygosity for mutation NM_194318.3:c.597-2A>G, which disrupts the 3'ss and causes out-of-frame exon skipping/PTC at the protein level.";
        EditHistory created = EditHistory.of("BB:walt", "2.0.0", Instant.parse("2021-01-01T12:00:00.00Z"));
        List<EditHistory> modifiedBy = List.of(
                EditHistory.of("BB:skyler", "2.0.1", Instant.parse("2021-01-02T12:00:00.00Z")),
                EditHistory.of("BB:jesse", "2.0.2", Instant.parse("2021-01-03T12:00:00.00Z"))
        );
        return StudyMetadata.of(freeText, created, modifiedBy);
    }

    private static List<CuratedVariant> getVariants() {
        GenomicAssembly hg38 = GenomicAssemblies.GRCh38p13();
        SplicingVariantMetadata splicingVariantMetadata = SplicingVariantMetadata.of("ACGT[A/C]TCGA", "splicing", "splicing|exon_skipping", true, false,
                -1, SplicingVariantMetadata.CrypticSpliceSiteType.NO_CSS, "", "Exon skipping",
                true, false, false, true, false, false, true, false, false);
        CuratedVariant a = CuratedVariant.sequenceSymbolic(hg38.contigByName("1"), "a", Strand.POSITIVE,
                Coordinates.of(CoordinateSystem.zeroBased(), 1000, 1001),
                "A", "C", 0, splicingVariantMetadata);

        Breakend left = Breakend.of(hg38.contigByName("2"), "b_left", Strand.POSITIVE, Coordinates.of(CoordinateSystem.zeroBased(), 1000, 1000));
        Breakend right = Breakend.of(hg38.contigByName("3"), "b_right", Strand.POSITIVE, Coordinates.of(CoordinateSystem.zeroBased(), 1000, 1000));

        MendelianVariantMetadata mendelianVariantMetadata = MendelianVariantMetadata.of("ACGT[A/C]TCGA", "coding", "coding|translocation", true, false, "regulator", "up",
                "0", false, "ABC", "DEF",
                "no", "no");
        CuratedVariant b = CuratedVariant.breakend("b", left, right, "N", "ACGTACGT", mendelianVariantMetadata);

        GenomicAssembly hg19 = GenomicAssemblies.GRCh37p13();
        VariantMetadata structuralVariantMetadata = StructuralVariantMetadata.of("ACGT[A/ACGTACGT]TCGA", "structural", "structural|coding", true, false);
        CuratedVariant c = CuratedVariant.sequenceSymbolic(hg19.contigByName("1"), "c", Strand.POSITIVE,
                Coordinates.of(CoordinateSystem.zeroBased(), 10_000, 10_001),
                "A", "<DEL>", -100, structuralVariantMetadata);
        return List.of(a, b, c);
    }

    private static Publication getPublication() {
        String authors = "Ben Mahmoud A, Siala O, Mansour RB, Driss F, Baklouti-Gargouri S, Mkaouar-Rebai E, Belguith N, Fakhfakh F";
        return Publication.of(
                Arrays.stream(authors.split(",")).map(String::trim).toList(),
                "First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome",
                "Gene",
                2021,
                "532(1)",
                "13-7",
                "23954224");
    }

    private static String getFamilyStudyPayload() {
        return """
                {
                  "studyMetadata" : {
                    "freeText" : "Two Tunisian neonates presented with low birth weight, disproportionate short stature, microcephaly and facial dysmorphism including dolichocephaly, round face and bilateral corneal opacity.\\ngDNA, mRNA and minigene analysis revealed heterozygosity for mutation NM_194318.3:c.597-2A>G, which disrupts the 3'ss and causes out-of-frame exon skipping/PTC at the protein level.",
                    "createdBy" : {
                      "curatorId" : "BB:walt",
                      "softwareVersion" : "2.0.0",
                      "timestamp" : "2021-01-01T12:00:00Z"
                    },
                    "modifiedBy" : [ {
                      "curatorId" : "BB:skyler",
                      "softwareVersion" : "2.0.1",
                      "timestamp" : "2021-01-02T12:00:00Z"
                    }, {
                      "curatorId" : "BB:jesse",
                      "softwareVersion" : "2.0.2",
                      "timestamp" : "2021-01-03T12:00:00Z"
                    } ]
                  },
                  "publication" : {
                    "authors" : [ "Ben Mahmoud A", "Siala O", "Mansour RB", "Driss F", "Baklouti-Gargouri S", "Mkaouar-Rebai E", "Belguith N", "Fakhfakh F" ],
                    "title" : "First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome",
                    "journal" : "Gene",
                    "year" : 2021,
                    "volume" : "532(1)",
                    "pages" : "13-7",
                    "pmid" : "23954224"
                  },
                  "variants" : [ {
                    "variantType" : "SNV",
                    "contigName" : "CM000663.2",
                    "strand" : "+",
                    "start" : 1000,
                    "end" : 1001,
                    "changeLength" : 0,
                    "id" : "a",
                    "ref" : "A",
                    "alt" : "C",
                    "variantValidation" : {
                      "validationContext" : "SPLICING",
                      "validationMetadata" : {
                        "variantMetadataContext" : "SPLICING",
                        "snippet" : "ACGT[A/C]TCGA",
                        "variantClass" : "splicing",
                        "pathomechanism" : "splicing|exon_skipping",
                        "cosegregation" : true,
                        "comparability" : false,
                        "crypticPosition" : -1,
                        "crypticSpliceSiteType" : "NO_CSS",
                        "crypticSpliceSiteSnippet" : "",
                        "consequence" : "Exon skipping",
                        "minigeneValidation" : true,
                        "siteDirectedMutagenesisValidation" : false,
                        "rtPcrValidation" : false,
                        "srProteinOverexpressionValidation" : true,
                        "srProteinKnockdownValidation" : false,
                        "cDnaSequencingValidation" : false,
                        "pcrValidation" : true,
                        "mutOfWtSpliceSiteValidation" : false,
                        "otherValidation" : false
                      }
                    }
                  }, {
                    "variantType" : "BND",
                    "leftContigName" : "CM000664.2",
                    "leftStrand" : "+",
                    "leftStart" : 1000,
                    "leftEnd" : 1000,
                    "leftId" : "b_left",
                    "rightContigName" : "CM000665.2",
                    "rightStrand" : "+",
                    "rightStart" : 1000,
                    "rightEnd" : 1000,
                    "rightId" : "b_right",
                    "eventId" : "b",
                    "ref" : "N",
                    "alt" : "ACGTACGT",
                    "variantValidation" : {
                      "validationContext" : "MENDELIAN",
                      "validationMetadata" : {
                        "variantMetadataContext" : "MENDELIAN",
                        "snippet" : "ACGT[A/C]TCGA",
                        "variantClass" : "coding",
                        "pathomechanism" : "coding|translocation",
                        "cosegregation" : true,
                        "comparability" : false,
                        "regulator" : "regulator",
                        "reporterRegulation" : "up",
                        "reporterResidualActivity" : "0",
                        "emsaValidationPerformed" : false,
                        "emsaTfSymbol" : "ABC",
                        "emsaGeneId" : "DEF",
                        "otherChoices" : "no",
                        "otherEffect" : "no"
                      }
                    }
                  }, {
                    "variantType" : "DEL",
                    "contigName" : "CM000663.1",
                    "strand" : "+",
                    "start" : 10000,
                    "end" : 10001,
                    "changeLength" : -100,
                    "id" : "c",
                    "ref" : "A",
                    "alt" : "<DEL>",
                    "variantValidation" : {
                      "validationContext" : "STRUCTURAL",
                      "validationMetadata" : {
                        "variantMetadataContext" : "STRUCTURAL",
                        "snippet" : "ACGT[A/ACGTACGT]TCGA",
                        "variantClass" : "structural",
                        "pathomechanism" : "structural|coding",
                        "cosegregation" : true,
                        "comparability" : false
                      }
                    }
                  } ],
                  "pedigree" : {
                    "members" : [ {
                      "id" : "Abel",
                      "age" : "P21Y5M23D",
                      "diseases" : [ {
                        "diseaseId" : "OMIM:123456",
                        "diseaseName" : "Jealousy",
                        "isExcluded" : true
                      } ],
                      "genotypes" : [ {
                        "variantId" : "a",
                        "genotype" : "HETEROZYGOUS"
                      }, {
                        "variantId" : "b",
                        "genotype" : "HETEROZYGOUS"
                      }, {
                        "variantId" : "c",
                        "genotype" : "HOMOZYGOUS_REFERENCE"
                      } ],
                      "phenotypicObservations" : [ {
                        "observationAge" : {
                          "startAge" : "P10Y5M",
                          "endAge" : "P20Y"
                        },
                        "phenotypicFeatures" : [ {
                          "termId" : "PP:0000002:Generosity",
                          "isExcluded" : false
                        }, {
                          "termId" : "PP:0000001:Jealousy",
                          "isExcluded" : true
                        } ]
                      }, {
                        "observationAge" : {
                          "startAge" : "P15Y10M",
                          "endAge" : "P15Y10M"
                        },
                        "phenotypicFeatures" : [ {
                          "termId" : "PP:0000003:Shagginess",
                          "isExcluded" : false
                        } ]
                      } ],
                      "sex" : "MALE",
                      "paternalId" : "Adam",
                      "maternalId" : "Eve",
                      "isProband" : true
                    }, {
                      "id" : "Adam",
                      "age" : "P48Y3M14D",
                      "diseases" : [ {
                        "diseaseId" : "OMIM:123456",
                        "diseaseName" : "Jealousy",
                        "isExcluded" : false
                      } ],
                      "genotypes" : [ {
                        "variantId" : "a",
                        "genotype" : "HETEROZYGOUS"
                      }, {
                        "variantId" : "b",
                        "genotype" : "HETEROZYGOUS"
                      }, {
                        "variantId" : "c",
                        "genotype" : "HETEROZYGOUS"
                      } ],
                      "phenotypicObservations" : [ {
                        "observationAge" : {
                          "startAge" : "P14Y5M",
                          "endAge" : "P58Y"
                        },
                        "phenotypicFeatures" : [ {
                          "termId" : "PP:0000002:Generosity",
                          "isExcluded" : true
                        }, {
                          "termId" : "PP:0000001:Jealousy",
                          "isExcluded" : false
                        } ]
                      }, {
                        "observationAge" : {
                          "startAge" : "P18Y10M",
                          "endAge" : "P18Y10M"
                        },
                        "phenotypicFeatures" : [ {
                          "termId" : "PP:0000003:Shagginess",
                          "isExcluded" : false
                        } ]
                      } ],
                      "sex" : "MALE",
                      "paternalId" : null,
                      "maternalId" : null,
                      "isProband" : false
                    }, {
                      "id" : "Cain",
                      "age" : "P25Y10D",
                      "diseases" : [ {
                        "diseaseId" : "OMIM:123456",
                        "diseaseName" : "Jealousy",
                        "isExcluded" : true
                      } ],
                      "genotypes" : [ {
                        "variantId" : "a",
                        "genotype" : "HETEROZYGOUS"
                      }, {
                        "variantId" : "b",
                        "genotype" : "HOMOZYGOUS_ALTERNATE"
                      }, {
                        "variantId" : "c",
                        "genotype" : "HOMOZYGOUS_ALTERNATE"
                      } ],
                      "phenotypicObservations" : [ ],
                      "sex" : "MALE",
                      "paternalId" : "Adam",
                      "maternalId" : "Eve",
                      "isProband" : false
                    }, {
                      "id" : "Eve",
                      "age" : null,
                      "diseases" : [ ],
                      "genotypes" : [ {
                        "variantId" : "a",
                        "genotype" : "HOMOZYGOUS_REFERENCE"
                      }, {
                        "variantId" : "b",
                        "genotype" : "HETEROZYGOUS"
                      }, {
                        "variantId" : "c",
                        "genotype" : "HETEROZYGOUS"
                      } ],
                      "phenotypicObservations" : [ ],
                      "sex" : "FEMALE",
                      "paternalId" : null,
                      "maternalId" : null,
                      "isProband" : false
                    } ]
                  }
                }""";
    }

    private static String getCohortStudyPayload() {
        return """
                {
                  "studyMetadata" : {
                    "freeText" : "Two Tunisian neonates presented with low birth weight, disproportionate short stature, microcephaly and facial dysmorphism including dolichocephaly, round face and bilateral corneal opacity.\\ngDNA, mRNA and minigene analysis revealed heterozygosity for mutation NM_194318.3:c.597-2A>G, which disrupts the 3'ss and causes out-of-frame exon skipping/PTC at the protein level.",
                    "createdBy" : {
                      "curatorId" : "BB:walt",
                      "softwareVersion" : "2.0.0",
                      "timestamp" : "2021-01-01T12:00:00Z"
                    },
                    "modifiedBy" : [ {
                      "curatorId" : "BB:skyler",
                      "softwareVersion" : "2.0.1",
                      "timestamp" : "2021-01-02T12:00:00Z"
                    }, {
                      "curatorId" : "BB:jesse",
                      "softwareVersion" : "2.0.2",
                      "timestamp" : "2021-01-03T12:00:00Z"
                    } ]
                  },
                  "publication" : {
                    "authors" : [ "Ben Mahmoud A", "Siala O", "Mansour RB", "Driss F", "Baklouti-Gargouri S", "Mkaouar-Rebai E", "Belguith N", "Fakhfakh F" ],
                    "title" : "First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome",
                    "journal" : "Gene",
                    "year" : 2021,
                    "volume" : "532(1)",
                    "pages" : "13-7",
                    "pmid" : "23954224"
                  },
                  "variants" : [ {
                    "variantType" : "SNV",
                    "contigName" : "CM000663.2",
                    "strand" : "+",
                    "start" : 1000,
                    "end" : 1001,
                    "changeLength" : 0,
                    "id" : "a",
                    "ref" : "A",
                    "alt" : "C",
                    "variantValidation" : {
                      "validationContext" : "SPLICING",
                      "validationMetadata" : {
                        "variantMetadataContext" : "SPLICING",
                        "snippet" : "ACGT[A/C]TCGA",
                        "variantClass" : "splicing",
                        "pathomechanism" : "splicing|exon_skipping",
                        "cosegregation" : true,
                        "comparability" : false,
                        "crypticPosition" : -1,
                        "crypticSpliceSiteType" : "NO_CSS",
                        "crypticSpliceSiteSnippet" : "",
                        "consequence" : "Exon skipping",
                        "minigeneValidation" : true,
                        "siteDirectedMutagenesisValidation" : false,
                        "rtPcrValidation" : false,
                        "srProteinOverexpressionValidation" : true,
                        "srProteinKnockdownValidation" : false,
                        "cDnaSequencingValidation" : false,
                        "pcrValidation" : true,
                        "mutOfWtSpliceSiteValidation" : false,
                        "otherValidation" : false
                      }
                    }
                  }, {
                    "variantType" : "BND",
                    "leftContigName" : "CM000664.2",
                    "leftStrand" : "+",
                    "leftStart" : 1000,
                    "leftEnd" : 1000,
                    "leftId" : "b_left",
                    "rightContigName" : "CM000665.2",
                    "rightStrand" : "+",
                    "rightStart" : 1000,
                    "rightEnd" : 1000,
                    "rightId" : "b_right",
                    "eventId" : "b",
                    "ref" : "N",
                    "alt" : "ACGTACGT",
                    "variantValidation" : {
                      "validationContext" : "MENDELIAN",
                      "validationMetadata" : {
                        "variantMetadataContext" : "MENDELIAN",
                        "snippet" : "ACGT[A/C]TCGA",
                        "variantClass" : "coding",
                        "pathomechanism" : "coding|translocation",
                        "cosegregation" : true,
                        "comparability" : false,
                        "regulator" : "regulator",
                        "reporterRegulation" : "up",
                        "reporterResidualActivity" : "0",
                        "emsaValidationPerformed" : false,
                        "emsaTfSymbol" : "ABC",
                        "emsaGeneId" : "DEF",
                        "otherChoices" : "no",
                        "otherEffect" : "no"
                      }
                    }
                  }, {
                    "variantType" : "DEL",
                    "contigName" : "CM000663.1",
                    "strand" : "+",
                    "start" : 10000,
                    "end" : 10001,
                    "changeLength" : -100,
                    "id" : "c",
                    "ref" : "A",
                    "alt" : "<DEL>",
                    "variantValidation" : {
                      "validationContext" : "STRUCTURAL",
                      "validationMetadata" : {
                        "variantMetadataContext" : "STRUCTURAL",
                        "snippet" : "ACGT[A/ACGTACGT]TCGA",
                        "variantClass" : "structural",
                        "pathomechanism" : "structural|coding",
                        "cosegregation" : true,
                        "comparability" : false
                      }
                    }
                  } ],
                  "individuals" : [ {
                    "id" : "Abel",
                    "age" : "P21Y5M23D",
                    "diseases" : [ {
                      "diseaseId" : "OMIM:123456",
                      "diseaseName" : "Jealousy",
                      "isExcluded" : true
                    } ],
                    "genotypes" : [ {
                      "variantId" : "a",
                      "genotype" : "HETEROZYGOUS"
                    }, {
                      "variantId" : "b",
                      "genotype" : "HETEROZYGOUS"
                    }, {
                      "variantId" : "c",
                      "genotype" : "HOMOZYGOUS_REFERENCE"
                    } ],
                    "phenotypicObservations" : [ {
                      "observationAge" : {
                        "startAge" : "P10Y5M",
                        "endAge" : "P20Y"
                      },
                      "phenotypicFeatures" : [ {
                        "termId" : "PP:0000002:Generosity",
                        "isExcluded" : false
                      }, {
                        "termId" : "PP:0000001:Jealousy",
                        "isExcluded" : true
                      } ]
                    }, {
                      "observationAge" : {
                        "startAge" : "P15Y10M",
                        "endAge" : "P15Y10M"
                      },
                      "phenotypicFeatures" : [ {
                        "termId" : "PP:0000003:Shagginess",
                        "isExcluded" : false
                      } ]
                    } ],
                    "sex" : "MALE",
                    "paternalId" : "Adam",
                    "maternalId" : "Eve",
                    "isProband" : true
                  }, {
                    "id" : "Adam",
                    "age" : "P48Y3M14D",
                    "diseases" : [ {
                      "diseaseId" : "OMIM:123456",
                      "diseaseName" : "Jealousy",
                      "isExcluded" : false
                    } ],
                    "genotypes" : [ {
                      "variantId" : "a",
                      "genotype" : "HETEROZYGOUS"
                    }, {
                      "variantId" : "b",
                      "genotype" : "HETEROZYGOUS"
                    }, {
                      "variantId" : "c",
                      "genotype" : "HETEROZYGOUS"
                    } ],
                    "phenotypicObservations" : [ {
                      "observationAge" : {
                        "startAge" : "P14Y5M",
                        "endAge" : "P58Y"
                      },
                      "phenotypicFeatures" : [ {
                        "termId" : "PP:0000002:Generosity",
                        "isExcluded" : true
                      }, {
                        "termId" : "PP:0000001:Jealousy",
                        "isExcluded" : false
                      } ]
                    }, {
                      "observationAge" : {
                        "startAge" : "P18Y10M",
                        "endAge" : "P18Y10M"
                      },
                      "phenotypicFeatures" : [ {
                        "termId" : "PP:0000003:Shagginess",
                        "isExcluded" : false
                      } ]
                    } ],
                    "sex" : "MALE",
                    "paternalId" : null,
                    "maternalId" : null,
                    "isProband" : false
                  }, {
                    "id" : "Cain",
                    "age" : "P25Y10D",
                    "diseases" : [ {
                      "diseaseId" : "OMIM:123456",
                      "diseaseName" : "Jealousy",
                      "isExcluded" : true
                    } ],
                    "genotypes" : [ {
                      "variantId" : "a",
                      "genotype" : "HETEROZYGOUS"
                    }, {
                      "variantId" : "b",
                      "genotype" : "HOMOZYGOUS_ALTERNATE"
                    }, {
                      "variantId" : "c",
                      "genotype" : "HOMOZYGOUS_ALTERNATE"
                    } ],
                    "phenotypicObservations" : [ ],
                    "sex" : "MALE",
                    "paternalId" : "Adam",
                    "maternalId" : "Eve",
                    "isProband" : false
                  }, {
                    "id" : "Eve",
                    "age" : null,
                    "diseases" : [ ],
                    "genotypes" : [ {
                      "variantId" : "a",
                      "genotype" : "HOMOZYGOUS_REFERENCE"
                    }, {
                      "variantId" : "b",
                      "genotype" : "HETEROZYGOUS"
                    }, {
                      "variantId" : "c",
                      "genotype" : "HETEROZYGOUS"
                    } ],
                    "phenotypicObservations" : [ ],
                    "sex" : "FEMALE",
                    "paternalId" : null,
                    "maternalId" : null,
                    "isProband" : false
                  } ]
                }""";
    }

    // ---------------------------------- FamilyStudy ------------------------------------------------------------------

    @Test
    public void deserializeFamilyStudy() throws Exception {
        String payload = getFamilyStudyPayload();

        JsonStudyParser parser = new JsonStudyParser(GenomicAssemblies.GRCh37p13(), GenomicAssemblies.GRCh38p13());
        Study study = parser.read(new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)));

        FamilyStudy expected = getFamilyStudy();
        assertThat(study, equalTo(expected));
    }

    @Test
    @Disabled // used to generate paylod to compare with in deserialize test
    public void serializeFamilyStudy() throws Exception {
        FamilyStudy study = getFamilyStudy();

        JsonStudyParser parser = new JsonStudyParser(GenomicAssemblies.GRCh37p13(), GenomicAssemblies.GRCh38p13());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        parser.write(study, os);

        System.err.println(os);
    }

    @Test
    public void roundTripFamilyStudy() throws IOException {
        FamilyStudy familyStudy = getFamilyStudy();

        JsonStudyParser jsonStudyParser = new JsonStudyParser(GenomicAssemblies.GRCh37p13(), GenomicAssemblies.GRCh38p13());

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        jsonStudyParser.write(familyStudy, os);
        Study deserialized = jsonStudyParser.read(os.toString());

        assertThat(familyStudy, equalTo(deserialized));
    }

    // ---------------------------------- CohortStudy ------------------------------------------------------------------


    @Test
    @Disabled // TODO - fix the issue with individual/pedigree member instances
    public void deserializeCohortStudy() throws Exception {
        String payload = getCohortStudyPayload();

        JsonStudyParser parser = new JsonStudyParser(GenomicAssemblies.GRCh37p13(), GenomicAssemblies.GRCh38p13());
        Study study = parser.read(payload);

        assertThat(getCohortStudy(), equalTo(study));
    }

    @Test
    @Disabled // used to generate paylod to compare with in deserialize test
    public void serializeCohortStudy() throws Exception {
        CohortStudy study = getCohortStudy();

        JsonStudyParser parser = new JsonStudyParser(GenomicAssemblies.GRCh37p13(), GenomicAssemblies.GRCh38p13());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        parser.write(study, os);

        System.err.println(os);
    }

    @Test
    @Disabled // TODO - fix the issue with individual/pedigree member instances
    public void roundTripCohortStudy() throws IOException {
        CohortStudy cohortStudy = getCohortStudy();

        JsonStudyParser jsonStudyParser = new JsonStudyParser(GenomicAssemblies.GRCh37p13(), GenomicAssemblies.GRCh38p13());

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        jsonStudyParser.write(cohortStudy, os);
        Study deserialized = jsonStudyParser.read(os.toString());

        assertThat(cohortStudy, equalTo(deserialized));
    }

}