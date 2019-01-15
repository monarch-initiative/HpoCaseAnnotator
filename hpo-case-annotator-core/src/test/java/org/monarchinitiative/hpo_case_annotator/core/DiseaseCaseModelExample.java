package org.monarchinitiative.hpo_case_annotator.core;

import org.monarchinitiative.hpo_case_annotator.model.proto.*;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class DiseaseCaseModelExample {

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
                .addVariant(makeVariantWithSplicingValidation())
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

    public static Variant makeVariantWithSplicingValidation() {
        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
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
                .build();
    }
}
