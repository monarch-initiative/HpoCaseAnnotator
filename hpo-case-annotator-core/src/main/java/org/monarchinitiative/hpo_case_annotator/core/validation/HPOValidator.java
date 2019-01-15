package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.monarchinitiative.hpo_case_annotator.model.proto.OntologyClass;
import org.monarchinitiative.hpo_case_annotator.model.xml_model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.model.xml_model.HPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This validator is supposed to validate HPO terms (e.g. that id & label match together).
 */
public class HPOValidator implements Validator<OntologyClass> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HPOValidator.class);

    private Map<String, HPO> hpoMap;


    public HPOValidator(String pathToHpoFile) {
        // use
//        HPOParser hpoParser = new HPOParser(this.settings.getHpOBOPath());
//        this.hpoMap = hpoParser.getHpoMap();
    }


//    public HPOValidator(Ontology ontology) {

//    }


    /**
     * This function checks that the mutation object (which was derived from an
     * XML file for a published mutation) has a valid set of HPO terms. It basically
     * checks that the HPO:id corresponds to tghe HPO:name for each of the terms. In the
     * XML file, it is required to enter both id and name, and these are checked here
     * against the hp.obo file. This is just for Q/C (sanity check).
     */
//    @Override
    public ValidationResult validateDiseaseCase(DiseaseCaseModel model) {
        return null;
//        return makeValidationResult(ValidationResult.UNAPPLICABLE, "HPO validation currently unapplicable");
        //        List<HPO> hpoList = model.getHpoList();
//
//        for (HPO term : hpoList) {
//            String testhpId = term.getHpoId();
//            String testhpName = term.getHpoName();
//            HPO refHpo = hpoMap.get(testhpId);
//            if (refHpo==null) {
//                setErrorMessage("HPO id is not present in current HP.obo file");
//                return ValidationResult.FAILED;
//            }
//            if (!testhpName.equals(refHpo.getHpoName())) {
//                setErrorMessage("HPO id and HPO name don't match");
//                return ValidationResult.FAILED;
//            }
//        }
//        setErrorMessage(OKAY);
//        return ValidationResult.PASSED;
    }

    @Override
    public List<ValidationResult> validate(OntologyClass instance) {
        // TODO - implement HPO checking.
        LOGGER.warn("HPOValidator is not yet implemented");
        return Collections.emptyList();
    }
}
