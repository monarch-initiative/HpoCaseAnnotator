package org.monarchinitiative.hpo_case_annotator.io.test_resources;

import org.monarchinitiative.hpo_case_annotator.model.proto.*;

import java.util.ArrayList;
import java.util.List;

import static org.monarchinitiative.hpo_case_annotator.io.test_resources.TestResources.SOFTWARE_VERSION;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
class DiseaseCaseModelExample {

    // TODO - simplify

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
