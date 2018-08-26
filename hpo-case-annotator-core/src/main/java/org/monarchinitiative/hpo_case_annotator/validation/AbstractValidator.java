package org.monarchinitiative.hpo_case_annotator.validation;

import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;

import java.util.List;

/**
 * Base class for all validators for sharing common resources and utility methods.
 */
public abstract class AbstractValidator {

    /**
     * Message presented when everything is OK.
     */
    public final String OKAY = "All right!";

    /**
     * If validation fails a message describing the failure will be stored here.
     */
    private String errorMessage;


    protected AbstractValidator() {
        // no-op, prevent instantiation through public constructor.
    }


    /**
     * Read XML files in provided directory and parse them into List with {@link org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel}
     * instances.
     *
     * @param diseaseCaseDirPath path do directory with
     * @return {@link List} containing
     */
//    @Deprecated // same functionality is in XMLModelParser
//    protected static List<DiseaseCaseModel> readDiseaseCaseModels(String diseaseCaseDirPath) {
//        File dirPath = new File(diseaseCaseDirPath);
//
//        File[] randomGenericCkorList = dirPath.listFiles();
//
//        List<DiseaseCaseModel> modelList = new ArrayList<>();
//        for (File f : randomGenericCkorList) {
//            if (f.getName().endsWith(".xml")) {
//                Optional<DiseaseCaseModel> modelOptional = XMLModelParser.loadDiseaseCaseModel(f.getAbsoluteFile());
//                modelOptional.ifPresent(modelList::add);
//            }
//        }
//        return modelList;
//    }


    /**
     * @param s {@link String} that is tested.
     * @return true if given string is either null or equals empty string ("")
     */
    protected static boolean isNullOrEmpty(String s) {
        return s == null || s.equals("");
    }


    /**
     * @param model {@link DiseaseCaseModel} instance about to be validated.
     * @return {@link ValidationResult}
     */
    public abstract ValidationResult validateDiseaseCase(DiseaseCaseModel model);


    // ************************ Getters & Setters *****************************
    public String getErrorMessage() {
        return this.errorMessage;
    }


    public void setErrorMessage(String newMsg) {
        this.errorMessage = newMsg;
    }
}
