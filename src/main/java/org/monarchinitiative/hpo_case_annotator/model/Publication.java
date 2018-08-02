package org.monarchinitiative.hpo_case_annotator.model;

import com.fasterxml.jackson.annotation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Bean for storing attributes of scientific publication. Currently author list, title, journal, year, volume, pages and
 * pmid are being stored here.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class Publication {

    private final StringProperty authorlist = new SimpleStringProperty(this, "authorlist", "");

    private final StringProperty title = new SimpleStringProperty(this, "title", "");

    private final StringProperty journal = new SimpleStringProperty(this, "journal", "");

    private final StringProperty year = new SimpleStringProperty(this, "year", "");

    private final StringProperty volume = new SimpleStringProperty(this, "volume", "");

    private final StringProperty pages = new SimpleStringProperty(this, "pages", "");

    private final StringProperty pmid = new SimpleStringProperty(this, "pmid", "");


    @JsonCreator
    public Publication(
            @JsonProperty("authorlist") String authorlist,
            @JsonProperty("title") String title,
            @JsonProperty("journal") String journal,
            @JsonProperty("year") String year,
            @JsonProperty("volume") String volume,
            @JsonProperty("pages") String pages,
            @JsonProperty("pmid") String pmid) {
        this.authorlist.set(authorlist);
        this.title.set(title);
        this.journal.set(journal);
        this.year.set(year);
        this.volume.set(volume);
        this.pages.set(pages);
        this.pmid.set(pmid);
    }


    public Publication() {
        // no-op
    }


    /**
     * Return first 10 words of given string. Append "..." char sequence if string consists from more than 10 words.
     *
     * @param st
     * @return
     */
    private static String getTenWords(String st) {
        String[] words = st.split(" ");
        StringBuilder sb = new StringBuilder(10);
        int len = (words.length < 10) ? words.length : 10;
        for (int i = 0; i < len; i++) {
            sb.append(words[i]).append(" ");
        }
        if (len == 10) sb.append("..."); // we shortened title, adding dots
        return sb.toString().trim();
    }


    @JsonGetter
    public final String getAuthorlist() {
        return authorlist.get();
    }


    @JsonSetter
    public final void setAuthorlist(String newAuthorlist) {
        authorlist.set(newAuthorlist);
    }


    public StringProperty authorlistProperty() {
        return authorlist;
    }


    @JsonIgnore
    public final String getFirstAuthorSurname() {
        if (getAuthorlist() == null || getAuthorlist().isEmpty()) {
            return "author";
        }
        String firstAuthor = getAuthorlist().split(",")[0];
        int lastindex = firstAuthor.lastIndexOf(' ');
        if (lastindex >= 0)
            return firstAuthor.substring(0, lastindex);
        else
            return "author";
    }


    @JsonGetter
    public final String getTitle() {
        return title.get();
    }


    @JsonSetter
    public final void setTitle(String newTitle) {
        title.set(newTitle);
    }


    public StringProperty titleProperty() {
        return title;
    }


    @JsonGetter
    public final String getJournal() {
        return journal.get();
    }


    @JsonSetter
    public final void setJournal(String newJournal) {
        journal.set(newJournal);
    }


    public StringProperty journalProperty() {
        return journal;
    }


    @JsonGetter
    public final String getYear() {
        return year.get();
    }


    @JsonSetter
    public final void setYear(String newYear) {
        year.set(newYear);
    }


    public StringProperty yearProperty() {
        return year;
    }


    @JsonGetter
    public final String getVolume() {
        return volume.get();
    }


    @JsonSetter
    public final void setVolume(String newVolume) {
        volume.set(newVolume);
    }


    public StringProperty volumeProperty() {
        return volume;
    }


    @JsonGetter
    public final String getPages() {
        return pages.get();
    }


    @JsonSetter
    public final void setPages(String newPages) {
        pages.set(newPages);
    }


    public StringProperty pagesProperty() {
        return pages;
    }


    @JsonGetter
    public final String getPmid() {
        return pmid.get();
    }


    @JsonSetter
    public final void setPmid(String newPmid) {
        pmid.set(newPmid);
    }


    public StringProperty pmidProperty() {
        return pmid;
    }

    /**
     * Test if instance contains some data.
     *
     * @return true if any attribute of instance is not null or empty String
     */
    @JsonIgnore
    public final boolean isEmpty() {
        return (getTitle() == null || "".equals(getTitle()))
                && (getJournal() == null || "".equals(getJournal()))
                && (getYear() == null || "".equals(getYear()))
                && (getVolume() == null || "".equals(getVolume()))
                && (getPages() == null || "".equals(getPages()))
                && (getPmid() == null || "".equals(getPmid()));
    }


    @Override
    public int hashCode() {
        int result = getAuthorlist() != null ? getAuthorlist().hashCode() : 0;
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getJournal() != null ? getJournal().hashCode() : 0);
        result = 31 * result + (getYear() != null ? getYear().hashCode() : 0);
        result = 31 * result + (getVolume() != null ? getVolume().hashCode() : 0);
        result = 31 * result + (getPages() != null ? getPages().hashCode() : 0);
        result = 31 * result + (getPmid() != null ? getPmid().hashCode() : 0);
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Publication that = (Publication) o;

        if (getAuthorlist() != null ? !getAuthorlist().equals(that.getAuthorlist()) : that.getAuthorlist() != null)
            return false;
        if (getTitle() != null ? !getTitle().equals(that.getTitle()) : that.getTitle() != null) return false;
        if (getJournal() != null ? !getJournal().equals(that.getJournal()) : that.getJournal() != null) return false;
        if (getYear() != null ? !getYear().equals(that.getYear()) : that.getYear() != null) return false;
        if (getVolume() != null ? !getVolume().equals(that.getVolume()) : that.getVolume() != null) return false;
        if (getPages() != null ? !getPages().equals(that.getPages()) : that.getPages() != null) return false;
        return getPmid() != null ? getPmid().equals(that.getPmid()) : that.getPmid() == null;
    }


    @Override
    public String toString() {
        return "Publication{" +
                "authorlist=" + authorlist.get() +
                ", title=" + title.get() +
                ", journal=" + journal.get() +
                ", year=" + year.get() +
                ", volume=" + volume.get() +
                ", pages=" + pages.get() +
                ", pmid=" + pmid.get() +
                '}';
    }
}
