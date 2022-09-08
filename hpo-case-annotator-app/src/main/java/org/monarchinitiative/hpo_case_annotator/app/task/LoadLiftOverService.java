package org.monarchinitiative.hpo_case_annotator.app.task;

import javafx.concurrent.Task;
import org.monarchinitiative.hpo_case_annotator.core.liftover.LiftOverAdapter;
import org.monarchinitiative.hpo_case_annotator.core.liftover.LiftOverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class LoadLiftOverService extends Task<LiftOverService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadLiftOverService.class);

    private final List<? extends File> chains;

    public LoadLiftOverService(List<? extends File> chains) {
        this.chains = chains;
    }

    @Override
    protected LiftOverService call() throws Exception {
        LOGGER.debug("Creating LiftOverAdapter from {} chains at {}",
                chains.size(),
                chains.stream()
                        .map(File::getAbsolutePath)
                        .collect(Collectors.joining(", ", "[", "]")));
        return LiftOverAdapter.ofChains(chains.toArray(File[]::new));
    }
}
