package org.monarchinitiative.hpo_case_annotator.test;

import org.monarchinitiative.hpo_case_annotator.model.proto.*;

import java.util.List;

class V1 {

    static DiseaseCase comprehensiveCase() {
        Publication publication = getPublication();
        String metadata = "Authors are describing a mutations in CFTR exon 13 that appears to contain two 3'CSS utilization of which is increased when there is a mutation in ESE element present in exon 13 (Figure 2.).\n\nThe 3'CSS whose coordinates are recorded in variants is the dominant one (Figure 2. D, D248). However, there exists also another (D195) which has coordinates: 117232182, 3 splice site, CAATTTAG|TGCAGAAA .";
        Gene gene = getGene();
        Disease disease = getDisease();
        List<OntologyClass> phenotype = getPhenotypes();
        FamilyInfo familyInfo = getFamilyInfo();
        Biocurator biocurator = getBiocurator();

        List<Variant> variants = List.of(mendelianVariant(), somaticVariant(), splicingVariant(), symbolicDeletion(), symbolicBreakendVariant());

        return DiseaseCase.newBuilder()
                .setPublication(publication)
                .setMetadata(metadata)
                .setGene(gene)
                .setDisease(disease)
                .addAllPhenotype(phenotype)
                .setFamilyInfo(familyInfo)
                .setBiocurator(biocurator)
                .addAllVariant(variants)
                .setSoftwareVersion("Hpo Case Annotator")
                .build();
    }

    private static Publication getPublication() {
        return Publication.newBuilder().setAuthorList("Beygó J, Buiting K, Seland S, Lüdecke HJ, Hehr U, Lich C, Prager B, Lohmann DR, Wieczorek D")
                .setTitle("First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome")
                .setJournal("Mol Syndromol")
                .setYear("2012")
                .setVolume("2(2)")
                .setPages("53-59")
                .setPmid("22712005")
                .build();
    }

    private static Gene getGene() {
        return Gene.newBuilder()
                .setEntrezId(1080)
                .setSymbol("CFTR")
                .build();
    }

    private static Disease getDisease() {
        return Disease.newBuilder()
                .setDatabase("OMIM")
                .setDiseaseId("219700")
                .setDiseaseName("CYSTIC FIBROSIS; CF")
                .build();
    }

    private static List<OntologyClass> getPhenotypes() {
        OntologyClass someFeature = OntologyClass.newBuilder()
                .setId("HP:1234567")
                .setLabel("Some feature")
                .setNotObserved(false)
                .build();
        OntologyClass otherFeature = OntologyClass.newBuilder()
                .setId("HP:9876543")
                .setLabel("Other feature")
                .setNotObserved(true)
                .build();
        return List.of(someFeature, otherFeature);
    }

    private static FamilyInfo getFamilyInfo() {
        return FamilyInfo.newBuilder()
                .setFamilyOrProbandId("FAM:001")
                .setAge("P10Y5M4D")
                .setSex(Sex.MALE)
                .build();
    }

    private static Biocurator getBiocurator() {
        return Biocurator.newBuilder()
                .setBiocuratorId("HPO:walterwhite")
                .build();
    }


    private static Variant mendelianVariant() {
        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.HG_19)
                        .setContig("9")
                        .setPos(123_737_057)
                        .setRefAllele("C")
                        .setAltAllele("G")
                        .build())
                .setSnippet("TTCATTTAC[C/G]TCTACTGGC")
                .setGenotype(Genotype.HOMOZYGOUS_ALTERNATE)
                .setVariantClass("promoter")
                .setPathomechanism("promoter|reduced-transcription")
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.MENDELIAN)
                        .setComparability(false)
                        .setCosegregation(false)
                        .setEmsaValidationPerformed(true)
                        .setEmsaGeneId("TF_GENEID_TEST")
                        .setEmsaTfSymbol("TF_SYMBOL_TEST")
                        .setOtherChoices("down")
                        .setOtherEffect("in vitro mRNA expression assay")
                        .setRegulator("TEST_REGULATOR")
                        .setReporterRegulation("no")
                        .setReporterResidualActivity("RES_ACT")
                        .build())
                .build();
    }

    private static Variant somaticVariant() {
        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.HG_19)
                        .setContig("9")
                        .setPos(123737057)
                        .setRefAllele("C")
                        .setAltAllele("A")
                        .build())
                .setSnippet("TTCATTTAC[C/A]TCTACTGG")
                .setGenotype(Genotype.HETEROZYGOUS)
                .setVariantClass("5UTR")
                .setPathomechanism("5UTR|transcription")
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.SOMATIC)
                        .setEmsaGeneId("TF_GENE_SYMBOL_TEST")
                        .setEmsaTfSymbol("TF_EMSA_SOM_TEST")
                        .setMPatients(100)
                        .setNPatients(78)
                        .setOtherChoices("up")
                        .setOtherEffect("Transgenic model")
                        .setRegulator("SOMATIC_REGULATOR")
                        .setReporterRegulation("up")
                        .setReporterResidualActivity("SOMATIC_RP_PERC")
                        .build())
                .build();
    }

    private static Variant splicingVariant() {
        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.HG_19)
                        .setContig("9")
                        .setPos(123737057)
                        .setRefAllele("C")
                        .setAltAllele("T")
                        .build())
                .setSnippet("TTCATTTAC[C/T]TCTACTGG")
                .setGenotype(Genotype.HOMOZYGOUS_ALTERNATE)
                .setVariantClass("splicing")
                .setPathomechanism("splicing|3css|activated")
                .setConsequence("Exon skipping")
                .setCrypticPosition(123737090)
                .setCrypticSpliceSiteType(CrypticSpliceSiteType.FIVE_PRIME)
                .setCrypticSpliceSiteSnippet("ATATG|GCGAGTTCTT")
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.SPLICING)
                        .setCosegregation(true)
                        .setComparability(false)
                        .setMinigeneValidation(true)
                        .setRtPcrValidation(true)
                        .setSrProteinKnockdownValidation(true)
                        .setCDnaSequencingValidation(true)
                        .setOtherValidation(true)
                        .build())
                .build();
    }

    private static Variant symbolicDeletion() {
        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
                        .setContig("5")
                        .setPos(149741531)
                        .setPos2(149744897)
                        .setRefAllele("N")
                        .setAltAllele("DEL")
                        .setCiBeginOne(-5)
                        .setCiBeginTwo(5)
                        .setCiEndOne(-15)
                        .setCiEndTwo(10)
                        .build())
                .setVariantClass("structural")
                .setSvType(StructuralType.DEL)
                .setGenotype(Genotype.HETEROZYGOUS)
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.INTRACHROMOSOMAL)
                        .build())
                .setImprecise(true)
                .build();
    }

    private static Variant symbolicBreakendVariant() {
        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
                        .setContig("9")
                        .setPos(133_359_000)
                        .setContigDirection(VariantPosition.Direction.FWD)
                        .setCiBeginOne(-5)
                        .setCiEndOne(10)

                        .setContig2("13")
                        .setPos2(32_300_000)
                        .setContig2Direction(VariantPosition.Direction.FWD)
                        .setCiBeginTwo(-15)
                        .setCiEndTwo(20)

                        .setRefAllele("G")
                        .build())
                .setVariantClass("structural")
                .setSvType(StructuralType.BND)
                .setGenotype(Genotype.HETEROZYGOUS)
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.TRANSLOCATION)
                        .build())
                .setImprecise(true)
                .build();
    }

}
