package org.monarchinitiative.hpocaseannotator.core.io.proto;


import com.google.protobuf.Timestamp;
import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpocaseannotator.core.io.MockCaseModel;
import org.monarchinitiative.hpocaseannotator.core.model.proto.*;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Here we test serialization and deserialization
 */
public class ProtoCaseModelCodecTest {

    private ProtoCaseModelCodec codec;


    /**
     * Read whole content of given file.
     *
     * @param file {@link File} pointing to the local filepath
     * @return String with the file content
     * @throws IOException bla
     */
    private static String readFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }


    @Before
    public void setUp() throws Exception {
        codec = new ProtoCaseModelCodec();
    }


    /**
     * Read JSON file <code>/CaseModel_1.json</code> containing test model and check deserialization.
     *
     * @throws IOException bla
     */
    @Test
    public void decode() throws IOException {
        CaseModel model = codec.decode(getClass().getResourceAsStream("/CaseModel_1.json"));

        // model is not stamped, since we want it to be reproducible
        Timestamp stamp = model.getUpdated();
        assertThat(stamp.getSeconds(), is(0L));
        assertThat(stamp.getNanos(), is(0));

        Proband proband = model.getProband();
        assertThat(proband.getFamilyOrPatientId(), is("proband1"));
        assertThat(proband.getSex(), is(Proband.Sex.MALE));
        assertThat(proband.getAge(), is(32));

        List<Disease> diseases = proband.getDiseasesList();
        assertThat(diseases.size(), is(1));
        Disease firstDisease = diseases.get(0);
        assertThat(firstDisease.getId(), is("MIM:219700"));
        assertThat(firstDisease.getLabel(), is("CYSTIC FIBROSIS; CF"));

        List<Phenotype> phenotypes = proband.getPhenotypesList();
        assertThat(phenotypes.size(), is(2));
        Phenotype firstPhenotype = phenotypes.get(0);
        assertThat(firstPhenotype.getNegated(), is(false));
        OntologyClass firstType = firstPhenotype.getType();
        assertThat(firstType.getId(), is("HP:0003074"));
        assertThat(firstType.getLabel(), is("Hyperglycemia"));

        Phenotype secondPhenotype = phenotypes.get(1);
        assertThat(secondPhenotype.getNegated(), is(true));
        OntologyClass secondType = secondPhenotype.getType();
        assertThat(secondType.getId(), is("HP:0000822"));
        assertThat(secondType.getLabel(), is("Hypertension"));

        Gene gene = model.getGene();
        assertThat(gene.getId(), is("1080"));
        assertThat(gene.getSymbol(), is("CFTR"));

        Publication publication = model.getPublication();
        assertThat(publication.getAuthorList(), is("Aznarez I, Chan EM, Zielenski J, Blencowe BJ, Tsui LC"));
        assertThat(publication.getTitle(), is("Characterization of disease-associated mutations affecting an exonic splicing enhancer and two cryptic splice sites in exon 13 of the cystic fibrosis transmembrane conductance regulator gene"));
        assertThat(publication.getJournal(), is("Hum Mol Genet"));
        assertThat(publication.getYear(), is(2003));
        assertThat(publication.getVolume(), is("12(16)"));
        assertThat(publication.getPages(), is("2031-40"));
        assertThat(publication.getPmid(), is("12913074"));

        List<Variant> variants = model.getVariantsList();
        assertThat(variants.size(), is(2));
        Variant firstVar = variants.get(0);
        assertThat(firstVar.getGenomeAssembly(), is(GenomeAssembly.HG_19));
        assertThat(firstVar.getContig(), is("1"));
        assertThat(firstVar.getPosition(), is(1234));
        assertThat(firstVar.getDeletion(), is("A"));
        assertThat(firstVar.getInsertion(), is("G"));
        assertThat(firstVar.getGenotype(), is(Genotype.HET));
        assertThat(firstVar.getVariantClass(), is(VariantClass.SPLICE));
        VariantEvidence firstEvidence = firstVar.getVariantEvidence();
        assertThat(firstEvidence.getType(), is(VariantEvidenceType.SPLICING));
        assertThat(firstEvidence.getCosegregation(), is(true));
        assertThat(firstEvidence.getMinigeneValidation(), is(true));
        assertThat(firstEvidence.getCDnaSequencingValidation(), is(true));

        Variant secondVar = variants.get(1);
        assertThat(secondVar.getGenomeAssembly(), is(GenomeAssembly.HG_19));
        assertThat(secondVar.getContig(), is("2"));
        assertThat(secondVar.getPosition(), is(3456));
        assertThat(secondVar.getDeletion(), is("A"));
        assertThat(secondVar.getInsertion(), is("C"));
        assertThat(secondVar.getGenotype(), is(Genotype.HOM_ALT));
        assertThat(secondVar.getVariantClass(), is(VariantClass.CODING));
        VariantEvidence secondEvidence = secondVar.getVariantEvidence();
        assertThat(secondEvidence.getType(), is(VariantEvidenceType.MENDELIAN));
        assertThat(secondEvidence.getComparability(), is(true));
        assertThat(secondEvidence.getReporterRegulation(), is("up"));
        assertThat(secondEvidence.getEmsaValidationPerformed(), is(true));
        assertThat(secondEvidence.getEmsaTfSymbol(), is("HNF4A"));
        assertThat(secondEvidence.getEmsaGeneId(), is("3223"));

        Metadata metadata = model.getMetadata();
        assertThat(metadata.getMetadataContent(), is("Authors describe patient suffering from ..."));

        Biocurator biocurator = model.getBiocurator();
        assertThat(biocurator.getBiocuratorId(), is("HPO:cnorris"));
        assertThat(biocurator.getEmail(), is("chuck_norris@hpo.jax.org"));
    }


    /**
     * Test serialization to JSON. Mock Case model is retrieved from {@link MockCaseModel}, after serialization the
     * content of generated JSON string is expected to match content of the file <em>/CaseModel_1.json</em>.
     * Timestamp is not tested.
     *
     * @throws IOException bla
     */
    @Test
    public void encode() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        codec.encode(MockCaseModel.getMockCaseModel(), os, false);
        String expected = readFile(new File(getClass().getResource("/CaseModel_1.json").getFile()));
        String actual = os.toString();
        assertThat(expected, is(actual));
    }
}