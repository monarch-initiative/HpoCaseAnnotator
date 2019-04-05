package org.monarchinitiative.hpo_case_annotator.model.test_resources;

import com.google.protobuf.Timestamp;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.*;

import java.time.Instant;

import static org.monarchinitiative.hpo_case_annotator.model.test_resources.PhenoPacketTestUtil.*;

class PhenopacketExample {

    /**
     * @return {@link Phenopacket} populated with some data
     */
    static Phenopacket mockPhenopacket() {
        return Phenopacket.newBuilder()
                .setId("EdPhenopacket")
                .setSubject(getSubject())
                .addPhenotypes(getCongenitalSyndactyly())
                .addPhenotypes(getNotCryptorchidism())
                .addGenes(getAbcc9Gene())
                .addVariants(getAbcc9Variant())
                .addDiseases(getHypertrichoticOsteochondrodysplasia())
                .setMetaData(getMetaData())
                .build();
    }

    private static Individual getSubject() {
        return Individual.newBuilder()
                .setId("Ed")
                .setDatasetId("TEST_DATASET")
                .setDateOfBirth(Timestamp.newBuilder()
                        .setSeconds(Instant.parse("1996-01-03T00:00:00Z").getEpochSecond())
                        .build())
                .setAgeAtCollection(Age.newBuilder().setAge("P20Y05M02D").build())
                .setSex(Sex.MALE)
                .setKaryotypicSex(KaryotypicSex.UNKNOWN_KARYOTYPE)
                .setTaxonomy(HOMO_SAPIENS)
                .build();
    }

    private static Phenotype getCongenitalSyndactyly() {
        return Phenotype.newBuilder() // severe syndactyly with congenital onset on the right side
                .setType(ontologyClass("HP:0001159", "Syndactyly"))
                .setNegated(false) // is present
                .setSeverity(ontologyClass("HP:0012828", "Severe"))
                .addModifiers(ontologyClass("HP:0012834", "Right"))
                .setClassOfOnset(ontologyClass("HP:0003577", "Congenital onset"))
                .addEvidence(Evidence.newBuilder()
                        .setEvidenceCode(TAS)
                        .setReference(ExternalReference.newBuilder()
                                .setId("EXT_REF_ID")
                                .setDescription("Some external reference description")
                                .build())
                        .build())
                .build();
    }

    private static Phenotype getNotCryptorchidism() {
        return Phenotype.newBuilder()
                .setType(ontologyClass("HP:0000028", "Cryptorchidism"))
                .setNegated(true)
                .addEvidence(Evidence.newBuilder()
                        .setEvidenceCode(TAS)
                        .setReference(ExternalReference.newBuilder()
                                .setId("EXT_REF_ID")
                                .setDescription("Some external reference description")
                                .build())
                        .build())
                .build();
    }

    private static Gene getAbcc9Gene() {
        return Gene.newBuilder()
                .setId("HGNC:10060")
                .setSymbol("ABCC9")
                .build();
    }

    private static Variant getAbcc9Variant() {
        return Variant.newBuilder()
                .setVcfAllele(VcfAllele.newBuilder()
                        .setId("hg19")
                        .setChr("12")
                        .setPos(22028588)
                        .setRef("C")
                        .setAlt("G")
                        .setInfo("DP=100")
                        .build())
                .setZygosity(HET)
                .build();
    }

    private static Disease getHypertrichoticOsteochondrodysplasia() {
        return Disease.newBuilder()
                .setTerm(ontologyClass("OMIM:239850", "Hypertrichotic Osteochondrodysplasia"))
                .setAgeOfOnset(Age.newBuilder().setAge("P00Y00M01D").build())
                .build();
    }

    private static MetaData getMetaData() {
        return MetaData.newBuilder()
                .setCreated(Timestamp.newBuilder()
                        .setSeconds(Instant.parse("2019-03-20T18:14:54Z").getEpochSecond())
                        .build())
                .setCreatedBy("Mr Fantastic")
                .setSubmittedBy("Test corp.")
                .addResources(getHPOResource())
                .addResources(getGenotypeOntologyResource())
                .addResources(getNcbiOrganismalClassificationResource())
                .addResources(getEvidenceOntologyResource())
                .build();
    }

    private static Resource getNcbiOrganismalClassificationResource() {
        return Resource.newBuilder()
                .setId("ncbitaxon")
                .setName("NCBI organismal classification")
                .setNamespacePrefix("NCBITaxon")
                .setIriPrefix("http://purl.obolibrary.org/obo/NCBITaxon_")
                .setUrl("http://purl.obolibrary.org/obo/ncbitaxon.owl")
                .setVersion("19-03-2019")
                .build();
    }

    private static Resource getEvidenceOntologyResource() {
        return Resource.newBuilder()
                .setId("eco")
                .setName("Evidence ontology")
                .setNamespacePrefix("ECO")
                .setIriPrefix("http://purl.obolibrary.org/obo/ECO_")
                .setUrl("http://purl.obolibrary.org/obo/eco.owl")
                .setVersion("19-03-2019")
                .build();
    }

    private static Resource getGenotypeOntologyResource() {
        return Resource.newBuilder()
                .setId("geno")
                .setName("Genotype Ontology")
                .setNamespacePrefix("GENO")
                .setIriPrefix("http://purl.obolibrary.org/obo/GENO_")
                .setUrl("http://purl.obolibrary.org/obo/geno.owl")
                .setVersion("19-03-2018")
                .build();
    }

    private static Resource getHPOResource() {
        return Resource.newBuilder()
                .setId("hp")
                .setName("human phenotype ontology")
                .setNamespacePrefix("HP")
                .setIriPrefix("http://purl.obolibrary.org/obo/HP_")
                .setUrl("http://purl.obolibrary.org/obo/hp.owl")
                .setVersion("2018-03-08")
                .build();
    }

}
