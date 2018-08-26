package org.monarchinitiative.hpo_case_annotator.io;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.Assert.assertThat;


/**
 * This test suite tests functionality of {@link FirstModelXMLParser}. The parser uses
 * {@link org.monarchinitiative.hpo_case_annotator.first_model.MutationReader} to read the XML data. The
 * {@link org.monarchinitiative.hpo_case_annotator.first_model.MutationReader} class uses SAX parser and DTD file to
 * parse XML content.
 * <p>
 * Since I am not familiar with SAX logic, I am using a little hack here in the {@link BeforeClass} and {@link AfterClass}
 * methods of this suite. SAX parser works after setting <code>user.dir</code> property to directory where the DTD files
 * are located.
 *
 * Two {@link VariantMode#MENDELIAN} cases are checked ({@link #readMendelianModelFu()}  and {@link #readMendelianModelDamjanovich()}).
 * Then, two {@link VariantMode#SOMATIC} cases are checked.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 1.0.6
 * @since 1.0
 */
public class FirstModelXMLParserTest {

    private static final File MENDELIAN_MODEL_DIR = new File(FirstModelXMLParserTest.class.getResource("/models/first/mendelian").getFile());

    private static final File SOMATIC_MODEL_DIR = new File(FirstModelXMLParserTest.class.getResource("/models/first/somatic").getFile());

    private static String USER_DIR;

    private FirstModelXMLParser mendelian, somatic;


    /**
     * Change <code>user.dir</code> system property to make sure
     *
     */
    @BeforeClass
    public static void setUpBefore() {
        USER_DIR = System.getProperty("user.dir");
        File altUserDir = new File(new File(USER_DIR), "target" + File.separator +
                "test-classes" + File.separator + "dtd");
        System.setProperty("user.dir", altUserDir.getAbsolutePath());
    }


    @AfterClass
    public static void tearDownAfter() throws Exception {
        System.setProperty("user.dir", USER_DIR);
    }


    @Before
    public void setUp() throws Exception {
        mendelian = new FirstModelXMLParser(MENDELIAN_MODEL_DIR);
        somatic = new FirstModelXMLParser(SOMATIC_MODEL_DIR);
    }


    /**
     * We test single model <code>Damjanovich-ENG-2011-1.xml</code>.
     */
    @Test
    public void readMendelianModelDamjanovich() throws Exception {
        DiseaseCaseModel model = mendelian.readModel(
                new FileInputStream(new File(MENDELIAN_MODEL_DIR, "Damjanovich-ENG-2011-1.xml")));

        Publication pub = model.getPublication();
        assertThat(pub, hasProperty("authorlist", is("Damjanovich K, Langa C, Blanco FJ, McDonald J, Botella LM, Bernabeu C, Wooderchak-Donahue W, Stevenson DA, Bayrak-Toydemir P")));
        assertThat(pub, hasProperty("title", is("5'UTR mutations of ENG cause hereditary hemorrhagic telangiectasia")));
        assertThat(pub, hasProperty("journal", is("Orphanet J Rare Dis")));
        assertThat(pub, hasProperty("year", is("2011")));
        assertThat(pub, hasProperty("volume", is("6")));
        assertThat(pub, hasProperty("pages", is("85")));
        assertThat(pub, hasProperty("pmid", is("22192717")));

        Metadata metadata = model.getMetadata();
        assertThat(metadata, hasProperty("metadataText", is("Hereditary hemorrhagic telangiectasia " +
                "(HHT) is a vascular disorder characterized by epistaxis, arteriovenous malformations, and " +
                "telangiectases. Mutations in ACVRL1 or ENG can cause HHT. In this report, a 5' UTR " +
                "mutation in ENG is described. A c.-127C>T heterozygous change was found in three out of " +
                "the 154 clinical HHT cases and in one case from Northern Spain (family 4). Two of the " +
                "siblings met clinical diagnostic criteria with frequent epistaxis, typical telagiectasia, " +
                "PAVMs, and gastrointestinal (GI) telangiectasia. This sequence alteration creates a potential " +
                "AUG initiation codon at base -127 from translation initiation of the ENG gene. the sequence " +
                "surrounding the new TIS fits well with the Kozak consensus the levels of endoglin protein expression " +
                "were assessed by transient transfection in the monkey cell line COS-7. The protein expression levels " +
                "of the mutant endoglin construct c.-127C>T were markedly reduced (74%) with respect to the wild type " +
                "construct. The mutation corresponds to NM_001114753.2:c.-127C>T. ENG is on the minus strand of " +
                "chromosome 9, and the chromosomal position of the mutation is chr9:130,616,761G>A (GRCh37).")));

        TargetGene targetGene = model.getTargetGene();
        assertThat(targetGene, hasProperty("entrezID", is("2022")));
        assertThat(targetGene, hasProperty("geneName", is("ENG")));

        List<Variant> variants = model.getVariants();
        assertThat(variants.size(), is(1));
        Variant first = variants.get(0);
        assertThat(first.getVariantMode(), is(VariantMode.MENDELIAN));
        assertThat(first, hasProperty("chromosome", is("9")));
        assertThat(first, hasProperty("position", is("130616761")));
        assertThat(first, hasProperty("referenceAllele", is("G")));
        assertThat(first, hasProperty("alternateAllele", is("A")));
        assertThat(first, hasProperty("snippet", is("ACGGC[G/A]TCCCTGC")));
        assertThat(first, hasProperty("genotype", is("heterozygous")));
        assertThat(first, hasProperty("variantClass", is("5UTR")));
        assertThat(first, hasProperty("pathomechanism", is("5UTR|uORF|reduced-translation|novel-uORF")));
        assertThat(first, hasProperty("cosegregation", is("true")));
        assertThat(first, hasProperty("comparability", is("false")));

        MendelianVariant mvar = ((MendelianVariant) first);
        assertThat(mvar, hasProperty("regulator", is("ENG 5'UTR")));
        assertThat(mvar, hasProperty("other", is("down")));
        assertThat(mvar, hasProperty("otherEffect", is("level of recombinant mutant protein")));

        FamilyInfo familyInfo = model.getFamilyInfo();
        assertThat(familyInfo, hasProperty("familyOrPatientID", is("Families 1-4")));

        List<HPO> hpos = model.getHpoList();
        assertThat(hpos.size(), is(3));
        assertThat(hpos, hasItem(new HPO("HP:0002604", "Gastrointestinal telangiectasia", "YES")));
        assertThat(hpos, hasItem(new HPO("HP:0006548", "Pulmonary arteriovenous malformation", "YES")));
        assertThat(hpos, hasItem(new HPO("HP:0004406", "Spontaneous, recurrent epistaxis", "YES")));

        Disease disease = model.getDisease();
        assertThat(disease, hasProperty("database", is("OMIM")));
        assertThat(disease, hasProperty("diseaseId", is("187300")));
        assertThat(disease, hasProperty("diseaseName", is("Telangiectasia, hereditary hemorrhagic, type 1")));

        Biocurator biocurator = model.getBiocurator();
        assertThat(biocurator, hasProperty("bioCuratorId", is("HPO:probinson")));
    }

    /**
     * We test single model <code>Fu-GRHPR-2014.xml</code> here.
     */
    @Test
    public void readMendelianModelFu() throws Exception {
        DiseaseCaseModel model = mendelian.readModel(
                new FileInputStream(new File(MENDELIAN_MODEL_DIR, "Fu-GRHPR-2014.xml")));

        Publication pub = model.getPublication();
        assertThat(pub, hasProperty("authorlist", is("Fu Y, Rope R, Fargue S, Cohen HT, Holmes RP, Cohen DM")));
        assertThat(pub, hasProperty("title", is("A mutation creating an out-of-frame alternative translation initiation site in the GRHPR 5'UTR causing primary hyperoxaluria type II")));
        assertThat(pub, hasProperty("journal", is("Clin Genet")));
        assertThat(pub, hasProperty("year", is("2014")));
        assertThat(pub, hasProperty("volume", is("[Epub ahead of print]")));
        assertThat(pub, hasProperty("pages", is("doi: 10.1111/cge.12541")));
        assertThat(pub, hasProperty("pmid", is("25410531")));

        List<Variant> variants = model.getVariants();
        assertThat(variants.size(), is(2));
        Variant first = variants.get(0);
        assertThat(first.getVariantMode(), is(VariantMode.MENDELIAN));
        assertThat(model.getGenomeBuild(), is("37"));
        assertThat(first, hasProperty("chromosome", is("9")));
        assertThat(first, hasProperty("position", is("37422743")));
        assertThat(first, hasProperty("referenceAllele", is("TGC")));
        assertThat(first, hasProperty("alternateAllele", is("TAT")));
        assertThat(first, hasProperty("snippet", is("GCACT[GC/AT]GGATG")));
        assertThat(first, hasProperty("genotype", is("heterozygous")));
        assertThat(first, hasProperty("variantClass", is("5UTR")));
        assertThat(first, hasProperty("pathomechanism", is("5UTR|uORF|reduced-translation|novel-uORF")));
        assertThat(first, hasProperty("cosegregation", is("false")));
        assertThat(first, hasProperty("comparability", is("false")));

        MendelianVariant mvar = ((MendelianVariant) first);
        assertThat(mvar, hasProperty("regulator", is("GRHPR 5'UTR")));
        assertThat(mvar, hasProperty("reporterRegulation", is("down")));
        assertThat(mvar, hasProperty("reporterResidualActivity", is("0.0")));

        Variant second = variants.get(1);
        assertThat(second.getVariantMode(), is(VariantMode.MENDELIAN));
        assertThat(model.getGenomeBuild(), is("37"));
        assertThat(second, hasProperty("chromosome", is("9")));
        assertThat(second, hasProperty("position", is("37430601")));
        assertThat(second, hasProperty("referenceAllele", is("TC")));
        assertThat(second, hasProperty("alternateAllele", is("T")));
        assertThat(second, hasProperty("snippet", is("TCTT[C/-]CAGAAGA")));
        assertThat(second, hasProperty("genotype", is("heterozygous")));
        assertThat(second, hasProperty("variantClass", is("coding")));
        assertThat(second, hasProperty("pathomechanism", is("coding")));
        assertThat(second, hasProperty("cosegregation", is("true")));
        assertThat(second, hasProperty("comparability", is("true")));

        Disease disease = model.getDisease();
        assertThat(disease, hasProperty("database", is("OMIM")));
        assertThat(disease, hasProperty("diseaseId", is("260000")));
        assertThat(disease, hasProperty("diseaseName", is("Hyperoxaluria, primary, type II")));

        List<HPO> hpos = model.getHpoList();
        assertThat(hpos.size(), is(2));
        assertThat(hpos, hasItem(new HPO("HP:0008672", "Calcium oxalate nephrolithiasis", "YES")));
        assertThat(hpos, hasItem(new HPO("HP:0012622", "Chronic kidney disease", "YES")));

        FamilyInfo familyInfo = model.getFamilyInfo();
        // no identifier in the XML file
        assertThat(familyInfo, hasProperty("familyOrPatientID", is("")));

        Metadata metadata = model.getMetadata();
        assertThat(metadata, hasProperty("metadataText", is("Primary hyperoxaluria type II is a " +
                "recessive genetic disorder caused by mutations in the GRHPR gene. Here, a compound heterozygos patient " +
                "is reported. The first mutation was a frameshift mutation, c.694delC, i.e., NM_012203.1:c.694delC, " +
                "p.(Gln232Argfs*3), corresponding to chr9:37,430,603delC (GRCh37; the GRHPR gene is located on the plus " +
                "strand of chromosome 9). The other variant is located in the 5' UTR of GRHPR, NM_012203.1:c.-4_-3delGCinsAT. " +
                "This allele is predicted to direct pre-mature out-of-frame translational initiation and synthesis of a " +
                "20-amino acid peptide (MDETGATHEGVRHPQDTRRG) bearing no relation to GRHPR. An SV40-driven luciferase " +
                "reporter downstream of the variant 41-bp GRHPR 5 ′ UTR exhibited marked hypoactivity relative to the " +
                "wild-type 5'UTR. This confirmed that, in the presence of the upstream out-of-frame translational start " +
                "site, negligible translation occurred from the more distal native initiation site, and probably giving " +
                "rise to a profoundly hypomorphic allele in vivo. Therefore, the novel dinucleotide 5 ′ UTR variant, " +
                "through introduction of a high-efficiency out-of-frame alternative translational start site, was a " +
                "strongly hypomorphic allele. The two mutated nucleotides are located at chr9:37,422,744-37,422,745 (GRCh37).")));

        TargetGene targetGene = model.getTargetGene();
        assertThat(targetGene, hasProperty("entrezID", is("9380")));
        assertThat(targetGene, hasProperty("geneName", is("GRHPR")));

        Biocurator biocurator = model.getBiocurator();
        assertThat(biocurator, hasProperty("bioCuratorId", is("HPO:probinson")));

    }


    @Test
    public void getMendelianModelNames() {
        Collection<File> modelNames = mendelian.getModelNames();
        // we have 7 XML files in the MENDELIAN MODEL DIR
        assertThat(modelNames.size(), is(7));
        // test all files
        assertThat(modelNames, hasItems(
                new File(MENDELIAN_MODEL_DIR, "Coffey-SH2D1A-1998.xml"),
                new File(MENDELIAN_MODEL_DIR, "Damjanovich-ENG-2011-1.xml"),
                new File(MENDELIAN_MODEL_DIR, "Francova-LDLR-2004-1.xml"),
                new File(MENDELIAN_MODEL_DIR, "Francova-LDLR-2004-2.xml"),
                new File(MENDELIAN_MODEL_DIR, "Fu-GRHPR-2014.xml"),
                new File(MENDELIAN_MODEL_DIR, "Hetet-FTL-2003-2.xml"),
                new File(MENDELIAN_MODEL_DIR, "Hetet-FTL-2003-3.xml")));
        // all files end with proper suffix
        assertThat(modelNames.stream()
                .map(File::getName)
                .collect(Collectors.toList()), everyItem(endsWith(".xml")));
    }

    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% SOMATIC MUTATIONS (ONCOLOGY) %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    /**
     * We test single model <code>Poulos-NDUFB9-2015.xml</code>.
     */
    @Test
    public void readSomaticModelPoulos() throws Exception {
        DiseaseCaseModel model = somatic.readModel(
                new FileInputStream(new File(SOMATIC_MODEL_DIR, "Poulos-NDUFB9-2015.xml")));

        Publication pub = model.getPublication();
        assertThat(pub, hasProperty("authorlist", is("Poulos RC, Thoms JA, Shah A, Beck D, Pimanda JE, Wong JW")));
        assertThat(pub, hasProperty("title", is("Systematic Screening of Promoter Regions Pinpoints Functional Cis-Regulatory Mutations in a Cutaneous Melanoma Genome")));
        assertThat(pub, hasProperty("journal", is("Mol Cancer Res")));
        assertThat(pub, hasProperty("year", is("2015")));
        assertThat(pub, hasProperty("volume", is("13(8)")));
        assertThat(pub, hasProperty("pages", is("1218-26")));
        assertThat(pub, hasProperty("pmid", is("26082173")));

        List<Variant> variants = model.getVariants();
        assertThat(variants.size(), is(1));
        Variant first = variants.get(0);
        assertThat(first.getVariantMode(), is(VariantMode.SOMATIC));
        assertThat(model.getGenomeBuild(), is("37"));
        assertThat(first, hasProperty("chromosome", is("8")));
        assertThat(first, hasProperty("position", is("125551344")));
        assertThat(first, hasProperty("referenceAllele", is("C")));
        assertThat(first, hasProperty("alternateAllele", is("T")));
        assertThat(first, hasProperty("snippet", is("TCCGC[C/T]CTTCC")));
        assertThat(first, hasProperty("genotype", is("heterozygous")));
        assertThat(first, hasProperty("variantClass", is("5UTR")));
        assertThat(first, hasProperty("pathomechanism", is("5UTR|reduced-transcription")));
        assertThat(first, hasProperty("cosegregation", is("false")));
        assertThat(first, hasProperty("comparability", is("false")));

        SomaticVariant svar = ((SomaticVariant) first);
        assertThat(svar, hasProperty("reporterRegulation", is("down")));
        assertThat(svar, hasProperty("reporterResidualActivity", is("80.0")));
        assertThat(svar, hasProperty("MPatients", is("19")));
        assertThat(svar, hasProperty("NPatients", is("432")));

        Disease disease = model.getDisease();
        assertThat(disease, hasProperty("database", is("NCI")));
        assertThat(disease, hasProperty("diseaseId", is("C3510")));
        assertThat(disease, hasProperty("diseaseName", is("Cutaneous Melanoma")));

        Metadata metadata = model.getMetadata();
        assertThat(metadata, hasProperty("metadataText", is("The authors describe an NDUFB9 putative " +
                "promoter mutation (chr8:125,551,344 C>T) within the 5'-untranslated region (UTR) of NDUFB9. The " +
                "mutation was found in 19 of 432 samples with cutaneous melanoma. The protein encoded by NDUFB9 is part " +
                "of both the mitochondrial dysfunction and oxidative phosphorylation (OXPHOS) key canonical pathways. " +
                "In reporter assays, activity of the mutant NDUFB9 promoter decreased compared to wild-type. " +
                "The mutation is predicted to abolish an SP1/Krueppel-like factor (KLF) binding motif and lies in a " +
                "highly conserved region. The authors showed that he mutation co-occurs significantly in melanoma with " +
                "nonsilent NF1 coding mutations.")));

        TargetGene targetGene = model.getTargetGene();
        assertThat(targetGene, hasProperty("entrezID", is("4715")));
        assertThat(targetGene, hasProperty("geneName", is("NDUFB9")));


        Biocurator biocurator = model.getBiocurator();
        assertThat(biocurator, hasProperty("bioCuratorId", is("HPO:probinson")));

        FamilyInfo familyInfo = model.getFamilyInfo();
        assertThat(familyInfo, hasProperty("familyOrPatientID", is(nullValue())));

        List<HPO> hpos = model.getHpoList();
        assertThat(hpos.size(), is(0));
    }

    /**
     * We test single model <code>Rachakonda-TERT-bladder-2013-1.xml</code>.
     */
    @Test
    public void readSomaticModelRachakonda() throws Exception {
        DiseaseCaseModel model = somatic.readModel(
                new FileInputStream(new File(SOMATIC_MODEL_DIR, "Rachakonda-TERT-bladder-2013-1.xml")));

        Publication pub = model.getPublication();
        assertThat(pub, hasProperty("authorlist", is("Rachakonda PS, Hosen I, de Verdier PJ, Fallah M, Heidenreich B, Ryk C, Wiklund NP, Steineck G, Schadendorf D, Hemminki K, Kumar R")));
        assertThat(pub, hasProperty("title", is("TERT promoter mutations in bladder cancer affect patient survival and disease recurrence through modification by a common polymorphism")));
        assertThat(pub, hasProperty("journal", is("Proc Natl Acad Sci U S A")));
        assertThat(pub, hasProperty("year", is("2013")));
        assertThat(pub, hasProperty("volume", is("110(43)")));
        assertThat(pub, hasProperty("pages", is("17426-31")));
        assertThat(pub, hasProperty("pmid", is("24101484")));

        List<Variant> variants = model.getVariants();
        assertThat(variants.size(), is(1));
        Variant first = variants.get(0);
        assertThat(first.getVariantMode(), is(VariantMode.SOMATIC));
        assertThat(model.getGenomeBuild(), is("37"));
        assertThat(first, hasProperty("chromosome", is("5")));
        assertThat(first, hasProperty("position", is("1295228")));
        assertThat(first, hasProperty("referenceAllele", is("G")));
        assertThat(first, hasProperty("alternateAllele", is("A")));
        assertThat(first, hasProperty("snippet", is("CGGA[G/A]GGG")));
        assertThat(first, hasProperty("genotype", is("heterozygous")));
        assertThat(first, hasProperty("variantClass", is("promoter")));
        assertThat(first, hasProperty("pathomechanism", is("promoter|increased-transcription")));
        assertThat(first, hasProperty("cosegregation", is("false")));
        assertThat(first, hasProperty("comparability", is("false")));

        SomaticVariant svar = ((SomaticVariant) first);
        assertThat(svar, hasProperty("regulator", is("TERT promoter")));
        assertThat(svar, hasProperty("reporterRegulation", is("up")));
        assertThat(svar, hasProperty("reporterResidualActivity", is("200.0")));
        assertThat(svar, hasProperty("MPatients", is("175")));
        assertThat(svar, hasProperty("NPatients", is("327")));

        Disease disease = model.getDisease();
        assertThat(disease, hasProperty("database", is("NCI")));
        assertThat(disease, hasProperty("diseaseId", is("C4912")));
        assertThat(disease, hasProperty("diseaseName", is("Bladder Carcinoma")));

        Metadata metadata = model.getMetadata();
        assertThat(metadata, hasProperty("metadataText", is("The authors investigated tumors from a " +
                "well-characterized series of 327 patients with urothelial cell carcinoma of bladder. The somatic " +
                "mutations, mainly at positions -124 and -146 bp from ATG start site that create binding motifs for " +
                "E-twenty six/ternary complex factors (Ets/TCF), affected 65.4% of the tumors, with even distribution " +
                "across different stages and grades. The authors' data showed that a common polymorphism rs2853669, " +
                "within a preexisting Ets2 binding site in the TERT promoter, acts as a modifier of the effect of the " +
                "mutations on survival and tumor recurrence. The patients with the mutations showed poor survival in the " +
                "absence [hazard ratio (HR) 2.19, 95% confidence interval (CI) 1.02-4.70] but not in the presence " +
                "(HR 0.42, 95% CI 0.18-1.01) of the variant allele of the polymorphism. The mutations in the absence of " +
                "the variant allele were highly associated with the disease recurrence in patients with Tis, Ta, and T1 " +
                "tumors (HR 1.85, 95% CI 1.11-3.08).")));

        TargetGene targetGene = model.getTargetGene();
        assertThat(targetGene, hasProperty("entrezID", is("7015")));
        assertThat(targetGene, hasProperty("geneName", is("TERT")));


        Biocurator biocurator = model.getBiocurator();
        assertThat(biocurator, hasProperty("bioCuratorId", is("HPO:mjabif")));

        FamilyInfo familyInfo = model.getFamilyInfo();
        assertThat(familyInfo, hasProperty("familyOrPatientID", is(nullValue())));

        List<HPO> hpos = model.getHpoList();
        assertThat(hpos.size(), is(0));
    }

    @Test
    public void getSomaticModelNames() {
        Collection<File> modelNames = somatic.getModelNames();
        // we have 6 XML files in the SOMATIC MODEL DIR
        assertThat(modelNames.size(), is(6));
        // test all files
        assertThat(modelNames, hasItems(
                new File(SOMATIC_MODEL_DIR, "Denisova-DPH3-SCC-1.xml"),
                new File(SOMATIC_MODEL_DIR, "George-TERT-2015.xml"),
                new File(SOMATIC_MODEL_DIR, "Poulos-NDUFB9-2015.xml"),
                new File(SOMATIC_MODEL_DIR, "Rachakonda-TERT-bladder-2013-1.xml"),
                new File(SOMATIC_MODEL_DIR, "Rachakonda-TERT-bladder-2013-2.xml"),
                new File(SOMATIC_MODEL_DIR, "Scholz-TERT-2015-3.xml")));
        // all files end with proper suffix
        assertThat(modelNames.stream()
                .map(File::getName)
                .collect(Collectors.toList()), everyItem(endsWith(".xml")));
    }

}