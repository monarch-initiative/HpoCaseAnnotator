package org.monarchinitiative.hpo_case_annotator.app.task;

import javafx.concurrent.Task;
import org.monarchinitiative.hpo_case_annotator.app.Startup;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.core.io.OMIMParser;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class LoadOmimDiseases extends Task<DiseaseIdentifierService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadOmimDiseases.class);

    private final String omimPath;

    public LoadOmimDiseases(String omimPath) {
        this.omimPath = Objects.requireNonNull(omimPath);
    }

    @Override
    protected DiseaseIdentifierService call() throws Exception {
        LOGGER.debug("Parsing bundled OMIM file '{}'", omimPath);
        List<DiseaseIdentifier> identifiers;
        try (InputStream is = Startup.class.getResourceAsStream(omimPath)) {
            identifiers = OMIMParser.loadDiseaseIdentifiers(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Error loading disease IDs from the bundled table", e);
            identifiers = List.of();
        }

        return DiseaseIdentifierService.of(identifiers);
    }
}
