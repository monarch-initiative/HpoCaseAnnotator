package org.monarchinitiative.hpo_case_annotator.model.io;

import org.monarchinitiative.hpo_case_annotator.model.xml_model.*;
import org.monarchinitiative.hpo_case_annotator.model.first_model.MutationReader;
import org.monarchinitiative.hpo_case_annotator.model.first_model.mutation.Mutation;
import org.monarchinitiative.hpo_case_annotator.model.first_model.mutation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * This parser is able to parse XML files with variants which have either {@link VariantMode#MENDELIAN} or {@link VariantMode#SOMATIC}
 * mode.
 * <p>
 * In order to use the parser to decode {@link VariantMode#MENDELIAN} XML files, there must be a <code>hgrm.dtd</code> in
 * the current working directory.
 * <p>
 * In order to decode {@link VariantMode#SOMATIC} XML files, there must be a <code>cancer-mutation.dtd</code> file in
 * the current working directory.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public class FirstModelXMLParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstModelXMLParser.class);

    private static final String MODEL_SUFFIX = ".xml";

    /** Path to directory where all the Mutation files are stored */
    private final File diseaseCaseDir;

    private final MutationReader mutationReader;


    public FirstModelXMLParser(File mutationDir) {
        this.diseaseCaseDir = mutationDir;
        this.mutationReader = new MutationReader(mutationDir);
    }


    /**
     * This method performs all the heavy lifting of moving data from {@link org.monarchinitiative.hpo_case_annotator.model.first_model.mutation.Variant}
     * and {@link Validation} into the {@link Variant} class.
     * @param var {@link org.monarchinitiative.hpo_case_annotator.model.first_model.mutation.Variant} with data and
     * @param validation {@link Validation} data to be moved into the
     * @return {@link Variant} object
     */
    private static Variant mapVariant(org.monarchinitiative.hpo_case_annotator.model.first_model.mutation.Variant var,
                                      Validation validation) {
        if (validation.hasFrequencyData()) { // then it is VariantMode#SOMATIC
            SomaticVariant variant = new SomaticVariant();
            // Variant fields
            variant.setChromosome(var.getChromosome());
            variant.setPosition(String.valueOf(var.getPosition()));
            variant.setReferenceAllele(var.getRef());
            variant.setAlternateAllele(var.getAlt());
            variant.setSnippet(var.getSnippet());
            variant.setGenotype(var.getGenotype());
            variant.setVariantClass(var.getVariantClass());
            variant.setPathomechanism(var.getPathMech());
            variant.setCosegregation(String.valueOf(validation.hasCosegregation()));
            variant.setComparability(String.valueOf(validation.hasComparability()));
            // SomaticVariant fields
            variant.setRegulator(var.getRegulator());
            variant.setReporterRegulation((validation.reporterIncreased()) ? "up" : "down");
            variant.setReporterResidualActivity(String.valueOf(validation.getReporterEffectSize()));
            variant.setEmsaValidationPerformed((validation.hasEMSA()) ? "yes" : "no");
            variant.setEmsaTFSymbol(validation.getEmsaSymbol());
            variant.setEmsaGeneId(String.valueOf(validation.getEmsaGeneID()));
            variant.setMPatients(String.valueOf(validation.getM_PatientsWithMutation()));
            variant.setNPatients(String.valueOf(validation.getN_Patients()));
            if (validation.otherIncreased())
                variant.setOther("up");
            else if (validation.otherDecreased())
                variant.setOther("down");
            else
                variant.setOther("no");
            variant.setOtherEffect(validation.getOtherCategory());

            return variant;
        } else { // else VariantMode#MENDELIAN
            MendelianVariant variant = new MendelianVariant();
            // Variant fields
            variant.setChromosome(var.getChromosome());
            variant.setPosition(String.valueOf(var.getPosition()));
            variant.setReferenceAllele(var.getRef());
            variant.setAlternateAllele(var.getAlt());
            variant.setSnippet(var.getSnippet());
            variant.setGenotype(var.getGenotype());
            variant.setVariantClass(var.getVariantClass());
            variant.setPathomechanism(var.getPathMech());
            variant.setCosegregation(String.valueOf(validation.hasCosegregation()));
            variant.setComparability(String.valueOf(validation.hasComparability()));
            // MendelianVariant fields
            variant.setRegulator(var.getRegulator());
            variant.setReporterRegulation((validation.reporterIncreased()) ? "up" : "down");
            variant.setReporterResidualActivity(String.valueOf(validation.getReporterEffectSize()));
            variant.setEmsaValidationPerformed((validation.hasEMSA()) ? "yes" : "no");
            variant.setEmsaTFSymbol(validation.getEmsaSymbol());
            variant.setEmsaGeneId(String.valueOf(validation.getEmsaGeneID()));
            if (validation.otherIncreased())
                variant.setOther("up");
            else if (validation.otherDecreased())
                variant.setOther("down");
            else
                variant.setOther("no");
            variant.setOtherEffect(validation.getOtherCategory());

            return variant;
        }
    }


    /**
     * The <em>first</em> format used to record 2 variants for a {@link Mutation} instance all the time. If only one
     * variant led to a disease, the second variant was a <em>dummy</em> variant with no data. In the newer format we do
     * <em>NOT</em> want to store <em>dummy</em> variants and this class is designed to get rid of them.
     *
     * @param var to be assessed
     * @return <code>true</code> if the variant is <em>dummy</em>
     */
    private static boolean isComplete(org.monarchinitiative.hpo_case_annotator.model.first_model.mutation.Variant var) {
        return var.getChromosome() != null
                && var.getPosition() != -1
                && var.getRef() != null
                && var.getAlt() != null
                && var.getClass() != null;
    }


//    @Override
    public void saveModel(OutputStream outputStream, DiseaseCaseModel model) throws IOException {
        throw new IOException("This parser cannot be used for saving model");
    }


//    @Override
    public DiseaseCaseModel readModel(InputStream inputStream) throws IOException {
        try {
            return convert(mutationReader.getSingleMutation(inputStream));
        } catch (SAXException e) {
            LOGGER.warn(e.getMessage());
            throw new IOException(e);
        }
    }


//    @Override
    public Collection<File> getModelNames() {
        if (diseaseCaseDir == null) {
            return new HashSet<>();
        }
        File[] files = diseaseCaseDir.listFiles(f -> f.getName().endsWith(MODEL_SUFFIX));
        if (files == null) {
            return new HashSet<>();
        }
        return Arrays.asList(files);
    }


    /**
     * This function maps information from {@link Mutation} into a {@link DiseaseCaseModel} object.
     *
     * @param mutation {@link Mutation} with input data
     * @return {@link DiseaseCaseModel} with mapped data
     */
    private static DiseaseCaseModel convert(Mutation mutation) {
        DiseaseCaseModel model = new DiseaseCaseModel();
        // Genome Build
        model.setGenomeBuild(mutation.getBuild());
        // Publication
        Publication publication = new Publication();
        publication.setAuthorlist(mutation.getPublicationAuthorlist());
        publication.setTitle(mutation.getPublicationTitle());
        publication.setJournal(mutation.getPublicationJournal());
        publication.setPages(mutation.getPublicationPages());
        publication.setPmid(mutation.getPMID());
        publication.setVolume(mutation.getPublicationVolume());
        publication.setYear(mutation.getPublicationYear());
        model.setPublication(publication);
        // Metadata
        Metadata metadata = new Metadata();
        metadata.setMetadataText(mutation.getMetadata());
        model.setMetadata(metadata);
        // TargetGene
        TargetGene targetGene = new TargetGene();
        targetGene.setEntrezID(mutation.getTargetGeneEntrezID());
        targetGene.setGeneName(mutation.getGeneSymbol());
        model.setTargetGene(targetGene);
        // List<Variant>
        List<Variant> variantList = new ArrayList<>(3);
        if (isComplete(mutation.getVariant1()))
            variantList.add(mapVariant(mutation.getVariant1(), mutation.getValidation1()));
        if (isComplete(mutation.getVariant2()))
            variantList.add(mapVariant(mutation.getVariant2(), mutation.getValidation2()));

        model.setVariants(variantList);
        // List<HPO>
        List<HPO> hpos = new ArrayList<>();
        mutation.getHPOterms().forEach((id, name) -> hpos.add(new HPO(id, name, "YES"))); // term is implicitly observed
        model.setHpoList(hpos);
        // Disease
        Disease disease = new Disease();
        disease.setDatabase(mutation.getDiseaseDatabase());
        disease.setDiseaseId(mutation.getDiseaseID());
        disease.setDiseaseName(mutation.getDiseaseName());
        model.setDisease(disease);
        // FamilyInfo
        FamilyInfo familyInfo = new FamilyInfo();
        familyInfo.setFamilyOrPatientID(mutation.getIdentifier());
        model.setFamilyInfo(familyInfo);
        // Biocurator
        Biocurator biocurator = new Biocurator();
        biocurator.setBioCuratorId(mutation.getBiocurator());
        model.setBiocurator(biocurator);

        return model;
    }
}
