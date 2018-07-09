package org.monarchinitiative.hpocaseannotator.core.io;

import org.monarchinitiative.hpocaseannotator.core.model.proto.*;

import java.util.Arrays;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public class MockCaseModel {

    private MockCaseModel() {
    }


    public static CaseModel getMockCaseModel() {
        return CaseModel.newBuilder()
                .setProband(Proband.newBuilder()
                        .setFamilyOrPatientId("proband1")
                        .setSex(Proband.Sex.MALE)
                        .setAge(32)
                        .addDiseases(0, Disease.newBuilder()
                                .setId("MIM:219700")
                                .setLabel("CYSTIC FIBROSIS; CF")
                                .build())
                        .addAllPhenotypes(Arrays.asList(
                                Phenotype.newBuilder()
                                        .setType(OntologyClass.newBuilder()
                                                .setId("HP:0003074")
                                                .setLabel("Hyperglycemia")
                                                .build())
                                        .setNegated(false)
                                        .build(),
                                Phenotype.newBuilder()
                                        .setType(OntologyClass.newBuilder()
                                                .setId("HP:0000822")
                                                .setLabel("Hypertension")
                                                .build())
                                        .setNegated(true)
                                        .build()))
                        .build())
                .setGene(Gene.newBuilder().setId("1080").setSymbol("CFTR").build())
                .setPublication(Publication.newBuilder()
                        .setAuthorList("Aznarez I, Chan EM, Zielenski J, Blencowe BJ, Tsui LC")
                        .setTitle("Characterization of disease-associated mutations affecting an exonic splicing enhancer and two " +
                                "cryptic splice sites in exon 13 of the cystic fibrosis transmembrane conductance regulator gene")
                        .setJournal("Hum Mol Genet")
                        .setYear(2003)
                        .setVolume("12(16)")
                        .setPages("2031-40")
                        .setPmid("12913074")
                        .build())
                .addAllVariants(Arrays.asList(
                        Variant.newBuilder()
                                .setGenomeAssembly(GenomeAssembly.HG_19)
                                .setContig("1")
                                .setPosition(1234)
                                .setDeletion("A")
                                .setInsertion("G")
                                .setGenotype(Genotype.HET)
                                .setVariantClass(VariantClass.SPLICE)
                                .setVariantEvidence(VariantEvidence.newBuilder()
                                        .setType(VariantEvidenceType.SPLICING)
                                        .setCDnaSequencingValidation(true)
                                        .setCosegregation(true)
                                        .setMinigeneValidation(true)
                                        .build())
                                .build(),
                        Variant.newBuilder()
                                .setGenomeAssembly(GenomeAssembly.HG_19)
                                .setContig("2")
                                .setPosition(3456)
                                .setDeletion("A")
                                .setInsertion("C")
                                .setGenotype(Genotype.HOM_ALT)
                                .setVariantClass(VariantClass.CODING)
                                .setVariantEvidence(VariantEvidence.newBuilder()
                                        .setType(VariantEvidenceType.MENDELIAN)
                                        .setCosegregation(false)
                                        .setComparability(true)
                                        .setEmsaValidationPerformed(true)
                                        .setEmsaTfSymbol("HNF4A")
                                        .setEmsaGeneId("3223")
                                        .setReporterRegulation("up")
                                        .build())
                                .build()
                ))
                .setMetadata(Metadata.newBuilder()
                        .setMetadataContent("Authors describe patient suffering from ...")
                        .build())
                .setBiocurator(Biocurator.newBuilder()
                        .setBiocuratorId("HPO:cnorris")
                        .setEmail("chuck_norris@hpo.jax.org")
                        .build())
                .build();
    }

}
