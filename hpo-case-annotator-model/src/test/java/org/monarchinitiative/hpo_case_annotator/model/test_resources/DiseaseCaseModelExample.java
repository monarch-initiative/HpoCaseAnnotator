package org.monarchinitiative.hpo_case_annotator.model.test_resources;

import org.monarchinitiative.hpo_case_annotator.model.proto.*;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
class DiseaseCaseModelExample {

    // TODO - add other models for testing here


    static DiseaseCase benMahmoud2013B3GLCT() {
        return DiseaseCase.newBuilder()
                .setGenomeBuild("GRCh37")
                .setPublication(Publication.newBuilder()
                        .setAuthorList("Ben Mahmoud A, Siala O, Mansour RB, Driss F, Baklouti-Gargouri S, Mkaouar-Rebai E, Belguith N, Fakhfakh F")
                        .setTitle("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome")
                        .setJournal("Gene")
                        .setPages("13-7")
                        .setPmid("23954224")
                        .setVolume("532(1)")
                        .setYear("2013")
                        .build())
                .setMetadata("Two Tunisian neonates presented with low birth weight, disproportionate short stature, microcephaly and facial dysmorphism including dolichocephaly, round face and bilateral corneal opacity. " +
                        "\n" + "gDNA, mRNA and minigene analysis revealed heterozygosity for mutation NM_194318.3:c.597-2A>G, which disrupts the 3'ss and causes out-of-frame exon skipping/PTC at the protein level.")
                .setGene(Gene.newBuilder()
                        .setEntrezId(145173)
                        .setSymbol("B3GLCT")
                        .build())
                .addVariant(Variant.newBuilder()
                        .setVariantPosition(VariantPosition.newBuilder()
                                .setGenomeAssembly(GenomeAssembly.HG_19)
                                .setContig("13")
                                .setPos(31843349)
                                .setRefAllele("A")
                                .setAltAllele("G")
                                .build())
                        .setGenotype(Genotype.HETEROZYGOUS)
                        .setSnippet("TTTCT[A/G]GGCTT")
                        .setVariantClass("splicing")
                        .setPathomechanism("splicing|3ss|disrupted")
                        .setConsequence("Exon skipping")
                        .setVariantValidation(VariantValidation.newBuilder()
                                .setContext(VariantValidation.Context.SPLICING)
                                .setComparability(true)
                                .setCDnaSequencingValidation(true)
                                .setMinigeneValidation(true)
                                .setPcrValidation(true)
                                .setRtPcrValidation(true)
                                .build())
                        .build())
                .setDisease(Disease.newBuilder()
                        .setDatabase("OMIM")
                        .setDiseaseId("261540")
                        .setDiseaseName("PETERS-PLUS SYNDROME")
                        .build())
                .addPhenotype(OntologyClass.newBuilder()
                        .setId("HP:0003498")
                        .setLabel("Disproportionate short stature")
                        .setNotObserved(false)
                        .build())
                .addPhenotype(OntologyClass.newBuilder()
                        .setId("HP:0007957")
                        .setLabel("Corneal opacity")
                        .setNotObserved(false)
                        .build())
                .addPhenotype(OntologyClass.newBuilder()
                        .setId("HP:0000268")
                        .setLabel("Dolichocephaly")
                        .setNotObserved(false)
                        .build())
                .addPhenotype(OntologyClass.newBuilder()
                        .setId("HP:0000311")
                        .setLabel("Round face")
                        .setNotObserved(false)
                        .build())
                .addPhenotype(OntologyClass.newBuilder()
                        .setId("HP:0011451")
                        .setLabel("Congenital microcephaly")
                        .setNotObserved(false)
                        .build())
                .addPhenotype(OntologyClass.newBuilder()
                        .setId("HP:0004325")
                        .setLabel("Decreased body weight")
                        .setNotObserved(false)
                        .build())
                .setFamilyInfo(FamilyInfo.newBuilder()
                        .setFamilyOrProbandId("Tunisian patients")
                        .setAge("P25Y") // this is not part of the original curated file, included here for testing
                        .setSex(Sex.MALE)
                        .build())
                .setBiocurator(Biocurator.newBuilder()
                        .setBiocuratorId("HPO:ahegde")
                        .build())
                .build();
    }

    /**
     * @return {@link DiseaseCase} corresponding to content of the <em>'org/monarchinitiative/hpo_case_annotator/model/io/test-model-v2-Aznarez-2003-CFTR.json'</em> file
     */
    static DiseaseCase v2Aznarez2003CFTR() {
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
                .build();
    }

}
