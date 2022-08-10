package org.monarchinitiative.hpo_case_annotator.forms.stepper.util;

import javafx.concurrent.Task;
import org.monarchinitiative.hpo_case_annotator.core.publication.PubMedSummaryRetriever;
import org.monarchinitiative.hpo_case_annotator.core.publication.PublicationDataParser;
import org.monarchinitiative.hpo_case_annotator.model.v2.Publication;

public class PubmedIO {

    private static final PubMedSummaryRetriever<Publication> V2_WORKER = PubMedSummaryRetriever.<Publication>builder()
            .publicationDataParser(PublicationDataParser.forV2PublicationFormat())
            .build();

    private PubmedIO() {
    }

    public static Task<Publication> v2publication(String pmid) {
        return new Task<>() {
            @Override
            protected Publication call() throws Exception {
                return V2_WORKER.getPublication(pmid);
            }
        };
    }
}
