package org.monarchinitiative.hpo_case_annotator.gui;

import org.monarchinitiative.hpo_case_annotator.model.proto.*;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class PojosForTesting {

    // TODO - add other models for testing here


    public static DiseaseCase benMahmoud2013B3GLCT() {
        return DiseaseCase.newBuilder()
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
                .addVariant(makeSplicingVariant())
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
     * @return {@link Variant} with {@link VariantValidation.Context#SPLICING} context. The data is not a real-life
     * example, just for testing (i.e. all validation methods are set to <code>true</code>)
     */
    public static Variant makeSplicingVariant() {
        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
                        .setContig("13")
                        .setPos(31843349)
                        .setRefAllele("A")
                        .setAltAllele("G")
                        .build())
                .setSnippet("TTTCT[A/G]GGCTT")
                .setGenotype(Genotype.HETEROZYGOUS)
                .setVariantClass("splicing")
                .setPathomechanism("splicing|3ss|disrupted")
                .setConsequence("Exon skipping")
                .setCrypticPosition(31843347)
                .setCrypticSpliceSiteType(CrypticSpliceSiteType.FIVE_PRIME)
                .setCrypticSpliceSiteSnippet("CTTAAAC|gtacca")
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.SPLICING)
                        .setCosegregation(true)
                        .setComparability(true)
                        .setMinigeneValidation(true)
                        .setSiteDirectedMutagenesisValidation(true)
                        .setRtPcrValidation(true)
                        .setSrProteinOverexpressionValidation(true)
                        .setSrProteinKnockdownValidation(true)
                        .setCDnaSequencingValidation(true)
                        .setPcrValidation(true)
                        .setMutOfWtSpliceSiteValidation(true)
                        .setOtherValidation(true)
                        .build())
                .build();
    }

    /**
     * @return {@link Variant} with {@link VariantValidation.Context#MENDELIAN} context. The data is not a real-life
     * example, just for testing.
     */
    public static Variant makeMendelianVariant() {
        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
                        .setContig("18")
                        .setPos(61030043)
                        .setRefAllele("CCTT")
                        .setAltAllele("C")
                        .build())
                .setSnippet("ATAAAAGCT[CCTT/C]GTTTATAG")
                .setGenotype(Genotype.HETEROZYGOUS)
                .setVariantClass("coding")
                .setPathomechanism("coding|missense")
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.MENDELIAN)
                        .setCosegregation(true)
                        .setComparability(true)
                        .setRegulator("regulator")
                        .setReporterRegulation("up")
                        .setReporterResidualActivity("12.3")
                        .setEmsaValidationPerformed(true)
                        .setEmsaTfSymbol("EMSA_TF_SYMBOL")
                        .setEmsaGeneId("EMSA_GENE_ID")
                        .setOtherChoices("down")
                        .setOtherEffect("Other effect")
                        .build())
                .build();
    }

    /**
     * @return {@link Variant} with {@link VariantValidation.Context#SOMATIC} context. The data is not a real-life
     * example, just for testing.
     */
    public static Variant makeSomaticVariant() {
        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
                        .setContig("18")
                        .setPos(61030043)
                        .setRefAllele("CCTT")
                        .setAltAllele("C")
                        .build())
                .setSnippet("ATAAAAGCT[CCTT/C]GTTTATAG")
                .setGenotype(Genotype.HETEROZYGOUS)
                .setVariantClass("coding")
                .setPathomechanism("coding|missense")
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.SOMATIC)
                        .setRegulator("regulator")
                        .setReporterRegulation("up")
                        .setReporterResidualActivity("12.3")
                        .setEmsaValidationPerformed(true)
                        .setEmsaTfSymbol("EMSA_TF_SYMBOL")
                        .setEmsaGeneId("EMSA_GENE_ID")
                        .setNPatients(20)
                        .setMPatients(30)
                        .setOtherChoices("down")
                        .setOtherEffect("Other effect")
                        .build())
                .build();
    }

    public static Variant makeStructuralDeletionVariant() {
        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
                        .setContig("9")
                        .setPos(10_000_000)
                        .setPos2(10_001_000)
                        .setRefAllele("N")
                        .setAltAllele("DEL")
                        .setCiBeginOne(-50)
                        .setCiBeginTwo(50)
                        .setCiEndOne(-100)
                        .setCiEndTwo(100)
                        .build())

                .setVariantClass("structural")
                .setGenotype(Genotype.HETEROZYGOUS)
                .setSvType(StructuralType.DEL)
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.INTRACHROMOSOMAL)
                        .setCosegregation(true)
                        .build())
                .build();
    }

    /**
     * @return example variant that represents an imprecise reciprocal translocation occuring at
     */
    public static Variant makeBreakendVariant() {
        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
                        .setContig("9")
                        .setPos(10_000_001)
                        .setContigDirection(VariantPosition.Direction.FWD)
                        .setContig2("11")
                        .setPos2(20_000_001)
                        .setContig2Direction(VariantPosition.Direction.REV)
                        .setRefAllele("N")
                        .setAltAllele("ACGT")
                        .setCiBeginOne(-50)
                        .setCiBeginTwo(50)
                        .setCiEndOne(-100)
                        .setCiEndTwo(100)
                        .build())
                .setVariantClass("structural")
                .setGenotype(Genotype.HETEROZYGOUS)
                .setSvType(StructuralType.BND)
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.TRANSLOCATION)
                        .build())
                .setImprecise(true)
                .build();

    }

    public static Variant makeCnvDuplicationVariant() {
        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
                        .setContig("9")
                        .setPos(10_000_000)
                        .setPos2(10_001_000)
                        .setRefAllele("N")
                        .setAltAllele("<DUP>")
                        .setCiBeginOne(-50)
                        .setCiBeginTwo(50)
                        .setCiEndOne(-100)
                        .setCiEndTwo(100)
                        .build())

                .setVariantClass("structural")
                .setGenotype(Genotype.HETEROZYGOUS)
                .setSvType(StructuralType.DUP)
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.INTRACHROMOSOMAL)
                        .setCosegregation(true)
                        .build())
                .build();
    }

    public static Publication makeExomiserPublication() {
        return Publication.newBuilder()
                .setAuthorList("Smedley D, Jacobsen JO, Jäger M, Köhler S, Holtgrewe M, Schubach M, Siragusa E, Zemojtel T, Buske OJ, Washington NL, Bone WP, Haendel MA, Robinson PN")
                .setTitle("Next-generation diagnostics and disease-gene discovery with the Exomiser")
                .setJournal("Nat Protoc")
                .setPages("2004-15")
                .setPmid("26562621")
                .setVolume("10(12)")
                .setYear("2015")
                .build();
    }

    public static Publication makeJannovarPublication() {
        return Publication.newBuilder()
                .setAuthorList("Jäger M, Wang K, Bauer S, Smedley D, Krawitz P, Robinson PN")
                .setTitle("Jannovar: a java library for exome annotation")
                .setJournal("Hum Mutat")
                .setPages("548-55")
                .setPmid("24677618")
                .setVolume("35(5)")
                .setYear("2014")
                .build();
    }
}
