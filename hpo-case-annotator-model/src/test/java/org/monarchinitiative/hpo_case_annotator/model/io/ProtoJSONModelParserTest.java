package org.monarchinitiative.hpo_case_annotator.model.io;

import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ProtoJSONModelParserTest {


    /**
     * This test demonstrates that the parser is also able to read data from deprecated variant fields. For the purpose
     * of testing <em>v1</em> denotes a model where there is an information stored in the deprecated fields and <em>v2</em>
     * denotes a model with no information stored in the deprecated fields.
     * <p>
     * This test should be removed when the deprecated fields are removed from the model.
     *
     * @throws Exception blabla
     */
    @Test
    public void v1ModelFormatIsParsedCorrectly() throws Exception {

        Optional<DiseaseCase> diseaseCaseOptional;
        try (InputStream is = getClass().getResourceAsStream("test-model-v1-Aznarez-2003-CFTR.json")) {
            diseaseCaseOptional = ProtoJSONModelParser.readDiseaseCase(is);
        }

        assertThat(diseaseCaseOptional.isPresent(), is(true));
        final DiseaseCase diseaseCase = diseaseCaseOptional.get();

        assertThat(diseaseCase.getGenomeBuild(), is("GRCh37"));
        // publication
        Publication expectedPublication = Publication.newBuilder()
                .setAuthorList("Aznarez I, Chan EM, Zielenski J, Blencowe BJ, Tsui LC").setTitle("Characterization of disease-associated mutations affecting an exonic splicing enhancer and two cryptic splice sites in exon 13 of the cystic fibrosis transmembrane conductance regulator gene")
                .setJournal("Hum Mol Genet").setYear("2003")
                .setVolume("12(16)").setPages("2031-40")
                .setPmid("12913074").build();
        assertThat(diseaseCase.getPublication(), is(expectedPublication));
        // metadata
        assertThat(diseaseCase.getMetadata(), is("Authors are describing a mutations in CFTR exon 13 that appears to contain two 3'CSS utilization of which is increased when there is a mutation in ESE element present in exon 13 (Figure 2.).\n\nThe 3'CSS whose coordinates are recorded in variants is the dominant one (Figure 2. D, D248). However, there exists also another (D195) which has coordinates: 117232182, 3 splice site, CAATTTAG|TGCAGAAA ."));
        // gene
        Gene expectedGene = Gene.newBuilder()
                .setEntrezId(1080)
                .setSymbol("CFTR")
                .build();
        assertThat(diseaseCase.getGene(), is(expectedGene));
        // disease
        Disease expectedDisease = Disease.newBuilder()
                .setDatabase("OMIM")
                .setDiseaseId("219700")
                .setDiseaseName("CYSTIC FIBROSIS; CF")
                .build();
        assertThat(diseaseCase.getDisease(), is(expectedDisease));
        // phenotype
        assertThat(diseaseCase.getPhenotypeCount(), is(0));
        // family_info
        assertThat(diseaseCase.getFamilyInfo(), is(FamilyInfo.getDefaultInstance()));
        // biocurator
        assertThat(diseaseCase.getBiocurator(), is(Biocurator.newBuilder().setBiocuratorId("HPO:ddanis").build()));
        // variants
        // the first variant
        assertThat(diseaseCase.getVariantList(), hasItem(Variant.newBuilder()
                .setContig("7")
                .setPos(117232187)
                .setRefAllele("G")
                .setAltAllele("T")
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
                .build()));
        // the second variant
        assertThat(diseaseCase.getVariantList(), hasItem(Variant.newBuilder()
                .setContig("7")
                .setPos(117232196)
                .setRefAllele("AA")
                .setAltAllele("A")
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
                .build()));
    }


    /**
     * <em>v2</em> model format is the format where no information is stored in the deprecated fields.
     * This is the same test as {@link #v1ModelFormatIsParsedCorrectly()}, however no deprecated field is tested
     *
     * @throws Exception blabla
     */
    @Test
    public void v2ModelFormatIsParsedCorrectly() throws Exception {
        Optional<DiseaseCase> diseaseCaseOptional;
        try (InputStream is = getClass().getResourceAsStream("test-model-v2-Aznarez-2003-CFTR.json")) {
            diseaseCaseOptional = ProtoJSONModelParser.readDiseaseCase(is);
        }

        assertTrue(diseaseCaseOptional.isPresent());
        final DiseaseCase diseaseCase = diseaseCaseOptional.get();

        // publication
        Publication expectedPublication = Publication.newBuilder()
                .setAuthorList("Aznarez I, Chan EM, Zielenski J, Blencowe BJ, Tsui LC").setTitle("Characterization of disease-associated mutations affecting an exonic splicing enhancer and two cryptic splice sites in exon 13 of the cystic fibrosis transmembrane conductance regulator gene")
                .setJournal("Hum Mol Genet").setYear("2003")
                .setVolume("12(16)").setPages("2031-40")
                .setPmid("12913074").build();
        assertThat(diseaseCase.getPublication(), is(expectedPublication));
        // metadata
        assertThat(diseaseCase.getMetadata(), is("Authors are describing a mutations in CFTR exon 13 that appears to contain two 3'CSS utilization of which is increased when there is a mutation in ESE element present in exon 13 (Figure 2.).\n\nThe 3'CSS whose coordinates are recorded in variants is the dominant one (Figure 2. D, D248). However, there exists also another (D195) which has coordinates: 117232182, 3 splice site, CAATTTAG|TGCAGAAA ."));
        // gene
        Gene expectedGene = Gene.newBuilder()
                .setEntrezId(1080)
                .setSymbol("CFTR")
                .build();
        assertThat(diseaseCase.getGene(), is(expectedGene));
        // disease
        Disease expectedDisease = Disease.newBuilder()
                .setDatabase("OMIM")
                .setDiseaseId("219700")
                .setDiseaseName("CYSTIC FIBROSIS; CF")
                .build();
        assertThat(diseaseCase.getDisease(), is(expectedDisease));
        // phenotype
        assertThat(diseaseCase.getPhenotypeCount(), is(0));
        // family_info
        assertThat(diseaseCase.getFamilyInfo(), is(FamilyInfo.getDefaultInstance()));
        // biocurator
        assertThat(diseaseCase.getBiocurator(), is(Biocurator.newBuilder().setBiocuratorId("HPO:ddanis").build()));
        // variants
        // the first variant
        assertThat(diseaseCase.getVariantList(), hasItem(Variant.newBuilder()
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
                .build()));
        // the second variant
        assertThat(diseaseCase.getVariantList(), hasItem(Variant.newBuilder()
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
                .build()));
    }

    /**
     * We want the model to be saved in the <em>v2</em> format - genome build model field is not used and variant
     * coordinates are stored as VariantPosition object.
     * <p>
     * The file <code>expected-v2-Aznarez-2003-CFTR.json</code> contains expected JSON content.
     *
     * @throws Exception bla
     */
    @Test
    public void modelIsSavedCorrectlyAsV2() throws Exception {
        String expected;
        Path expectedContentPath = Paths.get(getClass().getResource("test-model-v2-Aznarez-2003-CFTR.json").toURI());
        try (BufferedReader reader = Files.newBufferedReader(expectedContentPath)) {
            expected = reader.lines().collect(Collectors.joining("\n"));
        }

        Optional<DiseaseCase> diseaseCaseOptional;
        try (InputStream is = getClass().getResourceAsStream("test-model-v1-Aznarez-2003-CFTR.json")) {
            diseaseCaseOptional = ProtoJSONModelParser.readDiseaseCase(is);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DiseaseCase diseaseCase = diseaseCaseOptional.get();
        ProtoJSONModelParser.saveDiseaseCase(baos, diseaseCase, Charset.forName("UTF-8"));

        assertThat(expected, is(baos.toString()));
    }
}