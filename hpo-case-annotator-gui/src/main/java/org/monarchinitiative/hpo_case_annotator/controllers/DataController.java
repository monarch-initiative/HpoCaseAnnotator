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
import org.monarchinitiative.hpo_case_annotator.controllers.variant.MendelianVariantController;
import org.monarchinitiative.hpo_case_annotator.controllers.variant.SomaticVariantController;
import org.monarchinitiative.hpo_case_annotator.controllers.variant.SplicingVariantController;
import org.monarchinitiative.hpo_case_annotator.hpotextmining.controllers.Main;
import org.monarchinitiative.hpo_case_annotator.io.PubMedParser;
import org.monarchinitiative.hpo_case_annotator.io.RetrievePubMedSummary;
import org.monarchinitiative.hpo_case_annotator.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.*;
import org.monarchinitiative.hpo_case_annotator.util.PopUps;
import org.monarchinitiative.hpo_case_annotator.util.WidthAwareTextFields;
import org.monarchinitiative.hpo_case_annotator.validation.PubMedValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

/**
 * This class is a controller for all elements presented in GUI window except Menu. It manages model data.
 */
public final class DataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataController.class);

    private final OptionalResources optionalResources;

    private final ExecutorService executorService;

    private final ChoiceBasket choiceBasket;

    private final ResourceBundle resourceBundle;

    private final Injector injector;

    @FXML
    public Button hpoTextMiningButton;

    /**
     * The model class that is displayed in the elements of this controller.
     */
    private DiseaseCaseModel model = new DiseaseCaseModel();

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
    private ComboBox<String> sexComboBox;

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
     * Determine {@link VariantMode} of given {@link Variant} .
     *
     * @param variant {@link Variant} instance to be analyzed.
     * @return {@link VariantMode} corresponding to subclass of given variant instance.
     */
    private static VariantMode getVariantMode(Variant variant) {
        if (variant instanceof MendelianVariant) {
            return VariantMode.MENDELIAN;
        }
        if (variant instanceof SplicingVariant) {
            return VariantMode.SPLICING;
        }
        if (variant instanceof SomaticVariant) {
            return VariantMode.SOMATIC;
        }
        throw new IllegalArgumentException("ERROR: Variant of unknown type");
    }


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


    /**
     * Determine subclass of given {@link Variant}, perform casting and create appropriate @link BaseVariantController
     * subclass.
     *
     * @param mode    {@link VariantMode} to cast {@link Variant} object into.
     * @param variant {@link Variant} to be casted.
     * @return @link BaseVariantController subclass, which is also subclass of {@link TitledPane} and can be displayed
     * as a content within this {@link DataController}.
     */
    private TitledPane getVariantController(VariantMode mode, Variant variant) throws IOException {
        switch (mode) {
            case MENDELIAN:
                return new MendelianVariantController((MendelianVariant) variant, choiceBasket);
            case SPLICING:
                return new SplicingVariantController((SplicingVariant) variant, choiceBasket);
            case SOMATIC:
                return new SomaticVariantController((SomaticVariant) variant, choiceBasket);
            default:
                String msg = String.format("ERROR: Unknown variant mode %s\n%s", mode.name(), variant);
                LOGGER.error(msg);
                throw new IOException(msg);
        }
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


    public DiseaseCaseModel getModel() {
        return model;
    }


    /**
     * Set model instance that will be displayed by this DataController. Setting the model will bind this controller's
     * View elements to the corresponding model properties.<p> <em>Note:</em> it's also good to update current model
     * path using {@link DataController#setCurrentModelPath(File)} after setting a new model here.
     *
     * @param model {@link DiseaseCaseModel} instance.
     */
    public void setModel(DiseaseCaseModel model) {
        this.model = model;

        initializeBindings(model);
        loadVariants(model.getVariants());
    }


    /**
     * Create bindings between values of model attributes and GUI elements to ensure that updating of any value is
     * synchronized in model & GUI elements. Configure autocompletions to text fields.
     *
     * @param model {@link DiseaseCaseModel} instance to be bound with GUI elements.
     */
    private void initializeBindings(DiseaseCaseModel model) {

        // Current model title, this is "readOnly" binding
        currentModelLabel.textProperty().bind(model.getPublication().titleProperty());

        // Genome build
        model.setGenomeBuild(convertGenomeBuild(model));
        genomeBuildComboBox.valueProperty().bindBidirectional(model.genomeBuildProperty());

        // Gene
        entrezIDTextField.textProperty().bindBidirectional(model.getTargetGene().entrezIDProperty());
        geneSymbolTextField.textProperty().bindBidirectional(model.getTargetGene().geneNameProperty());

        // Disease
        diseaseDatabaseComboBox.valueProperty().bindBidirectional(model.getDisease().databaseProperty());
        diseaseIDTextField.textProperty().bindBidirectional(model.getDisease().diseaseIdProperty());
        diseaseNameTextField.textProperty().bindBidirectional(model.getDisease().diseaseNameProperty());

        // Proband & family
        probandFamilyTextField.textProperty().bindBidirectional(model.getFamilyInfo().familyOrPatientIDProperty());
        sexComboBox.valueProperty().bindBidirectional(model.getFamilyInfo().sexProperty());
        ageTextField.textProperty().bindBidirectional(model.getFamilyInfo().ageProperty());

        // Biocurator
        biocuratorIdTextField.textProperty().bind(model.getBiocurator().bioCuratorIdProperty());

        // Metadata
        metadataTextArea.textProperty().bindBidirectional(model.getMetadata().metadataTextProperty());
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
                variantsAccordion.getPanes()
                        // variant controller is also TitledPane.
                        .add(getVariantController(getVariantMode(variant), variant));
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
        Optional<String> modeName = PopUps.getToggleChoiceFromUser(VariantMode.getNames(),
                "Select variant type:", conversationTitle);
        modeName.ifPresent(mn -> {
            VariantMode mode = VariantMode.valueOf(mn);
            Variant variant;

            switch (mode) {
                case MENDELIAN:
                    variant = new MendelianVariant();
                    break;
                case SOMATIC:
                    variant = new SomaticVariant();
                    break;
                case SPLICING:
                    variant = new SplicingVariant();
                    break;
                default:
                    throw new IllegalArgumentException(String.format("ERROR: Unknown mode %s", mode));
            }
            // Pass reference to new Variant into Model and into View
            try {
                TitledPane pane = getVariantController(mode, variant);
                variantsAccordion.getPanes().add(pane);
                model.getVariants().add(variant);
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
        if (expanded != null) {
            variantsAccordion.getPanes().remove(expanded); // remove variant from the View
            Variant toBeRemoved;
            switch (expanded.getText()) {
                case "SPLICING":
                    toBeRemoved = ((SplicingVariantController) expanded).getVariant();
                    break;
                case "MENDELIAN":
                    toBeRemoved = ((MendelianVariantController) expanded).getVariant();
                    break;
                case "SOMATIC":
                    toBeRemoved = ((SomaticVariantController) expanded).getVariant();
                    break;
                default:
                    throw new IllegalStateException(String.format("Invalid variant mode %s", expanded.getText()));
            }
            if (!model.getVariants().remove(toBeRemoved)) { // and also from the Model.
                LOGGER.warn(String.format("Unable to remove variant %s from the model instance.", toBeRemoved));
            }
        }

    }


    @FXML
    void hpoTextMiningButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Main.fxml"), resourceBundle,
                    new JavaFXBuilderFactory(), injector::getInstance);
            Parent parent = loader.load();
            Main main = loader.getController();
            main.setPhenotypeTerms(model.getHpoList().stream()
                    .map(hpo ->
                            new Main.PhenotypeTerm(optionalResources.getOntology().getTerm(hpo.getHpoId()),
                                    hpo.getObserved().equals("YES")))
                    .collect(Collectors.toSet()));
            Stage stage = new Stage();
            stage.initOwner(hpoTextMiningButton.getParent().getScene().getWindow());
            stage.setScene(new Scene(parent));
            stage.showAndWait();
            // wait until user finishes
            model.getHpoList().clear();
            model.getHpoList().addAll(main.getPhenotypeTerms().stream()
                    .map(term -> new HPO(term.getHpoId(), term.getName(), (term.isPresent()) ? "YES" : "NOT"))
                    .collect(Collectors.toSet()));
        } catch (MalformedURLException e) {
            LOGGER.warn("HPO text mining url is in wrong format", e);
        } catch (IOException e) {
            LOGGER.warn("Unable to perform text mining", e);
            e.printStackTrace();
        }

    }


    /**
     * Parse inputted PubMed string and set Publication info to the model.
     */
    @FXML
    void inputPubMedDataButton() {
        String conversationTitle = "PubMed text parse";
        String pubMedText = inputPubMedDataTextField.getText();

        PubMedParser parser = new PubMedParser();
        Optional<Publication> pub = parser.parsePubMed(pubMedText);
        if (!pub.isPresent()) {
            PopUps.showInfoMessage(parser.getErrorString(), conversationTitle);
            return;
        }

        PubMedValidator validator = new PubMedValidator(new XMLModelParser(optionalResources.getDiseaseCaseDir()));
        Publication publication = pub.get();
        if (validator.seenThisPMIDBefore(publication.getPmid())) {
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
        if (!model.getPublication().isEmpty() && !model.getPublication().equals(publication)) {
            Optional<String> choice = PopUps.getToggleChoiceFromUser(new String[]{"UPDATE", "NEW"}, "You entered new" +
                            " publication data. Do you wish to UPDATE current data or create a NEW file?",
                    conversationTitle);
            if (!choice.isPresent())
                return;

            if (choice.get().equals("NEW")) {
                setModel(new DiseaseCaseModel());
                setCurrentModelPath(null);
            }
        }

        model.setPublication(publication);
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
     * New empty {@link DiseaseCaseModel} is implicitly created here. </p>
     */
    public void initialize() {
        genomeBuildComboBox.getItems().addAll(choiceBasket.getGenomeBuild());
        diseaseDatabaseComboBox.getItems().addAll(choiceBasket.getDiseaseDatabases());
        sexComboBox.getItems().addAll(choiceBasket.getSex());

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

}
