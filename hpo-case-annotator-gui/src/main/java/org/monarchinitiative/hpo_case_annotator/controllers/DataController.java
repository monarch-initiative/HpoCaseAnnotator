package org.monarchinitiative.hpo_case_annotator.controllers;

import com.google.inject.Injector;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.monarchinitiative.hpo_case_annotator.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.controllers.variant.AbstractVariantController;
import org.monarchinitiative.hpo_case_annotator.controllers.variant.MendelianVariantController;
import org.monarchinitiative.hpo_case_annotator.controllers.variant.SomaticVariantController;
import org.monarchinitiative.hpo_case_annotator.controllers.variant.SplicingVariantController;
import org.monarchinitiative.hpo_case_annotator.hpotextmining.controllers.Main;
import org.monarchinitiative.hpo_case_annotator.io.PubMedParseException;
import org.monarchinitiative.hpo_case_annotator.io.PubMedParser;
import org.monarchinitiative.hpo_case_annotator.io.RetrievePubMedSummary;
import org.monarchinitiative.hpo_case_annotator.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.ChoiceBasket;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;
import org.monarchinitiative.hpo_case_annotator.util.PopUps;
import org.monarchinitiative.hpo_case_annotator.util.WidthAwareTextFields;
import org.monarchinitiative.hpo_case_annotator.validation.PubMedValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is a controller for all elements presented in GUI window except Menu. It manages model data.
 */
public final class DataController implements DiseaseCaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataController.class);

    private final OptionalResources optionalResources;

    private final ExecutorService executorService;

    private final ChoiceBasket choiceBasket;

    private final ResourceBundle resourceBundle;

    private final Injector injector;

    private final List<OntologyClass> phenotypes = new ArrayList<>();

    @FXML
    public Button hpoTextMiningButton;

    private Publication publication = Publication.getDefaultInstance();

    @FXML
    private Label currentModelLabel;

    @FXML
    private ComboBox<String> genomeBuildComboBox;

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

    /**
     * Keep track to path of file containing data of current model so we don't need to ask user where to save a model
     * everytime a change has been made.
     */
    private File currentModelPath;


    private static String convertGenomeBuild(DiseaseCaseModel model) {
        switch (model.getGenomeBuild().toLowerCase()) {
            case "grch37":
            case "hg19":
                return "hg19";
            case "grch38":
            case "hg38":
                return "hg38";
            default:
                LOGGER.warn("Unknown genome build '{}' in model '{}'", model.getGenomeBuild(), model.getFileName());
                return model.getGenomeBuild();
        }
    }


    @Inject
    public DataController(OptionalResources optionalResources, ExecutorService executorService, ChoiceBasket choiceBasket,
                          ResourceBundle resourceBundle, Injector injector) {
        this.optionalResources = optionalResources;
        this.executorService = executorService;
        this.choiceBasket = choiceBasket;
        this.resourceBundle = resourceBundle;
        this.injector = injector;
    }


    /**
     * Get {@link TitledPane} appropriate for displaying a {@link Variant} with given {@link VariantValidation.Context}
     * <code>ctx</code>.
     *
     * @param ctx {@link VariantValidation.Context}
     * @return @link BaseVariantController subclass, which is also subclass of {@link TitledPane} and can be displayed
     * as a content within this {@link DataController}.
     */
    private AbstractVariantController getVariantController(VariantValidation.Context ctx) throws IOException {
        switch (ctx) {
            case MENDELIAN:
                return new MendelianVariantController(choiceBasket);
            case SPLICING:
                return new SplicingVariantController(choiceBasket);
            case SOMATIC:
                return new SomaticVariantController(choiceBasket);
            default:
                LOGGER.error("Unknown variant validation context {}", ctx);
                throw new IOException();
        }
    }


    private AbstractVariantController getVariantController(Variant variant) throws IOException {
        AbstractVariantController controller = getVariantController(variant.getVariantValidation().getContext());
        controller.presentVariant(variant);
        return controller;
    }


    /**
     * Get path to XML file corresponding to current model.
     *
     * @return {@link File} containing the path.
     */
    public File getCurrentModelPath() {
        return currentModelPath;
    }


    /**
     * Set path to XML file corresponding to current model.
     *
     * @param currentModelPath {@link File} containing the path.
     */
    public void setCurrentModelPath(File currentModelPath) {
        this.currentModelPath = currentModelPath;
    }


    /**
     * Load variant data into GUI accordion.
     *
     * @param variants {@link Collection} of {@link Variant} objects to be loaded.
     */
    private void loadVariants(Collection<Variant> variants) {
        variantsAccordion.getPanes().clear();
        variants.forEach(variant -> {
            try {
                // variant controller is also a TitledPane
                variantsAccordion.getPanes()
                        .add(getVariantController(variant));
            } catch (IOException e) {
                PopUps.showException("Error loading variant", "Error", "Unable to display variant", e);
            }
        });
    }


    /**
     * Ask user to select variant mode and add variant into model & into view.
     */
    @FXML
    void addVariantButtonAction() {
        // Which type of variant?
        String conversationTitle = "Add new variant";

        List<String> choices = Arrays.stream(VariantValidation.Context.values())
                .filter(c -> !(c.equals(VariantValidation.Context.NA) || c.equals(VariantValidation.Context.UNRECOGNIZED)))
                .map(Enum::toString)
                .collect(Collectors.toList());
        Optional<String> modeName = PopUps.getToggleChoiceFromUser(choices, "Select variant type:", conversationTitle);
        modeName.ifPresent(mn -> {
            VariantValidation.Context ctx = VariantValidation.Context.valueOf(mn);

            // Pass reference to new Variant into Model and into View
            try {
                TitledPane pane = getVariantController(ctx);
                variantsAccordion.getPanes().add(pane);
            } catch (IOException e) {
                PopUps.showException("Error loading variant", "Error", "Unable to display variant", e);
            }
        });
    }


    /**
     * Remove variant represented by expanded TitledPane in variant accordion from view & model.
     */
    @FXML
    void removeVariantAction() {
        TitledPane expanded = variantsAccordion.getExpandedPane();
        if (expanded != null)
            variantsAccordion.getPanes().remove(expanded); // remove variant from the View
    }


    @FXML
    void hpoTextMiningButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Main.fxml"), resourceBundle,
                    new JavaFXBuilderFactory(), injector::getInstance);
            Parent parent = loader.load();
            Main main = loader.getController();
            main.setPhenotypeTerms(phenotypes.stream()
                    .map(hpo ->
                            new Main.PhenotypeTerm(optionalResources.getOntology().getTerm(hpo.getId()),
                                    !hpo.getNotObserved()))
                    .collect(Collectors.toSet()));
            Stage stage = new Stage();
            stage.initOwner(hpoTextMiningButton.getParent().getScene().getWindow());
            stage.setScene(new Scene(parent));
            stage.showAndWait();
            // wait until user finishes
            phenotypes.clear();
            phenotypes.addAll(main.getPhenotypeTerms().stream()
                    .map(term -> OntologyClass.newBuilder()
                            .setId(term.getHpoId())
                            .setLabel(term.getName())
                            .setNotObserved(!term.isPresent())
                            .build())
                    .collect(Collectors.toSet()));
        } catch (MalformedURLException e) {
            LOGGER.warn("HPO text mining url is in wrong format", e);
        } catch (IOException e) {
            LOGGER.warn("Unable to perform text mining", e);
        }

    }


    /**
     * Parse inputted PubMed string and set Publication info to the model.
     */
    @FXML
    void inputPubMedDataButton() {
        String conversationTitle = "PubMed text parse";
        String pubMedText = inputPubMedDataTextField.getText();

        PubMedParser.Result result;
        try {
            result = PubMedParser.parsePubMed(pubMedText);
        } catch (PubMedParseException e) {
            PopUps.showInfoMessage(e.getMessage(), conversationTitle);
            return;
        }


        PubMedValidator validator = new PubMedValidator(new XMLModelParser(optionalResources.getDiseaseCaseDir()));
        if (validator.seenThisPMIDBefore(result.getPmid())) {
            boolean choice = PopUps.getBooleanFromUser(
                    "Shall we continue?",
                    "This publication has been already used in this project.",
                    conversationTitle);
            if (!choice) {
                inputPubMedDataTextField.setText(null);
                return;
            }
        }

        // Ask user if he wants to create a new model after entering the new Publication to prevent
        // accidental overwriting of finished file
        if (publication.equals(Publication.getDefaultInstance())) { // we're setting the publication for the first time
            publication = Publication.newBuilder()
                    .setAuthorList(result.getAuthorList())
                    .setTitle(result.getTitle())
                    .setJournal(result.getJournal())
                    .setYear(result.getYear())
                    .setVolume(result.getVolume())
                    .setPages(result.getPages())
                    .setPmid(result.getPmid())
                    .build();
        } else {
            Optional<String> choice = PopUps.getToggleChoiceFromUser(Arrays.asList("UPDATE", "NEW"), "You entered new" +
                            " publication data. Do you wish to UPDATE current data or to start annotating a NEW case?",
                    conversationTitle);
            if (!choice.isPresent())
                return;

            if (choice.get().equals("NEW")) {
                setCurrentModelPath(null);
                presentCase(DiseaseCase.getDefaultInstance());
            }
        }
        PopUps.showInfoMessage("PubMed parse OK", conversationTitle);
    }


    /**
     * Retrieve PubMed summary for given PMID.
     */
    @FXML
    void pmidLookupButtonAction() {
        final String pmid = pmidTextField.getText();
        FutureTask<Void> task = new FutureTask<>(() -> {
            Optional<String> optional = RetrievePubMedSummary.getSummary(pmid);
            if (optional.isPresent()) {
                Platform.runLater(() -> inputPubMedDataTextField.setText(optional.get()));
                return null;
            }
            Platform.runLater(() -> PopUps.showInfoMessage(String.format("Unable to retrieve PubMed summary for PMID %s", pmid), "Sorry"));
            return null;
        });
        executorService.submit(task);
    }


    /**
     * Populate view elements with choices, create bindings and autocompletions.
     */
    public void initialize() {
        genomeBuildComboBox.getItems().addAll(choiceBasket.getGenomeBuild());
        diseaseDatabaseComboBox.getItems().addAll(choiceBasket.getDiseaseDatabases());
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

        if (!optionalResources.getOmimIsMissing()) enableOmimAutocompletions();
        optionalResources.omimIsMissingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) enableOmimAutocompletions();
        });

        if (!optionalResources.getEntrezIsMissing()) enableEntrezAutocompletions();
        optionalResources.entrezIsMissingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) enableEntrezAutocompletions();
        });
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
    public void presentCase(DiseaseCase diseaseCase) {
        // Current model title, this is "readOnly" binding
        currentModelLabel.setText(diseaseCase.getPublication().getTitle());

        // Genome build
        // model.setGenomeBuild(convertGenomeBuild(model));
        genomeBuildComboBox.setValue(diseaseCase.getGenomeBuild());

        publication = diseaseCase.getPublication();

        // Gene
        entrezIDTextField.setText(String.valueOf(diseaseCase.getGene().getEntrezId()));
        geneSymbolTextField.setText(diseaseCase.getGene().getSymbol());

        // Disease
        diseaseDatabaseComboBox.setValue(diseaseCase.getDisease().getDatabase());
        diseaseIDTextField.setText(diseaseCase.getDisease().getDiseaseId());
        diseaseNameTextField.setText(diseaseCase.getDisease().getDiseaseName());

        // Proband & family
        probandFamilyTextField.setText(diseaseCase.getFamilyInfo().getFamilyOrProbandId());
        sexComboBox.setValue(diseaseCase.getFamilyInfo().getSex());
        ageTextField.setText(String.valueOf(diseaseCase.getFamilyInfo().getAge()));

        // Biocurator
        biocuratorIdTextField.setText(diseaseCase.getBiocurator().getBiocuratorId());

        // Metadata
        metadataTextArea.setText(diseaseCase.getMetadata());

        loadVariants(diseaseCase.getVariantList());
    }


    @Override
    public DiseaseCase getCase() {
        return DiseaseCase.newBuilder()
                .setGenomeBuild(genomeBuildComboBox.getValue())
                .setPublication(publication)
                .setMetadata(metadataTextArea.getText())
                .setGene(Gene.newBuilder()
                        .setEntrezId(Integer.parseInt(entrezIDTextField.getText())) // TODO - make sure that the field contains parsable integer
                        .setSymbol(geneSymbolTextField.getText())
                        .build())
                .setDisease(Disease.newBuilder()
                        .setDatabase(diseaseDatabaseComboBox.getValue())
                        .setDiseaseId(diseaseIDTextField.getText())
                        .setDiseaseName(diseaseNameTextField.getText())
                        .build())
                .addAllPhenotype(phenotypes)
                // family
                .setFamilyInfo(FamilyInfo.newBuilder()
                        .setFamilyOrProbandId(probandFamilyTextField.getText())
                        .build())
                .setBiocurator(Biocurator.newBuilder()
                        .setBiocuratorId(biocuratorIdTextField.getText())
                        .build())
                // variants
                .addAllVariant(variantsAccordion.getPanes().stream().map(variantControllerToVariant()).collect(Collectors.toList()))
                .build();

    }


    private Function<TitledPane, Variant> variantControllerToVariant() {
        return tp -> tp instanceof AbstractVariantController
                ? ((AbstractVariantController) tp).getVariant()
                : null;
    }
}
