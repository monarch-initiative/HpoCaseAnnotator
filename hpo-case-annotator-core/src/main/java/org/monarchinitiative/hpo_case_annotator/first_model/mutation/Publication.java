package org.monarchinitiative.hpo_case_annotator.first_model.mutation;


/**
 * Data object to encapsulate the information about a PubMed abstract
 * for an article that is being parsed.
 *
 * @author Peter Robinson
 * @version 0.0.1 (June 5, 2016)
 */
public class Publication {

    private String authorlist = null;

    private String title = null;

    private String journal = null;

    /** The year of publication */
    private String year = null;

    /** The journal volume of the publication */
    private String volume = null;

    /** The pages (from--to) of the publication */
    private String pages = null;

    private String pmid = null;

    private String error = null;


    public Publication() {

    }


    /** @return true of the parse was good */
    public boolean isValid() {
        return (authorlist != null &&
                title != null &&
                journal != null &&
                year != null &&
                volume != null &&
                pages != null &&
                pmid != null);
    }


    /**
     * @return The title of the  article describing the mutation
     */
    public String getPublicationTitle() {
        return this.title;
    }


    /**
     * @param t The title of the  article describing the mutation
     */
    public void setPublicationTitle(String t) {
        this.title = t;
    }


    /** @return List of the authors of the article describing the mutation */
    public String getPublicationAuthorlist() {
        return this.authorlist;
    }


    /** @param l List of the authors of the article describing the mutation */
    public void setPublicationAuthorlist(String l) {
        this.authorlist = l;
    }


    /** @return The year in which the article describing the mutation was published. */
    public String getPublicationYear() {
        return this.year;
    }


    /**
     * @param y The year in which the article describing the mutation was published.
     */
    public void setPublicationYear(String y) {
        this.year = y;
    }


    /** @return The volume of the journal article in which the mutation was published. */
    public String getPublicationVolume() {
        return this.volume;
    }


    /**
     * The volume of the journal in which the
     * current mutation was published. Can optionally also include the
     * issue in parentheses.
     *
     * @param v The volume (e.g., <b>5</b> or <b>6(7)</b>)
     */
    public void setPublicationVolume(String v) {
        this.volume = v;
    }


    /** @return The pages of the journal article in which the mutation was published. */
    public String getPublicationPages() {
        return this.pages;
    }


    /**
     * The range of pages for the article in which the current
     * mutation was described.
     *
     * @param p Page range, e.g., 56-62
     */
    public void setPublicationPages(String p) {
        this.pages = p;
    }


    /** @return The PubMed ID of the  article in which the mutation was published. */
    public String getPMID() {
        return this.pmid;
    }


    /** @param p The PubMed ID of the  article in which the mutation was published. */
    public void setPMID(String p) {
        this.pmid = p;
    }


    /**
     * @return the journal article in which the mutation was published.
     */
    public String getPublicationJournal() {
        return this.journal;
    }


    /**
     * @param j the journal article in which the mutation was published.
     */
    public void setPublicationJournal(String j) {
        this.journal = j;
    }


    public String getFirstAuthorAndYear() {
        String A[] = this.authorlist.split(",");
        return String.format("%s, %s", A[0], this.year);
    }


    public boolean hasError() {
        return this.error != null;
    }


    public String getError() {
        return this.error;
    }


    public void setError(String error) {
        if (error != null && error.length() > 0)
            this.error = error;
    }


    public String longForm() {
        return String.format("%s(%s)%s. %s %s:%s [%s]", authorlist, year, title, journal, volume, pages, pmid);
    }


    private String getFirstAuthor() {
        try {
            int x = authorlist.indexOf(",");
            return authorlist.substring(0, x);
        } catch (Exception e) {
            System.err.println(String.format("[ERROR] Could not parse authorlist (%s) in Publication.java", authorlist));
            e.printStackTrace();
            System.exit(1);
        }
        return "";
    }


    public boolean isInitialized() {
        return (authorlist != null &&
                title != null &&
                journal != null &&
                year != null &&
                volume != null &&
                pages != null &&
                pmid != null);
    }


    /**
     * @return a string like Smith,2012,pmid:3212121
     */
    public String shortForm() {
        String fa = getFirstAuthor();
        return String.format("%s(%s,pmid:%s)", fa, year, pmid);
    }


    /* OUTPUT like this:/** Expect something like this
     * 1: Nanji MS, Nguyen VT, Kawasoe JH, Inui K, Endo F, Nakajima T, Anezaki T, Cox DW.
     * Haplotype and mutation analysis in Japanese patients with Wilson disease.
     * Am  J Hum Genet. 1997 Jun;60(6):1423-9. PubMed PMID: 9199563; PubMed Central PMCID: PMC1716137. */
    public String asPubMedSummary() {
        String s = String.format("1: %s. %s. %s. %s;%s:%s. PMID: %s;.",
                authorlist,
                title,
                journal,
                year,
                volume,
                pages,
                pmid);
        return s;
    }

}
