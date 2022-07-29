package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.Publication;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;

public class ObservablePublication implements Publication, Updateable<Publication> {
    private final StringProperty authors = new SimpleStringProperty(this, "authors");
    private final StringProperty title = new SimpleStringProperty(this, "title");
    private final StringProperty journal = new SimpleStringProperty(this, "journal");
    private final ObjectProperty<Integer> year = new SimpleObjectProperty<>(this, "year");
    private final StringProperty volume = new SimpleStringProperty(this, "volume");
    private final StringProperty pages = new SimpleStringProperty(this, "pages");
    private final StringProperty pmid = new SimpleStringProperty(this, "pmid");

    public ObservablePublication() {
    }

    public ObservablePublication(Publication publication) {
        if (publication != null) {
            authors.set(publication.getAuthors());
            title.set(publication.getTitle());
            journal.set(publication.getJournal());
            year.set(publication.getYear());
            volume.set(publication.getVolume());
            pages.set(publication.getPages());
            pmid.set(publication.getPmid());
        }
    }

    @Override
    public String getAuthors() {
        return authors.get();
    }

    public void setAuthors(String authors) {
        this.authors.set(authors);
    }

    public StringProperty authorsProperty() {
        return authors;
    }

    @Override
    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    @Override
    public String getJournal() {
        return journal.get();
    }

    public void setJournal(String journal) {
        this.journal.set(journal);
    }

    public StringProperty journalProperty() {
        return journal;
    }

    @Override
    public Integer getYear() {
        return year.get();
    }

    public void setYear(int year) {
        this.year.set(year);
    }

    public ObjectProperty<Integer> yearProperty() {
        return year;
    }

    @Override
    public String getVolume() {
        return volume.get();
    }

    public void setVolume(String volume) {
        this.volume.set(volume);
    }

    public StringProperty volumeProperty() {
        return volume;
    }

    @Override
    public String getPages() {
        return pages.get();
    }

    public void setPages(String pages) {
        this.pages.set(pages);
    }

    public StringProperty pagesProperty() {
        return pages;
    }

    @Override
    public String getPmid() {
        return pmid.get();
    }

    public void setPmid(String pmid) {
        this.pmid.set(pmid);
    }

    public StringProperty pmidProperty() {
        return pmid;
    }

    @Override
    public void update(Publication data) {
        if (data == null) {
            authors.set(null);
            title.set(null);
            journal.set(null);
            year.set(-1);
            volume.set(null);
            pages.set(null);
            pmid.set(null);
        } else {
            authors.set(data.getAuthors());
            title.set(data.getTitle());
            journal.set(data.getJournal());
            year.set(data.getYear());
            volume.set(data.getVolume());
            pages.set(data.getPages());
            pmid.set(data.getPmid());
        }
    }

    @Override
    public String toString() {
        return "ObservablePublication{" +
                "authors=" + authors.get() +
                ", title=" + title.get() +
                ", journal=" + journal.get() +
                ", year=" + year.get() +
                ", volume=" + volume.get() +
                ", pages=" + pages.get() +
                ", pmid=" + pmid.get() +
                '}';
    }
}
