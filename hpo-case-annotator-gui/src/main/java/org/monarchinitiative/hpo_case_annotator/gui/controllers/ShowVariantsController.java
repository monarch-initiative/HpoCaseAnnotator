package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import com.google.inject.Inject;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.monarchinitiative.hpo_case_annotator.gui.util.HostServicesWrapper;
import org.monarchinitiative.hpo_case_annotator.model.ModelUtils;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantPosition;

import java.util.*;

public class ShowVariantsController {

    /**
     * Template for hyperlink pointing to PubMed entry of the publication.
     */
    private static final String PUBMED_BASE_LINK = "https://www.ncbi.nlm.nih.gov/pubmed/%s";

    /**
     * Allows to open hyperlink in OS-dependent default web browser.
     */
    private final HostServicesWrapper hostServices;
    @FXML
    public TableView<VariantData> variantsTableView;
    @FXML
    public TableColumn<VariantData, String> variantTableColumn;
    @FXML
    public TableColumn<VariantData, String> publicationTableColumn;
    @FXML
    public TableColumn<VariantData, Hyperlink> pmidTableColumn;
    @FXML
    public TableColumn<VariantData, String> geneTableColumn;
    @FXML
    public TableColumn<VariantData, String> variantClassTableColumn;
    @FXML
    public TableColumn<VariantData, String> pathomechanismTableColumn;
    @FXML
    public TableColumn<VariantData, String> consequenceTableColumn;

    private Set<DiseaseCase> modelCache = new HashSet<>();

    @Inject
    public ShowVariantsController(HostServicesWrapper hostServices) {
        this.hostServices = hostServices;
    }

    private static Collection<VariantData> meltDiseaseCases(Collection<DiseaseCase> cases) {
        List<VariantData> variantData = new ArrayList<>();
        for (DiseaseCase diseaseCase : cases) {
            for (Variant variant : diseaseCase.getVariantList()) {
                VariantPosition vp = variant.getVariantPosition();
                variantData.add(new VariantData(
                        String.format("%s %s:%d%s>%s", vp.getGenomeAssembly(), vp.getContig(), vp.getPos(), vp.getRefAllele(), vp.getAltAllele()),
                        ModelUtils.getFileNameWithSampleId(diseaseCase),
                        diseaseCase.getPublication().getPmid(),
                        diseaseCase.getGene().getSymbol(),
                        variant.getVariantClass(),
                        variant.getPathomechanism(),
                        variant.getConsequence()
                ));
            }
        }
        return variantData;
    }

    public void initialize() {
        variantTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getVariantSummary()));
        publicationTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getPublication()));
        pmidTableColumn.setCellValueFactory(cdf -> {
            Hyperlink hyperlink = new Hyperlink(cdf.getValue().getPmid());
            hyperlink.setOnAction(e -> hostServices.showDocument(String.format(PUBMED_BASE_LINK, hyperlink.getText())));
            return new ReadOnlyObjectWrapper<>(hyperlink);
        });
        geneTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getGene()));
        variantClassTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getVariantClass()));
        pathomechanismTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getPathomechanism()));
        consequenceTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getConsequence()));

        if (modelCache.size() > 0) {
            variantsTableView.getItems().addAll(meltDiseaseCases(modelCache));
        }
    }

    public void setData(Collection<DiseaseCase> cases) {
        if (variantsTableView == null) { // controller hasn't been processed by FXMLLoader yet.
            modelCache.addAll(cases);
        } else {
            variantsTableView.getItems().clear();
            variantsTableView.getItems().addAll(meltDiseaseCases(cases));
            variantsTableView.sort();
        }
    }

    public Collection<VariantData> getVariantData() {
        return new ArrayList<>(variantsTableView.getItems());
    }

    static class VariantData {
        private final String variantSummary;

        private final String publication;

        private final String pmid;

        private final String gene;

        private final String variantClass;
        private final String pathomechanism;
        private final String consequence;

        VariantData(String variantSummary,
                    String publication,
                    String pmid,
                    String gene,
                    String variantClass,
                    String pathomechanism,
                    String consequence) {
            this.variantSummary = variantSummary;
            this.publication = publication;
            this.pmid = pmid;
            this.gene = gene;
            this.variantClass = variantClass;
            this.pathomechanism = pathomechanism;
            this.consequence = consequence;
        }

        public String getVariantSummary() {
            return variantSummary;
        }

        public String getPublication() {
            return publication;
        }

        public String getPmid() {
            return pmid;
        }

        public String getGene() {
            return gene;
        }

        public String getVariantClass() {
            return variantClass;
        }

        public String getPathomechanism() {
            return pathomechanism;
        }

        public String getConsequence() {
            return consequence;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VariantData that = (VariantData) o;
            return Objects.equals(variantSummary, that.variantSummary) &&
                    Objects.equals(publication, that.publication) &&
                    Objects.equals(pmid, that.pmid) &&
                    Objects.equals(gene, that.gene) &&
                    Objects.equals(variantClass, that.variantClass) &&
                    Objects.equals(pathomechanism, that.pathomechanism) &&
                    Objects.equals(consequence, that.consequence);
        }

        @Override
        public int hashCode() {
            return Objects.hash(variantSummary, publication, pmid, gene, variantClass, pathomechanism, consequence);
        }

        @Override
        public String toString() {
            return "VariantData{" +
                    "variantSummary='" + variantSummary + '\'' +
                    ", publication='" + publication + '\'' +
                    ", pmid='" + pmid + '\'' +
                    ", gene='" + gene + '\'' +
                    ", variantClass='" + variantClass + '\'' +
                    ", pathomechanism='" + pathomechanism + '\'' +
                    ", consequence='" + consequence + '\'' +
                    '}';
        }
    }
}
