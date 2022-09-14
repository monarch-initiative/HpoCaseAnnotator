package org.monarchinitiative.hpo_case_annotator.model.v2;

record PublicationDefault(String authors, String title, String journal,
                          int year, String volume, String pages, String pmid) implements Publication {
    @Override
    public String getAuthors() {
        return authors;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getJournal() {
        return journal;
    }

    @Override
    public Integer getYear() {
        return year;
    }

    @Override
    public String getVolume() {
        return volume;
    }

    @Override
    public String getPages() {
        return pages;
    }

    @Override
    public String getPmid() {
        return pmid;
    }
}
