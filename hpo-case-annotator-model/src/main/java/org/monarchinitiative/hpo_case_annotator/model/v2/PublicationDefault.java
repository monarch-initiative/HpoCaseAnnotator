package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.util.List;

record PublicationDefault(List<String> authors, String title, String journal,
                          int year, String volume, String pages, String pmid) implements Publication {
}
