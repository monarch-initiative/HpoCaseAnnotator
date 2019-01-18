package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * This validator doesn't validate {@link DiseaseCase} directly. The purpose of this validator is to check if a
 * publication with given pmid has been already curated within this project and if there exists a corresponding model
 * file in project directory.
 */
public final class PubMedValidator implements Validator<Publication> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PubMedValidator.class);

    private final File diseaseCaseDirectory;


    PubMedValidator(File diseaseCaseDirectory) {
        this.diseaseCaseDirectory = diseaseCaseDirectory;
    }

    /**
     * Read all model files in project directory and check if there exists at least one model file that contains a
     * {@link Publication} with the same PMID as of this <code>instance</code>.
     *
     * @return empty {@link List} if there is no model satisfying the above criterion
     */
    @Override
    public List<ValidationResult> validate(Publication instance) {
        // TODO implement PubMed validator
        return Collections.emptyList();
    }
}

//    public boolean seenThisPMIDBefore(String pmid) {
//        Collection<File> modelNames = modelParser.getModelNames();
//        Set<DiseaseCase> models = new HashSet<>();
//        for (File name : modelNames) {
//            try (InputStream inputStream = new FileInputStream(name)) {
//                modelParser.readModel(inputStream)
//                        .ifPresent(models::add);
//            } catch (IOException e) {

//                LOGGER.warn("Unable to read file {}", name.getAbsolutePath());
//            }
//        }
//
//        return !models.isEmpty() && models.stream().anyMatch(model -> pmid.equals(model.getPublication().getPmid()));

//        return false;
//    }
