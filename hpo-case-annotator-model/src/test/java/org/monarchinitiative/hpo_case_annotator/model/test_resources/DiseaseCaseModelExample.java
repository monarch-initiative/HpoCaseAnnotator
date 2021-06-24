package org.monarchinitiative.hpo_case_annotator.model.test_resources;

import org.monarchinitiative.hpo_case_annotator.model.proto.*;

import java.util.ArrayList;
import java.util.List;

import static org.monarchinitiative.hpo_case_annotator.model.test_resources.TestResources.SOFTWARE_VERSION;

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
                .setSoftwareVersion(SOFTWARE_VERSION)
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
                .setSoftwareVersion(SOFTWARE_VERSION)
                .build();
    }


    /**
     * This method programmatically creates complete model instance for testing. The model contains three variants: one
     * Splicing variant, one Mendelian variant and one Somatic variant with all fields initialized. <p>The data of model
     * are the same as those in XML file <em>src/test/resources/models/xml/Aguilar-Ramirez-2009-C5.xml</em>
     *
     * @return {@link DiseaseCase} instance for testing.
     */
    static DiseaseCase aguilar_Ramirez_2009_C5() {

        /* Genome build */
        return DiseaseCase.newBuilder()

                /* Publication data */
                .setPublication(
                        Publication.newBuilder().setAuthorList("Aguilar-Ramírez P, Reis ES, Florido MP, Barbosa AS, Farah CS, Costa-Carvalho BT, Isaac L")
                                .setTitle("Skipping of exon 30 in C5 gene results in complete human C5 deficiency and demonstrates the importance of C5d and CUB domains for stability")
                                .setJournal("Mol Immunol")
                                .setYear("2009")
                                .setVolume("46(10)")
                                .setPages("2116-23")
                                .setPmid("19375167")
                                .build())
                .setMetadata("Authors describe proband coming from large family with history of consanguinity carrying " +
                        "primary complete C5 deficiency. Blah, blah...")
                .setGene(Gene.newBuilder()
                        .setSymbol("C5")
                        .setEntrezId(727)
                        .build())
                /* Variants which belong to this model */
                .addVariant(Variant.newBuilder()
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
                                .setCosegregation(true)
                                .setComparability(false)
                                .setMinigeneValidation(true)
                                .setRtPcrValidation(true)
                                .setSrProteinKnockdownValidation(true)
                                .setCDnaSequencingValidation(true)
                                .setOtherValidation(true)
                                .build())
                        .build())
                .addVariant(Variant.newBuilder()
                        .setVariantPosition(VariantPosition.newBuilder()
                                .setGenomeAssembly(GenomeAssembly.HG_19)
                                .setContig("9")
                                .setPos(123737057)
                                .setRefAllele("C")
                                .setAltAllele("G")
                                .build())
                        .setSnippet("TTCATTTAC[C/G]TCTACTGG")
                        .setGenotype(Genotype.HOMOZYGOUS_ALTERNATE)
                        .setVariantClass("promoter")
                        .setPathomechanism("promoter|reduced-transcription")
                        .setVariantValidation(VariantValidation.newBuilder()
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
                        .build())
                .addVariant(Variant.newBuilder()
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
                        .build())
                /* Family/proband information */
                .setFamilyInfo(FamilyInfo.newBuilder()
                        .setFamilyOrProbandId("1/II:9")
                        .setSex(Sex.MALE)
                        .setAge("19")
                        .build())
                /* HPO terms */
                .addPhenotype(OntologyClass.newBuilder()
                        .setId("HP:0001287")
                        .setLabel("Meningitis")
                        .setNotObserved(false)
                        .build())
                .addPhenotype(OntologyClass.newBuilder()
                        .setId("HP:0030955")
                        .setLabel("Alcoholism")
                        .setNotObserved(true)
                        .build())
                .addPhenotype(OntologyClass.newBuilder()
                        .setId("HP:0002721")
                        .setLabel("Immunodeficiency")
                        .setNotObserved(false)
                        .build())
                .setDisease(Disease.newBuilder()
                        .setDatabase("OMIM")
                        .setDiseaseId("609536")
                        .setDiseaseName("COMPLEMENT COMPONENT 5 DEFICIENCY; C5D")
                        .build())
                /* Biocurator */
                .setBiocurator(Biocurator.newBuilder()
                        .setBiocuratorId("HPO:walterwhite")
                        .build())
                .setSoftwareVersion(SOFTWARE_VERSION)
                .build();
    }

    /**
     * @return {@link DiseaseCase} representing a fictional breakend. Note that the publication, disease, proband, etc.
     * are placeholders only.
     */
    static DiseaseCase beygo2012TCOF1_M18662_Translocation() {
        /* Genome build */
        return DiseaseCase.newBuilder()

                /* Publication data */
                .setPublication(beygoPublication())
                .setMetadata("Authors describe a proband M18662 with presence of TCS1")
                .setGene(geneTcof1())
                /* Variants which belong to this model */
                .addVariant(beygoBreakend())
                /* Family/proband information */
                .setFamilyInfo(beygoFamily())
                /* HPO terms */
                .addAllPhenotype(beygoPhenotype())
                .setDisease(treacherCollinsSyndrome())
                /* Biocurator */
                .setBiocurator(Biocurator.newBuilder()
                        .setBiocuratorId("HPO:walterwhite")
                        .build())
                .setSoftwareVersion(SOFTWARE_VERSION)
                .build();
    }

    /**
     * @return {@link DiseaseCase} representing a heterozygous single exon deletion causing TCS1 (autosomal dominant
     * disorder).
     */
    static DiseaseCase beygo2012TCOF1_M18662_Deletion() {
        /* Genome build */
        return DiseaseCase.newBuilder()

                /* Publication data */
                .setPublication(beygoPublication())
                .setMetadata("Authors describe a proband M18662 with presence of TCS1")
                .setGene(geneTcof1())
                /* Variants which belong to this model */
                .addVariant(beygoDeletion())
                /* Family/proband information */
                .setFamilyInfo(beygoFamily())
                /* HPO terms */
                .addAllPhenotype(beygoPhenotype())
                .setDisease(treacherCollinsSyndrome())
                /* Biocurator */
                .setBiocurator(Biocurator.newBuilder()
                        .setBiocuratorId("HPO:walterwhite")
                        .build())
                .setSoftwareVersion(SOFTWARE_VERSION)
                .build();
    }

    private static Variant beygoDeletion() {
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

    /**
     * @return fictional breakend
     */
    private static Variant beygoBreakend() {
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

    private static Disease treacherCollinsSyndrome() {
        return Disease.newBuilder()
                .setDatabase("OMIM")
                .setDiseaseId("154500")
                .setDiseaseName("TREACHER COLLINS SYNDROME 1; TCS1")
                .build();
    }

    private static FamilyInfo beygoFamily() {
        return FamilyInfo.newBuilder()
                .setFamilyOrProbandId("M18662")
                .setSex(Sex.FEMALE)
                .setAge("P26Y")
                .build();
    }

    private static Gene geneTcof1() {
        return Gene.newBuilder()
                .setSymbol("TCOF1")
                .setEntrezId(6949)
                .build();
    }

    private static Publication beygoPublication() {
        return Publication.newBuilder().setAuthorList("Beygó J, Buiting K, Seland S, Lüdecke HJ, Hehr U, Lich C, Prager B, Lohmann DR, Wieczorek D")
                .setTitle("First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome")
                .setJournal("Mol Syndromol")
                .setYear("2012")
                .setVolume("2(2)")
                .setPages("53-59")
                .setPmid("22712005")
                .build();
    }

    private static Iterable<? extends OntologyClass> beygoPhenotype() {
        List<OntologyClass> phenotype = new ArrayList<>();
        phenotype.add(OntologyClass.newBuilder()
                .setId("HP:0011453")
                .setLabel("Abnormality of the incus")
                .setNotObserved(false)
                .build());
        phenotype.add(OntologyClass.newBuilder()
                .setId("HP:0000405")
                .setLabel("Conductive hearing impairment")
                .setNotObserved(false)
                .build());
        phenotype.add(OntologyClass.newBuilder()
                .setId("HP:0025336")
                .setLabel("Delayed ability to sit")
                .setNotObserved(true)
                .build());
        phenotype.add(OntologyClass.newBuilder()
                .setId("HP:0000750")
                .setLabel("Delayed speech and language development")
                .setNotObserved(true)
                .build());

        return phenotype;
    }
}
