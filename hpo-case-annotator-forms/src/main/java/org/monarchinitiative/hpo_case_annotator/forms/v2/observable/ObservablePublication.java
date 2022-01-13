package org.monarchinitiative.hpo_case_annotator.forms.v2.observable;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;

public class ObservablePublication {
    private final ObservableList<String> authors = FXCollections.observableList(new LinkedList<>());
    private final StringProperty title = new SimpleStringProperty(this, "title");
    private final StringProperty journal = new SimpleStringProperty(this, "journal");
    private final IntegerProperty year = new SimpleIntegerProperty(this, "year");
    private final StringProperty volume = new SimpleStringProperty(this, "volume");
    private final StringProperty pages = new SimpleStringProperty(this, "pages");
    private final StringProperty pmid = new SimpleStringProperty(this, "pmid");

    public ObservableList<String> authors() {
        return authors;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getJournal() {
        return journal.get();
    }

    public void setJournal(String journal) {
        this.journal.set(journal);
    }

    public StringProperty journalProperty() {
        return journal;
    }

    public int getYear() {
        return year.get();
    }

    public void setYear(int year) {
        this.year.set(year);
    }

    public IntegerProperty yearProperty() {
        return year;
    }

    public String getVolume() {
        return volume.get();
    }

    public void setVolume(String volume) {
        this.volume.set(volume);
    }

    public StringProperty volumeProperty() {
        return volume;
    }

    public String getPages() {
        return pages.get();
    }

    public void setPages(String pages) {
        this.pages.set(pages);
    }

    public StringProperty pagesProperty() {
        return pages;
    }

    public String getPmid() {
        return pmid.get();
    }

    public void setPmid(String pmid) {
        this.pmid.set(pmid);
    }

    public StringProperty pmidProperty() {
        return pmid;
    }
}
