package org.monarchinitiative.hpo_case_annotator.app.mining;

import org.monarchinitiative.fenominal.core.TermMiner;
import org.monarchinitiative.hpo_case_annotator.core.mining.MinedTerm;
import org.monarchinitiative.hpo_case_annotator.core.mining.NamedEntityFinder;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of {@link NamedEntityFinder} backed by {@link TermMiner} from the {@code fenominal-core} library.
 */
public class FenominalNamedEntityFinder implements NamedEntityFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(FenominalNamedEntityFinder.class);

    private final TermMiner miner;

    public static FenominalNamedEntityFinder of(TermMiner miner) {
        return new FenominalNamedEntityFinder(miner);
    }

    private FenominalNamedEntityFinder(TermMiner miner) {
        this.miner = miner;
    }

    @Override
    public List<MinedTerm> process(String source) {
        return miner.mineTerms(source).stream()
                .map(toMinedTerm())
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    private static Function<org.monarchinitiative.fenominal.model.MinedTerm, Optional<MinedTerm>> toMinedTerm() {
        return mt -> {
            TermId id;
            try {
                id = TermId.of(mt.getTermIdAsString());
            } catch (PhenolRuntimeException e) {
                LOGGER.warn("Error converting {} to term ID: {}", mt.getTermIdAsString(), e.getMessage());
                LOGGER.debug("Error converting {} to term ID: {}", mt.getTermIdAsString(), e.getMessage(), e);
                return Optional.empty();
            }

            return Optional.of(MinedTerm.of(id, mt.getBegin(), mt.getEnd(), !mt.isPresent()));
        };
    }
}
