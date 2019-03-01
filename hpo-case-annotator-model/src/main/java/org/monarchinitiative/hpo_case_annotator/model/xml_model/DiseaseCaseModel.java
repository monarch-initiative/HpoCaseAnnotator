package org.monarchinitiative.hpo_case_annotator.model.xml_model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains properties which are essential for each DiseaseCaseModel. Each property is represented as a bean
 * created from JavaFX bean properties. Using JavaFX beans offers convenient way of work in event-driven environment
 * using binding. Following properties are (in my opinion) neccessary in all DiseaseCaseModel instances: <ul> <li>Genome
 * build - which reference genome's coordinates are used to represent variants</li> <li>PubMed Text - source of the
 * information, a publication</li> <li>Publication - data obtained by parsing PubMed Text</li> <li>Metadata -
 * unstandardizable useful contextual information</li> <li>Gene - entrez gene ID & gene name</li> <li>Variants - list of
 * variants which belong to this model</li> <li>Family/proband - id of the family/proband inside the publication</li>
 * <li>HPO - list of HPO terms describing phenotype of the current family/proband</li> <li>Disease - disease-related
 * information as OMIM/NCI IDs, name of disease</li> <li>Biocurator - a person who entered all this data</li> </ul>
 *
 * @author Daniel Danis
 */
public final class DiseaseCaseModel {

    /** Genome build */
    private StringProperty genomeBuild = new SimpleStringProperty(this, "genomeBuild");


    /** Publication data */
    private ObjectProperty<Publication> publication = new SimpleObjectProperty<>(this, "publication", new Publication());

    /** Metadata */
    private ObjectProperty<Metadata> metadata = new SimpleObjectProperty<>(this, "metadata", new Metadata());

    /** Gene under examination */
    private ObjectProperty<TargetGene> targetGene = new SimpleObjectProperty<>(this, "targetGene", new TargetGene());

    /** List of variants which belong to this model */
    private ObjectProperty<List<Variant>> variants = new SimpleObjectProperty<>(this, "variants", new ArrayList<>());

    /** Family/proband information */
    private ObjectProperty<FamilyInfo> familyInfo = new SimpleObjectProperty<>(this, "familyInfo", new FamilyInfo());

    /** HPO info */
    private ObjectProperty<List<HPO>> hpoList = new SimpleObjectProperty<>(this, "hpoList", new ArrayList<>());

    /** Disease-related information */
    private ObjectProperty<Disease> disease = new SimpleObjectProperty<>(this, "disease", new Disease());

    /** Biocurator */
    private ObjectProperty<Biocurator> biocurator = new SimpleObjectProperty<>(this, "biocurator", new Biocurator());


    public DiseaseCaseModel() {

    }

    public String getGenomeBuild() {
        return genomeBuild.get();
    }


    public void setGenomeBuild(String newGenomeBuild) {
        genomeBuild.set(newGenomeBuild);
    }


    public StringProperty genomeBuildProperty() {
        return genomeBuild;
    }


    public final Publication getPublication() {
        return publication.get();
    }


    public final void setPublication(Publication newPublication) {
        publication.set(newPublication);
    }


    public ObjectProperty<Publication> publicationProperty() {
        return publication;
    }


    public final Metadata getMetadata() {
        return metadata.get();
    }


    public final void setMetadata(Metadata newMetadata) {
        metadata.set(newMetadata);
    }


    public ObjectProperty<Metadata> metadataProperty() {
        return metadata;
    }


    public final TargetGene getTargetGene() {
        return targetGene.get();
    }


    public final void setTargetGene(TargetGene newTargetGene) {
        targetGene.set(newTargetGene);
    }


    public ObjectProperty<TargetGene> targetGeneProperty() {
        return targetGene;
    }


    public final List<Variant> getVariants() {
        return variants.get();
    }


    public final void setVariants(List<Variant> newVariants) {
        variants.set(newVariants);
    }


    public ObjectProperty<List<Variant>> variantsProperty() {
        return variants;
    }


    public final FamilyInfo getFamilyInfo() {
        return familyInfo.get();
    }


    public final void setFamilyInfo(FamilyInfo newFamilyInfo) {
        familyInfo.set(newFamilyInfo);
    }


    public ObjectProperty<FamilyInfo> familyInfoProperty() {
        return familyInfo;
    }


    public final List<HPO> getHpoList() {
        return hpoList.get();
    }


    public final void setHpoList(List<HPO> newSet) {
        hpoList.set(newSet);
    }


    public ObjectProperty<List<HPO>> hpoListProperty() {
        return hpoList;
    }


    public final Disease getDisease() {
        return disease.get();
    }


    public final void setDisease(Disease newDisease) {
        disease.set(newDisease);
    }


    public ObjectProperty<Disease> diseaseProperty() {
        return disease;
    }


    public final Biocurator getBiocurator() {
        return biocurator.get();
    }


    public final void setBiocurator(Biocurator newBiocurator) {
        biocurator.set(newBiocurator);
    }


    public ObjectProperty<Biocurator> biocuratorProperty() {
        return biocurator;
    }


    @Override
    public int hashCode() {
        int result = (getGenomeBuild() != null) ? getGenomeBuild().hashCode() : 0;
        result = 31 * result + getPublication().hashCode();
        result = 31 * result + getMetadata().hashCode();
        result = 31 * result + getTargetGene().hashCode();
        result = 31 * result + getVariants().hashCode();
        result = 31 * result + getFamilyInfo().hashCode();
        result = 31 * result + getHpoList().hashCode();
        result = 31 * result + getDisease().hashCode();
        result = 31 * result + getBiocurator().hashCode();
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiseaseCaseModel that = (DiseaseCaseModel) o;

        if (!getGenomeBuild().equals(that.getGenomeBuild())) return false;
        if (!getPublication().equals(that.getPublication())) return false;
        if (!getMetadata().equals(that.getMetadata())) return false;
        if (!getTargetGene().equals(that.getTargetGene())) return false;
        if (!getVariants().equals(that.getVariants())) return false;
        if (!getFamilyInfo().equals(that.getFamilyInfo())) return false;
        if (!getHpoList().equals(that.getHpoList())) return false;
        if (!getDisease().equals(that.getDisease())) return false;
        return getBiocurator().equals(that.getBiocurator());
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DiseaseCaseModel{");
        sb.append("genomeBuild=").append(genomeBuild.get());
        sb.append(", publication=").append(publication.get());
        sb.append(", metadata=").append(metadata.get());
        sb.append(", targetGene=").append(targetGene.get());
        sb.append(", variants=").append(variants.get());
        sb.append(", familyInfo=").append(familyInfo.get());
        sb.append(", hpoList=").append(hpoList.get());
        sb.append(", disease=").append(disease.get());
        sb.append(", biocurator=").append(biocurator.get());
        sb.append('}');
        return sb.toString();
    }


    /**
     * Get String usable as a filename for this model in <code><b>author-year-gene</b></code> format. Placeholders are
     * used if some of the values are missing. The returned String <b><em>doesn't</em></b> have any specific suffix
     * (e.g. .xml).
     *
     * @return String usable as with filename of this model.
     */
    public String getFileName() {
        getPublication().getFirstAuthorSurname();
        String fa = getPublication().getFirstAuthorSurname();
        String ye = (getPublication().getYear() == null) ? "year" : getPublication().getYear();
        String gn = (getTargetGene().getGeneName() == null) ? "genesymbol" : getTargetGene().getGeneName();
        return String.format("%s-%s-%s", fa, ye, gn);
    }
}
