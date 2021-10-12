package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.monarchinitiative.hpo_case_annotator.gui.util.HostServicesWrapper;
import org.monarchinitiative.hpo_case_annotator.io.ModelUtils;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is the controller for dialog that presents attributes of curated publications in table. Individual cases
 * are presented as rows and attributes such as author, gene, variant are presented as columns. <p>Collection with
 * {@link DiseaseCase} instances to be presented is supposed to be entered using {@link
 * ShowPublicationsController#setData(Collection)} method. The content of table is read-only.
 */
public final class ShowPublicationsController {

    /**
     * Template for hyperlink pointing to PubMed entry of the publication.
     */
    private static final String PUBMED_BASE_LINK = "https://www.ncbi.nlm.nih.gov/pubmed/%s";

    /**
     * Allows to open hyperlink in OS-dependent default web browser.
     */
    private final HostServicesWrapper hostServices;

    private Set<DiseaseCase> model_cache = new HashSet<>();

    @FXML
    private TableView<DiseaseCase> publicationsTableView;

    /**
     * ############################ COLUMNS OF THE TABLEVIEW #######################################################
     */
    @FXML
    private TableColumn<DiseaseCase, String> firstAuthorTableColumn;

    @FXML
    private TableColumn<DiseaseCase, String> titleTableColumn;

    @FXML
    private TableColumn<DiseaseCase, String> journalTableColumn;

    @FXML
    private TableColumn<DiseaseCase, String> yearTableColumn;

    @FXML
    private TableColumn<DiseaseCase, String> probandTableColumn;

    @FXML
    private TableColumn<DiseaseCase, String> pmidTableColumn;

    @FXML
    private TableColumn<DiseaseCase, String> geneTableColumn;

    @FXML
    private TableColumn<DiseaseCase, String> variantsTableColumn;

    @FXML
    private TableColumn<DiseaseCase, String> variantClassTableColumn;

    @FXML
    private TableColumn<DiseaseCase, Hyperlink> pubMedTableColumn;


    ShowPublicationsController(HostServicesWrapper hostServices) {
        this.hostServices = hostServices;
    }


    /**
     * Crunch variants present in the current {@link DiseaseCase} into string representation that will be
     * presented to user.
     *
     * @param model {@link DiseaseCase} instance being presented in the table row
     * @return String representation of variants present in the model. E.g. chr1:12345G>C,chr3:4321C>G
     */
    private static String crunchVariants(DiseaseCase model) {
        return model.getVariantList().stream()
                .map(v -> String.format("%s %s:%d%s>%s", v.getVariantPosition().getGenomeAssembly(), v.getVariantPosition().getContig(),
                        v.getVariantPosition().getPos(), v.getVariantPosition().getRefAllele(), v.getVariantPosition().getAltAllele()))
                .collect(Collectors.joining(","));
    }


    /**
     * Crunch data present in the <em>variant class</em> attribute of variants in the current {@link DiseaseCase}
     * into string representation that will be presented to user in <em>Variant class</em> column of this table.
     *
     * @param model {@link DiseaseCase} instance being presented in the table row
     * @return String obtained by iteration through variants and concatenation of the values. E.g splicing,coding
     */
    private static String crunchVClasses(DiseaseCase model) {
        return model.getVariantList().stream()
                .map(Variant::getVariantClass)
                .collect(Collectors.joining(","));
    }


    /**
     * Initialize graphical elements of the controller
     */
    public void initialize() {
        firstAuthorTableColumn.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(ModelUtils.getFirstAuthorsSurname(cdf.getValue().getPublication().getAuthorList())));
        titleTableColumn.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getPublication().getTitle()));
        journalTableColumn.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getPublication().getJournal()));
        yearTableColumn.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getPublication().getYear()));
        probandTableColumn.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getFamilyInfo().getFamilyOrProbandId()));
        pmidTableColumn.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getPublication().getPmid()));
        geneTableColumn.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getGene().getSymbol()));
        variantsTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(crunchVariants(cdf.getValue())));

        variantClassTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(crunchVClasses(cdf.getValue())));

        pubMedTableColumn.setCellValueFactory(cdf -> {
            Hyperlink hyper = new Hyperlink(String.format(PUBMED_BASE_LINK, cdf.getValue().getPublication().getPmid()));
            hyper.setOnAction(e -> hostServices.showDocument(hyper.getText()));
            return new ReadOnlyObjectWrapper<>(hyper);
        });
        if (model_cache.size() > 0) {
            publicationsTableView.getItems().addAll(model_cache);
        }

        firstAuthorTableColumn.setSortType(TableColumn.SortType.ASCENDING);
        titleTableColumn.setSortType(TableColumn.SortType.ASCENDING);
        publicationsTableView.getSortOrder().add(firstAuthorTableColumn);
        publicationsTableView.getSortOrder().add(titleTableColumn);
    }


    /**
     * Entry point for collection of data that will be presented by this dialog.
     *
     * @param models {@link Collection} containing {@link DiseaseCase} instances.
     */
    public void setData(Collection<DiseaseCase> models) {
        if (publicationsTableView == null) { // controller hasn't been processed by FXMLLoader yet.
            model_cache.addAll(models);
        } else {
            publicationsTableView.getItems().clear();
            publicationsTableView.getItems().addAll(models);
            publicationsTableView.sort();
        }
    }
}
