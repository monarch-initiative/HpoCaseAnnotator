package org.monarchinitiative.hpo_case_annotator.model;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

/**
 * Bean with data used to populate content of FXML (view) elements. LegacyContentController is parsed from YAML file.
 *
 * @author Daniel Danis
 */
public class ChoiceBasket {

    private List<String> genomeBuild;

    private List<String> chromosome;

    private List<String> genotype;

    private List<String> variantClass;

    private List<String> pathomechanism;

    private List<String> consequence;

    private List<String> crypticSpliceSiteType;

    private List<String> reporter;

    private List<String> emsa;

    private List<String> otherChoices;

    private List<String> otherEffect;

    private List<String> diseaseDatabases;

    private List<String> cosegregation;

    private List<String> comparability;

    private List<String> sex;


    private ChoiceBasket() { //private no-op ;)
    }

//    private static void checkFilePresence() {
//        Optional<File> destinationFile = LegacyHRMDResourceManager.getParametersFile();
//        if (! destinationFile.exists() ) {
//                FileChannel source = null;
//                FileChannel destination = null;
//            try {
//                source = new FileInputStream(new File(defaultPath)).getChannel();
//                destination = new FileOutputStream(destinationFile).getChannel();
//                destination.transferFrom(source, 0, source.size());
//            } catch (IOException ioe) {
//                Alert alert = new Alert(Alert.AlertType.ERROR);
//                alert.setTitle("HRMD initialization");
//                PopUps.showException("HRMD initialization",
//                        "Error",
//                        String.format("Error occurred while copying of %s file to %s.", defaultPath, destinationFile.getAbsolutePath()),
//                        ioe);
//            } finally {
//                try {
//                    if (source != null) {
//                        source.close();
//                    }
//                    if (destination != null) {
//                        destination.close();
//                    }
//                } catch (IOException ioe) {
//                    // swallow
//                }
//            }
//        }
//    }


    /**
     * Parse given YAML file into {@link ChoiceBasket} object.
     *
     * @param where {@link File} object with path to YAML file.
     * @return {@link ChoiceBasket} populated with content of YAML file.
     */
    public static ChoiceBasket loadChoices(File where) {
//        checkFilePresence();
        ChoiceBasket pp = new ChoiceBasket();

        Yaml yaml = new Yaml();
        Map<String, List<String>> magicTrick = null;
        try {
            // TODO - do this "magical" parsing somehow better
            magicTrick = (Map<String, List<String>>) yaml.load(new BufferedReader(new FileReader(where)));
        } catch (FileNotFoundException fnfe) {
            System.out.println(String.format("ERROR: Problem parsing params YAML file from location %s",
                    where.getAbsolutePath()));
            System.out.println(fnfe.getMessage());
            fnfe.printStackTrace();
        }

        pp.setGenomeBuild(magicTrick.get("genomeBuild"));
        pp.setChromosome(magicTrick.get("chromosome"));
        pp.setGenotype(magicTrick.get("genotype"));
        pp.setVariantClass(magicTrick.get("variantClass"));
        pp.setPathomechanism(magicTrick.get("pathomechanism"));
        pp.setConsequence(magicTrick.get("consequence"));
        pp.setCrypticSpliceSiteType(magicTrick.get("crypticSpliceSiteType"));
        pp.setReporter(magicTrick.get("reporter"));
        pp.setEmsa(magicTrick.get("emsa"));
        pp.setOtherChoices(magicTrick.get("otherChoices"));
        pp.setOtherEffect(magicTrick.get("otherEffect"));
        pp.setDiseaseDatabases(magicTrick.get("diseaseDatabases"));
        pp.setCosegregation(magicTrick.get("cosegregation"));
        pp.setComparability(magicTrick.get("comparability"));
        pp.setSex(magicTrick.get("sex"));

        return pp;
    }


    public List<String> getGenomeBuild() {
        return genomeBuild;
    }


    public void setGenomeBuild(List<String> genomeBuild) {
        this.genomeBuild = genomeBuild;
    }


    public List<String> getChromosome() {
        return chromosome;
    }


    public void setChromosome(List<String> chromosome) {
        this.chromosome = chromosome;
    }


    public List<String> getGenotype() {
        return genotype;
    }


    public void setGenotype(List<String> genotype) {
        this.genotype = genotype;
    }


    public List<String> getVariantClass() {
        return variantClass;
    }


    public void setVariantClass(List<String> variantClass) {
        this.variantClass = variantClass;
    }


    public List<String> getPathomechanism() {
        return pathomechanism;
    }


    public void setPathomechanism(List<String> pathomechanism) {
        this.pathomechanism = pathomechanism;
    }


    public List<String> getConsequence() {
        return consequence;
    }


    public void setConsequence(List<String> consequence) {
        this.consequence = consequence;
    }


    public List<String> getCrypticSpliceSiteType() {
        return crypticSpliceSiteType;
    }


    public void setCrypticSpliceSiteType(List<String> crypticSpliceSiteType) {
        this.crypticSpliceSiteType = crypticSpliceSiteType;
    }


    public List<String> getReporter() {
        return reporter;
    }


    public void setReporter(List<String> reporter) {
        this.reporter = reporter;
    }


    public List<String> getEmsa() {
        return emsa;
    }


    public void setEmsa(List<String> emsa) {
        this.emsa = emsa;
    }


    public List<String> getOtherChoices() {
        return otherChoices;
    }


    public void setOtherChoices(List<String> otherChoices) {
        this.otherChoices = otherChoices;
    }


    public List<String> getOtherEffect() {
        return otherEffect;
    }


    public void setOtherEffect(List<String> otherEffect) {
        this.otherEffect = otherEffect;
    }


    public List<String> getDiseaseDatabases() {
        return diseaseDatabases;
    }


    public void setDiseaseDatabases(List<String> diseaseDatabases) {
        this.diseaseDatabases = diseaseDatabases;
    }


    public List<String> getCosegregation() {
        return cosegregation;
    }


    public void setCosegregation(List<String> cosegregation) {
        this.cosegregation = cosegregation;
    }


    public List<String> getComparability() {
        return comparability;
    }


    public void setComparability(List<String> comparability) {
        this.comparability = comparability;
    }


    public List<String> getSex() {
        return sex;
    }


    public void setSex(List<String> sex) {
        this.sex = sex;
    }
}
