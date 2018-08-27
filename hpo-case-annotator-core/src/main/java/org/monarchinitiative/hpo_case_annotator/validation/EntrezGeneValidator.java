package org.monarchinitiative.hpo_case_annotator.validation;

import org.monarchinitiative.hpo_case_annotator.io.EntrezParser;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.model.TargetGene;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * This validator is supposed to check that the entered gene information is correct (e.g. gene id matches gene symbol).
 */
public class EntrezGeneValidator extends AbstractValidator {

    /** Key: Entrez ID; value: TargetGene bean */
    private Map<Integer, TargetGene> entrezId2gene = null;


    /**
     * Create instance of validator which will use provided entrez GTF file.
     *
     * @param pathToEntrezFile path to Entrez GTF file.
     */
    public EntrezGeneValidator(File pathToEntrezFile) throws IOException {
        EntrezParser parser = new EntrezParser(pathToEntrezFile);
        parser.readFile();
        this.entrezId2gene = parser.getEntrezMap();
    }


    /**
     * Test that the Entrez gene id & symbol match. Then test that the gene is on the correct chromosome.
     */
    @Override
    public ValidationResult validateDiseaseCase(DiseaseCaseModel model) {

        TargetGene testGene = model.getTargetGene();
        TargetGene refGene = getEntrez(model.getTargetGene().getEntrezID());
        if (refGene == null) {
            setErrorMessage(String.format("Gene with id %s and name %s was not found in Entrez gene file",
                    model.getTargetGene().getEntrezID(), model.getTargetGene().getGeneName()));
            return ValidationResult.FAILED;
        }

        /* Since we perform lookup by ID only thing that we know for sure
         * is whether the Gene names match or not. */
        if (!testGene.getGeneName().equals(refGene.getGeneName())) {
            setErrorMessage(String.format("Gene names for ID %s do not match. Observed: %s Expected: %s",
                    testGene.getEntrezID(), testGene.getGeneName(), refGene.getGeneName()));
            return ValidationResult.FAILED;
        }

        setErrorMessage(OKAY);
        return ValidationResult.PASSED;
    }


    private TargetGene getEntrez(String id) {
        try {
            int geneid = Integer.parseInt(id);
            TargetGene egene = this.entrezId2gene.get(geneid);
            return egene; // can be null if not found
        } catch (NumberFormatException nfe) {
            System.err.println("Error parsing Entrez ID:" + id);
            nfe.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}