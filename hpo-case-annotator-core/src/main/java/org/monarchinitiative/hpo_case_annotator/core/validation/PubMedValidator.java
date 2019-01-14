package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.monarchinitiative.hpo_case_annotator.model.io.ModelParser;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This validator doesn't validate {@link DiseaseCase} directly because there is no PubMed attribute present in the
 * model. The purpose of this validator is to check if a publication with given pmid has been already curated within
 * this project and if there exists a corresponding model file in project directory.
 */
public final class PubMedValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PubMedValidator.class);

    private final ModelParser modelParser;


    public PubMedValidator(ModelParser modelParser) {
        this.modelParser = modelParser;
    }


    /**
     * Read all model files in project directory and check if there exists at least one model file that has given pmid.
     *
     * @return true if there is a file in project directory that has the same pmid as provided.
     */
    public boolean seenThisPMIDBefore(String pmid) {
        Collection<File> modelNames = modelParser.getModelNames();
        Set<DiseaseCase> models = new HashSet<>();
        for (File name : modelNames) {
            try (InputStream inputStream = new FileInputStream(name)) {
                modelParser.readModel(inputStream)
                        .ifPresent(models::add);
            } catch (IOException e) {
                // TODO user interaction - add Cancel button to Show Exception dialog which will break the for loop if
                // selected
                LOGGER.warn("Unable to read file {}", name.getAbsolutePath());
            }
        }

        return !models.isEmpty() && models.stream().anyMatch(model -> pmid.equals(model.getPublication().getPmid()));
    }

}