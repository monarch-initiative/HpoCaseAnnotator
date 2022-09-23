package org.monarchinitiative.hpo_case_annotator.app.task;

import javafx.concurrent.Task;
import org.monarchinitiative.hpo_case_annotator.core.reference.functional.FunctionalAnnotationService;
import org.monarchinitiative.hpo_case_annotator.core.reference.functional.JannovarFunctionalAnnotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class LoadJannovarFunctionalAnnotationService extends Task<FunctionalAnnotationService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadJannovarFunctionalAnnotationService.class);

    private final Path jannovarPath;

    public LoadJannovarFunctionalAnnotationService(Path jannovarPath) {
        this.jannovarPath = jannovarPath;
    }

    @Override
    protected FunctionalAnnotationService call() throws Exception {
        LOGGER.debug("Creating FunctionalAnnotationService from Jannovar cache at {}", jannovarPath.toAbsolutePath());
        JannovarFunctionalAnnotationService service = JannovarFunctionalAnnotationService.of(jannovarPath);
        LOGGER.debug("Done!");
        return service;
    }
}
