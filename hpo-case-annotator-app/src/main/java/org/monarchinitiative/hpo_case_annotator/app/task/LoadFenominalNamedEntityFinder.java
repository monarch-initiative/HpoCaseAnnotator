package org.monarchinitiative.hpo_case_annotator.app.task;

import javafx.concurrent.Task;
import org.monarchinitiative.fenominal.core.TermMiner;
import org.monarchinitiative.hpo_case_annotator.app.mining.FenominalNamedEntityFinder;
import org.monarchinitiative.hpo_case_annotator.core.mining.NamedEntityFinder;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task for creating {@link NamedEntityFinder} using provided HPO ({@code hpo}).
 */
public class LoadFenominalNamedEntityFinder extends Task<NamedEntityFinder> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFenominalNamedEntityFinder.class);

    private final Ontology hpo;

    public LoadFenominalNamedEntityFinder(Ontology hpo) {
        this.hpo = hpo;
    }

    @Override
    protected NamedEntityFinder call() throws Exception {
        LOGGER.debug("Setting up fuzzy Fenominal named entity finder.");
        TermMiner miner = TermMiner.defaultFuzzyMapper(hpo);
        return FenominalNamedEntityFinder.of(miner);
    }

}
