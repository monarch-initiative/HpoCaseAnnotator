package org.monarchinitiative.hpo_case_annotator.model.v2;

public interface Publication {

    static Publication of(String authors, String title, String journal,
                          int year, String volume, String pages, String pmid) {
        return new PublicationDefault(authors, title, journal, year, volume, pages, pmid);
    }

    String getAuthors();

    String getTitle();

    String getJournal();

    int getYear();

    String getVolume();

    String getPages();

    String getPmid();

    int hashCode();

    boolean equals(Object o);

}
