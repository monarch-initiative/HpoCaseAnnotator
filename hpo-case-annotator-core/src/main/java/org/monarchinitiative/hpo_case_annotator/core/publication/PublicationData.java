package org.monarchinitiative.hpo_case_annotator.core.publication;

import java.util.List;

record PublicationData(
        List<String> authors,
        String title,
        String journal,
        String year,
        String volume,
        String pages,
        String pmid) {
}
