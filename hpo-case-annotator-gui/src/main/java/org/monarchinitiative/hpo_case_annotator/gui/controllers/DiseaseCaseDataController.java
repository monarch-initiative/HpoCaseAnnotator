package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.monarchinitiative.hpo_case_annotator.core.io.PubMedParseException;
import org.monarchinitiative.hpo_case_annotator.core.io.PubMedParser;
import org.monarchinitiative.hpo_case_annotator.core.io.PubMedSummaryRetriever;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationResult;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationRunner;
import org.monarchinitiative.hpo_case_annotator.gui.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.variant.AbstractVariantController;
import org.monarchinitiative.hpo_case_annotator.gui.util.HostServicesWrapper;
import org.monarchinitiative.hpo_case_annotator.gui.util.PopUps;
import org.monarchinitiative.hpo_case_annotator.gui.util.WidthAwareTextFields;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;
import org.monarchinitiative.hpo_case_annotator.model.utils.ModelUtils;
import org.monarchinitiative.hpotextmining.gui.controller.HpoTextMining;
import org.monarchinitiative.hpotextmining.gui.controller.Main;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is a controller for all elements presented in GUI window except Menu. It manages model data.
 */
public final class DiseaseCaseDataController extends AbstractDiseaseCaseDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseCaseDataController.class);

    /**
     * Template for hyperlink pointing to PubMed entry of the publication.
     */
    private static final String PUBMED_BASE_LINK = "https://www.ncbi.nlm.nih.gov/pubmed/%s";

    private final OptionalResources optionalResources;

    private final ExecutorService executorService;

    private final GuiElementValues elementValues;

    private final ResourceBundle resourceBundle;

    private final Injector injector;

    private final ObservableList<OntologyClass> phenotypes = FXCollections.observableList(new ArrayList<>());

    private final ObservableList<AbstractVariantController> variantControllers;

    private final ObjectProperty<Publication> publication = new SimpleObjectProperty<>(this, "publication", Publication.getDefaultInstance());

    private final String appNameVersion;

    @FXML
    public Button inputPubMedDataButton;

    @FXML
    public Label phenotypeSummaryLabel;

    @FXML
    public Button seeOnPubmedButton;

    @FXML
    private Button hpoTextMiningButton;

    @FXML
    private Label currentModelLabel;

    @FXML
    private TextField inputPubMedDataTextField;

    @FXML
    private TextField pmidTextField;

    @FXML
    private Button pmidLookupButton;

    @FXML
    private TextField entrezIDTextField;

    @FXML
    private TextField geneSymbolTextField;

    /**
     * This element displays variants
     */
    @FXML
    private Accordion variantsAccordion;

    @FXML
    private ComboBox<String> diseaseDatabaseComboBox;

    @FXML
    private TextField diseaseIDTextField;

    @FXML
    private TextField diseaseNameTextField;

    @FXML
    private TextField probandFamilyTextField;

    @FXML
    private ComboBox<Sex> sexComboBox;

    @FXML
    private TextField ageTextField;

    @FXML
    private TextField biocuratorIdTextField;

    @FXML
    private TextArea metadataTextArea;


    @Inject
    public DiseaseCaseDataController(OptionalResources optionalResources, ExecutorService executorService, GuiElementValues elementValues,
                                     ResourceBundle resourceBundle, Injector injector) {
        this.optionalResources = optionalResources;
        this.executorService = executorService;
        this.elementValues = elementValues;
        this.resourceBundle = resourceBundle;
        this.injector = injector;
        this.variantControllers = FXCollections.observableArrayList();
        this.appNameVersion = injector.getInstance(Key.get(String.class, Names.named("appNameVersion")));
    }

    /**
     * @return {@link Function} for mapping {@link org.monarchinitiative.hpotextmining.gui.controller.Main.PhenotypeTerm} to
     * {@link OntologyClass}
     */
    private static Function<Main.PhenotypeTerm, OntologyClass> phenotypeTermToOntologyClass() {
        return pt -> OntologyClass.newBuilder()
                .setId(pt.getTerm().getId().getValue())
                .setLabel(pt.getTerm().getName())
                .setNotObserved(!pt.isPresent())
                .build();
    }

    /**
     * @param ontology {@link Ontology} needed for mapping
     * @return {@link Function} mapping {@link OntologyClass} to {@link Main.PhenotypeTerm} instance
     */
    private static Function<OntologyClass, Main.PhenotypeTerm> ontologyClassToPhenotypeTerm(Ontology ontology) {
        return oc -> {
            TermId id = TermId.of(oc.getId());
            Term term = ontology.getTermMap().get(id);
            return new Main.PhenotypeTerm(term, !oc.getNotObserved());
        };
    }

    /**
     * @return change listener for Phenotypes observable list that updates the {@code phenotypeSummaryLabel} with observed/excluded
     * phenotype term count
     */
    private static ListChangeListener<OntologyClass> makePhenotypeSummaryLabel(List<OntologyClass> phenotypes, Label phenotypeSummaryLabel) {
        return c -> {
            int nObserved = 0, nExcluded = 0;
            for (OntologyClass phenotype : phenotypes) {
                if (phenotype.getNotObserved()) {
                    nExcluded++;
                } else {
                    nObserved++;
                }
            }
            String observedSummary = (nObserved == 1) ? "1 observed term" : String.format("%d observed terms", nObserved);
            String excludedSummary = (nExcluded == 1) ? "1 excluded term" : String.format("%d excluded terms", nExcluded);

            phenotypeSummaryLabel.setText(String.join("\n", observedSummary, excludedSummary));
        };
    }

    /**
     * Load given <code>variant</code> collection into GUI accordion.
     *
     * @param variants {@link Collection} of {@link Variant} objects to be loaded.
     */
    private void loadVariants(Collection<Variant> variants) {
        variants.forEach(variant -> {
            try {
                loadVariant(variant);
            } catch (IOException e) {
                PopUps.showException("Error loading variant", "Error", "Unable to display variant", e);
            }
        });
    }

    /**
     * Load a single variant into the controller.
     * <p>
     * <b>IMPORTANT</b> - this is the only place where the variant should be added into the {@link DiseaseCaseDataController}!
     *
     * @param variant {@link Variant} to be loaded
     * @throws IOException if loading fails. Whoa..
     */
    private void loadVariant(Variant variant) throws IOException {
        URL location;
        final VariantValidation.Context ctx = variant.getVariantValidation().getContext();
        switch (ctx) {
            case MENDELIAN:
                location = AbstractVariantController.class.getResource("MendelianVariant.fxml");
                break;
            case SOMATIC:
                location = AbstractVariantController.class.getResource("SomaticVariant.fxml");
                break;
            case SPLICING:
                location = AbstractVariantController.class.getResource("SplicingVariant.fxml");
                break;
            default:
                LOGGER.warn("Unknown variant validation context '{}'", variant.getVariantValidation().getContext());
                return;
        }

        FXMLLoader loader = new FXMLLoader(location);
        loader.setControllerFactory(injector::getInstance);
        Parent variantRoot = loader.load();
        if (variantRoot instanceof TitledPane) { // this should be true if the top level FXML element
            final TitledPane tp = (TitledPane) variantRoot;
            variantsAccordion.getPanes().add(tp);
            AbstractVariantController controller = loader.getController();
            tp.textProperty().bind(controller.variantTitleBinding());
            controller.entrezIdProperty().bind(entrezIDTextField.textProperty());
            controller.presentData(variant);
            variantControllers.add(controller);
        } else {
            throw new IOException(String.format("Unable to display root FXML element loaded from '%s' which is not a TitledPane", location.toExternalForm()));
        }
    }

    /**
     * Load a single variant from the model
     *
     * <p>
     * <b>IMPORTANT</b> - this is the only place where the variant should be removed from the {@link DiseaseCaseDataController}!
     *
     * @param idx index of the variant to be removed
     */
    private void removeVariant(int idx) {
        final int n_accordions = variantsAccordion.getPanes().size();
        final int n_controllers = variantControllers.size();

        if (n_accordions == n_controllers && idx < n_accordions) {
            variantsAccordion.getPanes().remove(idx); // remove variant from the View
            variantControllers.remove(idx); // and controller from the controller list
        } else { // refuse to remove variant if there is an inconsistency
            PopUps.showWarningDialog("Remove variant", "Cannot remove variant", "Inconsistency was detected.\nPlease contact Daniel or Peter");
        }
    }

    /**
     * Ask user to select variant mode and add variant into model & into view.
     */
    @FXML
    private void addVariantButtonAction() {
        // Which type of variant?
        String conversationTitle = "Add new variant";

        List<String> choices = Arrays.stream(VariantValidation.Context.values())
                .filter(c -> !(c.equals(VariantValidation.Context.NO_CONTEXT) || c.equals(VariantValidation.Context.UNRECOGNIZED)))
                .map(Enum::toString)
                .collect(Collectors.toList());
        Optional<String> modeName = PopUps.getToggleChoiceFromUser(choices, "Select variant type:", conversationTitle);
        modeName.ifPresent(mn -> {
            VariantValidation.Context ctx = VariantValidation.Context.valueOf(mn);

            Variant variant = Variant.newBuilder() // we will be presenting an empty variant with specific 'ctx'
                    .setVariantValidation(VariantValidation.newBuilder()
                            .setContext(ctx)
                            .build())
                    .setVariantPosition(VariantPosition.newBuilder()
                            // we want all variants to have GRCH37 assembly by default
                            .setGenomeAssembly(GenomeAssembly.GRCH_37)
                            .build())
                    .build();

            try {
                loadVariant(variant);
            } catch (IOException e) {
                PopUps.showException("Error loading variant", "Error", "Unable to display variant", e);
            }
        });
    }

    /**
     * Remove variant represented by expanded TitledPane in variant accordion from view & model.
     */
    @FXML
    private void removeVariantAction() {
        TitledPane expanded = variantsAccordion.getExpandedPane();
        if (expanded != null) {
            final int idx = variantsAccordion.getPanes().indexOf(expanded);
            removeVariant(idx);
        } else {
            PopUps.showInfoMessage("Expand variant before removing", "Remove variant");
        }
    }

    @FXML
    private void hpoTextMiningButtonAction() {
        String conversationTitle = "HPO text mining analysis";
        try {
            URL scigraphMiningUrl = injector.getInstance(Key.get(URL.class, Names.named("scigraphMiningUrl")));
            HpoTextMining hpoTextMining = HpoTextMining.builder()
                    .withSciGraphUrl(scigraphMiningUrl)
                    .withOntology(optionalResources.getOntology())
                    .withExecutorService(executorService)
                    .withPhenotypeTerms(phenotypes.stream()
                            .map(ontologyClassToPhenotypeTerm(optionalResources.getOntology()))
                            .collect(Collectors.toSet()))
                    .build();

            Stage stage = new Stage();
            stage.initOwner(hpoTextMiningButton.getParent().getScene().getWindow());
            stage.setTitle(conversationTitle);
            stage.setScene(new Scene(hpoTextMining.getMainParent()));
            stage.showAndWait();

            phenotypes.clear();
            phenotypes.addAll(hpoTextMining.getApprovedTerms().stream()
                    .map(phenotypeTermToOntologyClass())
                    .collect(Collectors.toSet()));
        } catch (IOException e) {
            LOGGER.warn("Error occurred during text mining", e);
            PopUps.showException(conversationTitle, "Error occurred during text mining", e.getMessage(), e);
        } catch (StringIndexOutOfBoundsException sioe) {
            LOGGER.warn("Error occurred during text mining", sioe);
            PopUps.showException(conversationTitle, "Error: StringIndexOutOfBoundsException", sioe.getMessage(), sioe);
        }
    }

    /**
     * Parse inputted PubMed string and set Publication info to the model.
     */
    @FXML
    private void inputPubMedDataButtonAction() {
        String conversationTitle = "PubMed text parse";
        String pubMedText = inputPubMedDataTextField.getText();

        PubMedParser.Result result;
        try {
            result = PubMedParser.parsePubMed(pubMedText);
        } catch (PubMedParseException e) {
            PopUps.showInfoMessage(e.getMessage(), conversationTitle);
            return;
        }

        Publication temporary = Publication.newBuilder()
                .setAuthorList(result.getAuthorList())
                .setTitle(result.getTitle())
                .setJournal(result.getJournal())
                .setYear(result.getYear())
                .setVolume(result.getVolume())
                .setPages(result.getPages())
                .setPmid(result.getPmid())
                .build();

        ValidationRunner<Publication> pubMedValidator = ValidationRunner.forPubMedValidation(optionalResources.getDiseaseCaseDir());
        List<ValidationResult> results = pubMedValidator.validateSingleModel(temporary);
        if (!results.isEmpty()) { // TODO - this is doing nothing at the moment
            boolean choice = PopUps.getBooleanFromUser(
                    "Shall we continue?",
                    results.get(0).getMessage(),
                    "This publication has been already used in the project");
            if (!choice) {
                inputPubMedDataTextField.setText(null);
                return;
            }
        }

        // Ask user if he wants to create a new model after entering the new Publication to prevent
        // accidental overwriting of finished file
        if (publication.get().equals(Publication.getDefaultInstance())) { // we're setting the publication for the first time
            publication.set(temporary);
        } else {
            Optional<String> choice = PopUps.getToggleChoiceFromUser(Arrays.asList("UPDATE", "NEW"), "You entered new" +
                            " publication data. Do you wish to UPDATE current data or to start annotating a NEW case?",
                    conversationTitle);
            if (!choice.isPresent())
                return;

            if (choice.get().equals("NEW")) {
                setCurrentModelPath(null);
                presentData(DiseaseCase.getDefaultInstance());
            }
        }
        PopUps.showInfoMessage("PubMed parse OK", conversationTitle);
    }

    /**
     * Retrieve PubMed summary for given PMID.
     */
    @FXML
    private void pmidLookupButtonAction() {
        final String pmid = pmidTextField.getText();
        FutureTask<Void> task = new FutureTask<>(() -> {
            try {
                PubMedSummaryRetriever retriever = new PubMedSummaryRetriever();
                final String summary = retriever.getSummary(pmid);
                Platform.runLater(() -> inputPubMedDataTextField.setText(summary));
            } catch (IOException e) {
                Platform.runLater(() -> PopUps.showInfoMessage(String.format("Unable to retrieve PubMed summary for PMID %s", pmid), "Sorry"));
            }
            return null;
        });
        executorService.submit(task);
    }

    /**
     * Populate view elements with choices, create bindings and autocompletions.
     */
    public void initialize() {
        diseaseDatabaseComboBox.getItems().addAll(elementValues.getDiseaseDatabases());
        sexComboBox.getItems().addAll(Arrays.stream(Sex.values()).filter(s -> !s.equals(Sex.UNRECOGNIZED)).collect(Collectors.toList()));

        hpoTextMiningButton.disableProperty().bind(optionalResources.ontologyProperty().isNull());
        entrezIDTextField.disableProperty().bind(optionalResources.entrezIsMissingProperty());
        geneSymbolTextField.disableProperty().bind(optionalResources.entrezIsMissingProperty());
        diseaseDatabaseComboBox.disableProperty().bind(optionalResources.omimIsMissingProperty());
        diseaseIDTextField.disableProperty().bind(optionalResources.omimIsMissingProperty());
        diseaseNameTextField.disableProperty().bind(optionalResources.omimIsMissingProperty());

        // Trim whitespaces in input & enable PMID lookup after a valid integer has been entered into the pmidTextField
        pmidTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String in = (newValue == null) ? "" : newValue;
            if (in.matches("\\s+.*") || in.matches(".*\\s+")) { // whitespace at start or in the end
                in = in.trim();
            }
            if (in.matches("\\d+")) {
                pmidLookupButton.setDisable(false);
            } else {
                pmidLookupButton.setDisable(true);
            }
            pmidTextField.setText(in);
        });


        this.diseaseIDTextField.textProperty().addListener( // ChangeListener
                (observable, oldValue, newValue) -> {
                    String txt = diseaseIDTextField.getText();
                    txt = txt.replaceAll("\\s+", "");
                    diseaseIDTextField.setText(txt);
                });

        if (!optionalResources.getOmimIsMissing()) enableOmimAutocompletions();
        optionalResources.omimIsMissingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) enableOmimAutocompletions();
        });

        if (!optionalResources.getEntrezIsMissing()) enableEntrezAutocompletions();
        optionalResources.entrezIsMissingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) enableEntrezAutocompletions();
        });

        // Set default values to GUI fields
        presentData(DiseaseCase.newBuilder()
                // Default disease database is OMIM
                .setDisease(Disease.newBuilder()
                        .setDatabase("OMIM")
                        .build())
                .build()); // the last statement

        // generate phenotype summary text
        phenotypes.addListener(makePhenotypeSummaryLabel(phenotypes, phenotypeSummaryLabel));

        // Enable the `See on PubMed` button when publication is set
        BooleanBinding pmidIsNotSet = Bindings.createBooleanBinding(() -> publication.get().getPmid().isEmpty(), publication);
        seeOnPubmedButton.disableProperty().bind(pmidIsNotSet);
    }

    /**
     * Create autocompletions on GUI elements - allow completion of gene symbol after entering gene id and vice-versa
     */
    private void enableEntrezAutocompletions() {
        // create bindings for autocompletion of Entrez ID & Symbol
        WidthAwareTextFields.bindWidthAwareAutoCompletion(entrezIDTextField, optionalResources.getEntrezId2symbol().keySet());
        WidthAwareTextFields.bindWidthAwareAutoCompletion(geneSymbolTextField, optionalResources.getSymbol2entrezId().keySet());
        entrezIDTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.equals("")) {
                geneSymbolTextField.clear();
            } else {
                if (optionalResources.getEntrezId2symbol().containsKey(newValue)) {
                    geneSymbolTextField.setText(optionalResources.getEntrezId2symbol().get(newValue));
                }
            }
        }));
        geneSymbolTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.equals("")) {
                entrezIDTextField.clear();
            } else {
                if (optionalResources.getSymbol2entrezId().containsKey(newValue))
                    entrezIDTextField.setText(optionalResources.getSymbol2entrezId().get(newValue));
            }
        }));
    }


    /**
     * Create autocompletions on GUI elements - add suggestion boxes offering disease name based on a few typed
     * characters.
     */
    private void enableOmimAutocompletions() {
        // create bindings for autocompletion of Disease ID & name (label)
        WidthAwareTextFields.bindWidthAwareAutoCompletion(diseaseIDTextField, optionalResources.getMimid2canonicalName().keySet());
        WidthAwareTextFields.bindWidthAwareAutoCompletion(diseaseNameTextField, optionalResources.getCanonicalName2mimid().keySet());
        diseaseIDTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.equals("")) {
                diseaseNameTextField.clear();
            } else {
                if (optionalResources.getMimid2canonicalName().containsKey(newValue))
                    diseaseNameTextField.setText(optionalResources.getMimid2canonicalName().get(newValue));
            }
        }));
        diseaseNameTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.equals("")) {
                diseaseIDTextField.clear();
            } else {
                if (optionalResources.getCanonicalName2mimid().containsKey(newValue))
                    diseaseIDTextField.setText(optionalResources.getCanonicalName2mimid().get(newValue));
            }
        }));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void presentData(DiseaseCase data) {
        // Current model title, this is "readOnly" binding
        currentModelLabel.setText(data.getPublication().getTitle());

        publication.set(data.getPublication());

        // Show data on publication in text fields
        pmidTextField.setText(publication.get().getPmid());
        inputPubMedDataTextField.setText(publication.get().getTitle());

        // Gene
        entrezIDTextField.setText(String.valueOf(data.getGene().getEntrezId()));
        geneSymbolTextField.setText(data.getGene().getSymbol());

        // Phenotype terms
        phenotypes.clear();
        phenotypes.addAll(data.getPhenotypeList().stream().distinct().collect(Collectors.toList()));

        // Disease
        diseaseDatabaseComboBox.setValue(data.getDisease().getDatabase());
        diseaseIDTextField.setText(data.getDisease().getDiseaseId());
        diseaseNameTextField.setText(data.getDisease().getDiseaseName());

        // Proband & family
        probandFamilyTextField.setText(data.getFamilyInfo().getFamilyOrProbandId());
        sexComboBox.setValue(data.getFamilyInfo().getSex());
        ageTextField.setText(String.valueOf(data.getFamilyInfo().getAge()));

        // Biocurator
        biocuratorIdTextField.setText(data.getBiocurator().getBiocuratorId());

        // Metadata
        metadataTextArea.setText(data.getMetadata());

        // Delete old variants and present the new ones
        variantsAccordion.getPanes().clear();
        variantControllers.clear();
        loadVariants(data.getVariantList());
    }


    @Override
    public DiseaseCase getData() {
        Gene gene;
        if (entrezIDTextField.getText().isEmpty() || geneSymbolTextField.getText().isEmpty()) {
            gene = Gene.getDefaultInstance();
        } else {
            try {
                gene = Gene.newBuilder()
                        .setEntrezId(Integer.parseInt(entrezIDTextField.getText()))
                        .setSymbol(geneSymbolTextField.getText())
                        .build();
            } catch (NumberFormatException nfe) {
                LOGGER.warn("Not valid integer/entrez id: {}", entrezIDTextField.getText());
                gene = Gene.getDefaultInstance();
            }
        }

        Disease disease;
        if (diseaseDatabaseComboBox.getValue() == null || diseaseDatabaseComboBox.getValue().isEmpty() ||
                diseaseIDTextField.getText().isEmpty() || diseaseNameTextField.getText().isEmpty()) {
            disease = Disease.getDefaultInstance();
        } else {
            disease = Disease.newBuilder()
                    .setDatabase(diseaseDatabaseComboBox.getValue())
                    .setDiseaseId(diseaseIDTextField.getText())
                    .setDiseaseName(diseaseNameTextField.getText())
                    .build();
        }

        FamilyInfo familyInfo;
        if (probandFamilyTextField.getText().isEmpty() || ageTextField.getText().isEmpty()
                || sexComboBox.getValue() == null) {
            familyInfo = FamilyInfo.getDefaultInstance();
        } else {
            familyInfo = FamilyInfo.newBuilder()
                    .setAge(ageTextField.getText())
                    .setFamilyOrProbandId(probandFamilyTextField.getText())
                    .setSex(sexComboBox.getValue())
                    .build();
        }

        return DiseaseCase.newBuilder()
                .setPublication(publication.get())
                .setGene(gene)
                .addAllVariant(variantControllers.stream()
                        .map(AbstractVariantController::getData)
                        .collect(Collectors.toList()))
                .addAllPhenotype(phenotypes)
                .setDisease(disease)
                .setFamilyInfo(familyInfo)
                .setBiocurator(Biocurator.newBuilder()
                        .setBiocuratorId(optionalResources.getBiocuratorId())
                        .build())
                .setMetadata(metadataTextArea.getText())
                .setSoftwareVersion(appNameVersion)
                .build();
    }


    @Override
    Binding<String> diseaseCaseTitleBinding() {
        return Bindings.createStringBinding(() -> {
                    if (isComplete()) {
                        return ModelUtils.getFileNameWithSampleId(getData());
                    } else {
                        return String.format("Data INCOMPLETE: %s", validationResults.get(0).getMessage());
                    }
                },
                getObservableDiseaseCaseDependencies().toArray(new Observable[0]));
    }


    @Override
    List<? extends Observable> getObservableDiseaseCaseDependencies() {
        return Arrays.asList(publication, entrezIDTextField.textProperty(), geneSymbolTextField.textProperty(),
                diseaseDatabaseComboBox.valueProperty(), diseaseNameTextField.textProperty(), diseaseIDTextField.textProperty(),
                probandFamilyTextField.textProperty(), sexComboBox.valueProperty(), ageTextField.textProperty(), variantControllers);
    }

    @FXML
    public void seeOnPubmedButtonAction() {
        HostServicesWrapper hsw = injector.getInstance(HostServicesWrapper.class);
        final String publicationUrl = String.format(PUBMED_BASE_LINK, publication.get().getPmid());
        hsw.showDocument(publicationUrl);
    }
}
