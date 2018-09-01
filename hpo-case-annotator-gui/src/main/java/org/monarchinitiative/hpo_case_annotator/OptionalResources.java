package org.monarchinitiative.hpo_case_annotator;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermContainer;
import org.monarchinitiative.hpo_case_annotator.model.TargetGene;
import org.monarchinitiative.hpo_case_annotator.refgenome.GenomeAssembly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

/**
 * This class is a POJO holding paths to HRMD resources. Created by Daniel Danis on 7/16/17.
 */
public final class OptionalResources {

    /**
     * Use this name to save HP.obo file on the local filesystem.
     */
    public static final String DEFAULT_HPO_FILE_NAME = "HP.obo";

    /**
     * Use this name to save the Entrez gene file on the local filesystem.
     */
    public static final String DEFAULT_ENTREZ_FILE_NAME = "Homo_sapiens.gene_info.gz";

    public static final String DISEASE_CASE_DIR_PROPERTY = "disease.case.dir";

    public static final String BIOCURATOR_ID_PROPERTY = "biocurator.id";

    public static final String ONTOLOGY_PATH_PROPERTY = "hp.obo.path";

    public static final String ENTREZ_GENE_PATH_PROPERTY = "entrez.gene.path";

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionalResources.class);

    private final BooleanBinding entrezIsMissing;

    private final BooleanBinding omimIsMissing;

    private final ObjectProperty<File> diseaseCaseDir = new SimpleObjectProperty<>(this, "diseaseCaseDir");

    // default value does not harm here
    private final StringProperty biocuratorId = new SimpleStringProperty(this, "biocuratorId", "");

    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology");

    private final ObjectProperty<Map<Integer, TargetGene>> entrezId2gene = new SimpleObjectProperty<>(this, "entrezId2gene");

    private final ObjectProperty<Map<String, String>> entrezId2symbol = new SimpleObjectProperty<>(this, "entrezId2symbol");

    private final ObjectProperty<Map<String, String>> symbol2entrezId = new SimpleObjectProperty<>(this,
            "symbol2entrezId");

    private final ObjectProperty<Map<String, String>> mimid2canonicalName = new SimpleObjectProperty<>(this,
            "mimid2canonicalName");

    private final ObjectProperty<Map<String, String>> canonicalName2mimid = new SimpleObjectProperty<>(this,
            "canonicalName2mimid");

    private File ontologyPath;

    private File entrezPath;

    public OptionalResources() {
        this.entrezIsMissing = Bindings.createBooleanBinding(() -> Stream.of(entrezId2geneProperty(),
                entrezId2symbolProperty(), symbol2entrezIdProperty()).anyMatch(op -> op.get() == null),
                entrezId2geneProperty(), entrezId2symbolProperty(), symbol2entrezIdProperty());
        this.omimIsMissing = Bindings.createBooleanBinding(() -> Stream.of(mimid2canonicalNameProperty(),
                canonicalName2mimidProperty()).anyMatch(op -> op.get() == null),
                mimid2canonicalNameProperty(), canonicalName2mimidProperty());
    }


    public static Ontology deserializeOntology(File ontologyPath) {
        try {
            OBOParser parser = new OBOParser(new OBOParserFileInput(ontologyPath.getAbsolutePath()), OBOParser
                    .PARSE_DEFINITIONS);
            LOGGER.info(parser.doParse());
            TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser
                    .getDate());
            return Ontology.create(termContainer);
        } catch (IOException | OBOParserException e) {
            LOGGER.warn("Error occured during deserialization of obo file at {}", ontologyPath, e);
            e.printStackTrace();
        }
        return null;
    }


    public File getOntologyPath() {
        return ontologyPath;
    }


    public void setOntologyPath(File ontologyPath) {
        this.ontologyPath = ontologyPath;
    }


    public File getEntrezPath() {
        return entrezPath;
    }


    public void setEntrezPath(File entrezPath) {
        this.entrezPath = entrezPath;
    }


    public Boolean getOmimIsMissing() {
        return omimIsMissing.get();
    }


    public BooleanBinding omimIsMissingProperty() {
        return omimIsMissing;
    }


    public Map<String, String> getMimid2canonicalName() {
        return mimid2canonicalName.get();
    }


    public void setMimid2canonicalName(Map<String, String> mimid2canonicalName) {
        this.mimid2canonicalName.set(mimid2canonicalName);
    }


    public ObjectProperty<Map<String, String>> mimid2canonicalNameProperty() {
        return mimid2canonicalName;
    }


    public Map<String, String> getCanonicalName2mimid() {
        return canonicalName2mimid.get();
    }


    public void setCanonicalName2mimid(Map<String, String> canonicalName2mimid) {
        this.canonicalName2mimid.set(canonicalName2mimid);
    }


    public ObjectProperty<Map<String, String>> canonicalName2mimidProperty() {
        return canonicalName2mimid;
    }


    public Boolean getEntrezIsMissing() {
        return entrezIsMissing.get();
    }


    public BooleanBinding entrezIsMissingProperty() {
        return entrezIsMissing;
    }


    public Map<Integer, TargetGene> getEntrezId2gene() {
        return entrezId2gene.get();
    }


    public void setEntrezId2gene(Map<Integer, TargetGene> entrezId2gene) {
        this.entrezId2gene.set(entrezId2gene);
    }


    public ObjectProperty<Map<Integer, TargetGene>> entrezId2geneProperty() {
        return entrezId2gene;
    }


    public Map<String, String> getEntrezId2symbol() {
        return entrezId2symbol.get();
    }


    public void setEntrezId2symbol(Map<String, String> entrezId2symbol) {
        this.entrezId2symbol.set(entrezId2symbol);
    }


    public ObjectProperty<Map<String, String>> entrezId2symbolProperty() {
        return entrezId2symbol;
    }


    public File getDiseaseCaseDir() {
        return diseaseCaseDir.get();
    }


    public void setDiseaseCaseDir(File diseaseCaseDir) {
        this.diseaseCaseDir.set(diseaseCaseDir);
    }


    public ObjectProperty<File> diseaseCaseDirProperty() {
        return diseaseCaseDir;
    }


    public String getBiocuratorId() {
        return biocuratorId.get();
    }


    public void setBiocuratorId(String biocuratorId) {
        this.biocuratorId.set(biocuratorId);
    }


    public StringProperty biocuratorIdProperty() {
        return biocuratorId;
    }


    public Ontology getOntology() {
        return ontology.get();
    }


    public void setOntology(Ontology ontology) {
        this.ontology.set(ontology);
    }


    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }


    public Map<String, String> getSymbol2entrezId() {
        return symbol2entrezId.get();
    }


    public void setSymbol2entrezId(Map<String, String> symbol2entrezId) {
        this.symbol2entrezId.set(symbol2entrezId);
    }


    public ObjectProperty<Map<String, String>> symbol2entrezIdProperty() {
        return symbol2entrezId;
    }

}
