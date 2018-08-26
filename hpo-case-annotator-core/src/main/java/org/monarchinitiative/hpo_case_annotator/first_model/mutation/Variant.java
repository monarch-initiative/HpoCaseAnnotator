package org.monarchinitiative.hpo_case_annotator.first_model.mutation;


/**
 * A class that represents a single variant with build, chromosome, position, ref and alt.
 * Note that we use the VCF conventions with respect to how mutations are recorded.
 *
 * @author Peter Robinson
 * @version 0.1.14 (27 February, 2016)
 */
public class Variant implements Comparable<Variant> {

    private static final int BAD_VARIANT = -1;

    /** The genome build. For now, we are using GRCh37). */
    private String build = null;

    /** The chromosome on which the mutation occurs (the actual strings are coming from the widget). */
    private String chromosome = null;

    /** The position of the mutation on the chromosome (using VCF convention for indels). Initialized to {@link #BAD_VARIANT}, a flag. */
    private int pos = BAD_VARIANT;

    /** The reference sequence, using VCF convention for indels. */
    private String ref = null;

    /** The ALT (mutation) sequencing, using VCF convention for indels. */
    private String alt = null;

    /**
     * A string representing the enhancer or promoter or other element in which the
     * mutation is located. More precision than free text is not currently possible.
     */
    private String regulator = null;

    /**
     * A string representing the sequence that surounds the mutation, e.g., ACGT[A/T]ACGT, where the
     * nucleotides enclosed in brackets represent the wildtype (W) and mutated (M) sequence: [W/M]. In general
     * we attempt to have 5 nucleotides to either side of the mutation.
     */
    private String snippet = null;

    /** The category of this variant */
    private VarClass varclass = null;

    /**
     * The presumptive pathomechanism. The value should be taken from a list of terms in the
     * file resources/ontology/pathomechanism.tab. This file will be worked into a full ontology
     * later on with which will can organise the mutations in the database. For instance, a variant
     * category might be 3' UTR, but the presumptive mechanism might be polyadenylation tract mutant.
     * For now, we will take the opinion of the authors of the original publication for this value.
     */
    private String pathomechanism = null;

    /** One of HETEROZYGOUS or HOMOZYGOUS */
    private GenotypeClass genotype = null;


    public Variant() {
        this.build = "37";
        this.genotype = GenotypeClass.HETEROZYGOUS; /* default value */
        this.pathomechanism = "unknown"; /* default value */
    }


    public Variant(String build, String chromosome, int position, String rseq, String aseq, String gtype, String clazz, String snippet, String regulator, String pmech) {
        if (build.equals("37") || build.equals("38"))
            this.build = build;
        else
            throw new IllegalArgumentException(String.format("Build must be 37/38: build:\"%s\" not reconized", build));
        if (chromosome.equals("M") || chromosome.equals("X") || chromosome.equals("Y"))
            this.chromosome = chromosome;
        else { // Chromosome should be 1--22
            try { // The following just checks if we have a valid number
                Integer i = Integer.parseInt(chromosome);
                this.chromosome = chromosome;
            } catch (NumberFormatException nf) {
                throw new IllegalArgumentException(String.format("Did not recognize chromosome:\"%s\":%s", chromosome, nf.getMessage()));
            }
        }
        this.pos = position;
        this.ref = rseq;
        this.alt = aseq;
        this.snippet = snippet;
        this.regulator = regulator;
        setVariantClass(clazz);
        setGenotype(gtype);
        this.pathomechanism = pmech;
    }


    /**
     * Perform a Q/C check on the data to see if it is properly initialized.
     *
     * @return True if everything is OK, otherwise false.
     */
    public boolean isInitialized() {
        if (this.pos != BAD_VARIANT &&
                this.build != null &&
                this.chromosome != null &&
                this.ref != null &&
                this.alt != null &&
                this.varclass != null &&
                this.genotype != null) {
            return true;
        } else {
            return false;
        }
    }


    /** @return the string representing the sequence that surrounds the variant. */
    public String getSnippet() {
        return this.snippet;
    }


    /**
     * This method is used by the XML content handler to set the current value of snippet.
     */
    public void setSnippet(String sn) {
        this.snippet = sn;
    }


    /**
     * Set the value of {@link #pathomechanism}.
     */
    public void setPathomechanism(String pm) {
        this.pathomechanism = pm;
    }


    public String getPathMech() {
        return this.pathomechanism;
    }


    /**
     * This is used to ensure that we have entered a snippet and a pathomechanism
     * for each and every mutation (Q/C)
     */
    public boolean hasSnippet() {
        return this.snippet != null && this.snippet.length() > 0 && this.pathomechanism != null
                && this.pathomechanism.length() > 0 && !pathomechanism.equals("unknown");
    }


    /**
     * @param p Position of variant on chromosome represented as string
     */
    public void setPosition(String p) {
        try {
            Integer i = Integer.parseInt(p);
            this.pos = i.intValue();
        } catch (NumberFormatException e) {
            System.err.println("Error parsing position of variant: " + p + "\n" + e.getMessage());
            this.pos = BAD_VARIANT; // Flag
        }
    }

    ;


    public int getPosition() {
        return this.pos;
    }

    ;


    public void setPosition(int p) {
        this.pos = p;
    }


    public String getChromosome() {
        return this.chromosome;
    }


    public void setChromosome(String c) {
        this.chromosome = c;
    }


    public String getRef() {
        return this.ref;
    }


    public void setRef(String r) {
        this.ref = r;
    }


    public String getAlt() {
        return this.alt;
    }


    public void setAlt(String a) {
        this.alt = a;
    }


    public String getBuild() {
        return this.build;
    }


    public void setBuild(String b) {
        this.build = b;
    }


    public String getGenotype() {
        return this.genotype.toString();
    }


    public void setGenotype(String gtype) {
        if (gtype.equalsIgnoreCase("heterozygous") || gtype.equalsIgnoreCase("het"))
            this.genotype = GenotypeClass.HETEROZYGOUS;
        else if (gtype.equalsIgnoreCase("homozygous") || gtype.equalsIgnoreCase("hom"))
            this.genotype = GenotypeClass.HOMOZYGOUS;
        else {
            throw new IllegalArgumentException("Did not recognize genotype " + gtype);
        }
    }


    /**
     * @return a String representing the variant category (enhancer, promoter, etc). If null, returns ""
     */
    public String getVariantClass() {
        if (this.varclass != null)
            return this.varclass.toString();
        else
            return "";
    }


    public void setVariantClass(String vc) {
        if (vc.equalsIgnoreCase("enhancer"))
            this.varclass = VarClass.ENHANCER;
        else if (vc.equalsIgnoreCase("promoter"))
            this.varclass = VarClass.PROMOTER;
        else if (vc.equalsIgnoreCase("5UTR"))
            this.varclass = VarClass.UTR5;
        else if (vc.equalsIgnoreCase("3UTR"))
            this.varclass = VarClass.UTR3;
        else if (vc.equalsIgnoreCase("microRNAgene"))
            this.varclass = VarClass.MICRORNA_GENE;
        else if (vc.equalsIgnoreCase("coding"))
            this.varclass = VarClass.CODING;
        else if (vc.equalsIgnoreCase("ICR"))
            this.varclass = VarClass.ICR;
        else if (vc.equalsIgnoreCase("RNP_RNAcomponent"))
            this.varclass = VarClass.RNP_RNA;
        else if (vc.equalsIgnoreCase("LINC_RNA"))
            this.varclass = VarClass.LINC_RNA;
        else if (vc.equalsIgnoreCase("splicing"))
            this.varclass = VarClass.SPLICING;
        else if (vc.equalsIgnoreCase("polyA_site"))
            this.varclass = VarClass.POLYA_SITE;
        else {
            throw new IllegalArgumentException("Did not recognize variant class " + vc);
        }
    }


    public boolean isCoding() {
        return this.varclass != null && this.varclass == VarClass.CODING;
    }


    public boolean isNonCoding() {
        return this.varclass != null && this.varclass != VarClass.CODING;
    }


    public boolean isSpliceVariant() {
        return this.varclass != null && this.varclass == VarClass.SPLICING;
    }


    public String getRegulator() {
        return this.regulator;
    }


    public void setRegulator(String r) {
        this.regulator = r;
    }


    /**
     * Return a CSV row to represent this variant (used by Export/Export all mutations to summary file).
     */
    public String summary() {
        String gstr = "-";
        if (this.genotype == GenotypeClass.HETEROZYGOUS)
            gstr = "het";
        else if (this.genotype == GenotypeClass.HOMOZYGOUS)
            gstr = "hom";
        return String.format("chr%s\t%d\t%s\t%s\t%s", chromosome, pos, ref, alt, gstr);

    }


    /** Q/C check of this variant. */
    public boolean isValid() {
        if (this.pos < 0)
            return false;
        if (this.ref == null || this.ref.length() == 0)
            return false;
        if (this.alt == null || this.alt.length() == 0)
            return false;
        if (this.chromosome == null || this.chromosome.length() == 0)
            return false;
        if (this.build == null || this.build.length() == 0)
            return false;
        return true;
    }


    @Override
    public int hashCode() {
        int hash = 17;
        int hashMultiplikator = 17;
        hash = hashMultiplikator * hash
                + chromosome.hashCode();
        hash = hashMultiplikator * hash
                + ref.hashCode() + alt.hashCode() + pos;
        return hash;
    }


    @Override
    public boolean equals(Object ob) {
        if (ob instanceof Variant) {
            Variant other = (Variant) ob;
            if (chromosome.equals(other.chromosome)
                    && pos == other.pos
                    || ref.equals(other.ref)
                    && alt.equals(other.alt))
                return true;
        }
        return false;
    }


    public String toString() {
        String gstr = "-";
        if (this.genotype == GenotypeClass.HETEROZYGOUS)
            gstr = "het";
        else if (this.genotype == GenotypeClass.HOMOZYGOUS)
            gstr = "hom";
        return String.format("chr%s:g.%d%s>%s(build:%s,regulator:%s,genotype=%s)", chromosome, pos, ref, alt, build, regulator, gstr);
    }


    @Override
    public int compareTo(Variant other) {
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than
        // other and 0 if they are supposed to be equal
        int cmp = this.chromosome.compareTo(other.chromosome);
        if (cmp != 0)
            return cmp;
        cmp = this.pos - other.pos;
        if (cmp != 0)
            return cmp;
        cmp = this.ref.compareTo(other.ref);
        if (cmp != 0)
            return cmp;
        return this.alt.compareTo(other.alt);
    }


    /**
     * The types of variant class we are interested in for this database
     */
    public enum VarClass {
        ENHANCER("enhancer"),
        PROMOTER("promoter"),
        UTR5("5UTR"),
        UTR3("3UTR"),
        ICR("ICR"),
        MICRORNA_GENE("microRNAgene"),
        RNP_RNA("RNP_RNAcomponent"),
        SPLICING("splicing"),
        POLYA_SITE("polyA_site"),
        LINC_RNA("LINC_RNA"),
        CODING("coding");

        private String name = null;


        /** constructor sets the name variable. */
        VarClass(String n) {
            name = n;
        }


        public String toString() {
            return this.name;
        }
    }


    public enum GenotypeClass {
        HETEROZYGOUS("heterozygous"),
        HOMOZYGOUS("homozygous");

        private final String name;


        GenotypeClass(String n) {
            name = n;
        }


        public String toString() {
            return this.name;
        }
    }

}
/* eof */
