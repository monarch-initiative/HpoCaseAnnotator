package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.util.List;

public interface Publication {

    static Publication of(List<String> authors, String title, String journal,
                          int year, String volume, String pages, String pmid) {
        return new PublicationDefault(authors, title, journal, year, volume, pages, pmid);
    }

    List<String> authors();

    String title();

    String journal();

    int year();

    String volume();

    String pages();

    String pmid();

    int hashCode();

    boolean equals(Object o);

}
