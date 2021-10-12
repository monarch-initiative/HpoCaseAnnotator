package org.monarchinitiative.hpo_case_annotator.io.test_resources;

import org.monarchinitiative.hpo_case_annotator.model.proto.*;

public final class TestResources {

    public static final String SOFTWARE_VERSION = "Hpo Case Annotator";

    // TODO - create an example of FamilyStudy and CohortStudy and test export into Phenopacket.
    //  Drop support of DiseaseCase as we only export data formatted in the most recent (currently v2) model format.

    private TestResources() {
        // private no-op
    }

    public static DiseaseCase v2Aznarez2003CFTR() {
        // publication
        Publication publication = Publication.newBuilder()
                .setAuthorList("Aznarez I, Chan EM, Zielenski J, Blencowe BJ, Tsui LC").setTitle("Characterization of disease-associated mutations affecting an exonic splicing enhancer and two cryptic splice sites in exon 13 of the cystic fibrosis transmembrane conductance regulator gene")
                .setJournal("Hum Mol Genet").setYear("2003")
                .setVolume("12(16)").setPages("2031-40")
                .setPmid("12913074").build();

        // metadata
        String metadata = "Authors are describing a mutations in CFTR exon 13 that appears to contain two 3'CSS utilization of which is increased when there is a mutation in ESE element present in exon 13 (Figure 2.).\n\nThe 3'CSS whose coordinates are recorded in variants is the dominant one (Figure 2. D, D248). However, there exists also another (D195) which has coordinates: 117232182, 3 splice site, CAATTTAG|TGCAGAAA .";

        // gene
        Gene gene = Gene.newBuilder()
                .setEntrezId(1080)
                .setSymbol("CFTR")
                .build();

        // disease
        Disease disease = Disease.newBuilder()
                .setDatabase("OMIM")
                .setDiseaseId("219700")
                .setDiseaseName("CYSTIC FIBROSIS; CF")
                .build();

        // phenotype

        // family_info
        FamilyInfo familyInfo = FamilyInfo.getDefaultInstance();

        // biocurator
        Biocurator biocurator = Biocurator.newBuilder().setBiocuratorId("HPO:ddanis").build();

        // variants
        // the first variant
        Variant first = Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
                        .setContig("7")
                        .setPos(117232187)
                        .setRefAllele("G")
                        .setAltAllele("T")
                        .build())
                .setSnippet("TTTAGTGCA[G/T]AAAGAAGAA")
                .setGenotype(Genotype.HOMOZYGOUS_ALTERNATE)
                .setVariantClass("splicing")
                .setPathomechanism("splicing|SRE|ESE|binding|decreased")
                .setConsequence("Alternative/cryptic 3' splice site")
                .setCrypticPosition(117232235)
                .setCrypticSpliceSiteSnippet("TTCTCATTAG|AAGGAG")
                .setCrypticSpliceSiteType(CrypticSpliceSiteType.THREE_PRIME)
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.SPLICING)
                        .setMinigeneValidation(true)
                        .build())
                .build();

        // the second variant
        Variant second = Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
                        .setContig("7")
                        .setPos(117232196)
                        .setRefAllele("AA")
                        .setAltAllele("A")
                        .build())
                .setSnippet("GAAAGAAGA[AA/A]TTCAAT")
                .setGenotype(Genotype.HOMOZYGOUS_ALTERNATE)
                .setVariantClass("splicing")
                .setPathomechanism("splicing|SRE|ESE|binding|decreased")
                .setConsequence("Alternative/cryptic 3' splice site")
                .setCrypticPosition(117232235)
                .setCrypticSpliceSiteSnippet("TTCTCATTAG|AAGGAG")
                .setCrypticSpliceSiteType(CrypticSpliceSiteType.THREE_PRIME)
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.SPLICING)
                        .setMinigeneValidation(true)
                        .build())
                .build();
        return DiseaseCase.newBuilder()
                .setPublication(publication)
                .setMetadata(metadata)
                .setGene(gene)
                .setDisease(disease)
                .setFamilyInfo(familyInfo)
                .setBiocurator(biocurator)
                .addVariant(first)
                .addVariant(second)
                .setSoftwareVersion(SOFTWARE_VERSION)
                .build();
    }

    public static DiseaseCase structuralFictionalBreakend() {
        return DiseaseCaseModelExample.beygo2012TCOF1_M18662_Translocation();
    }
}
