package org.monarchinitiative.hpo_case_annotator.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Bean with data used to populate content of FXML (view) elements. LegacyContentController is parsed from YAML file.
 *
 * @author Daniel Danis
 */
public class ChoiceBasket {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChoiceBasket.class);

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


    public ChoiceBasket(InputStream where) {
        Yaml yaml = new Yaml();
        Map<String, List<String>> magicTrick = null;

        // TODO - do this "magical" parsing somehow better
        magicTrick = (Map<String, List<String>>) yaml.load(where);


        setGenomeBuild(magicTrick.get("genomeBuild"));
        setChromosome(magicTrick.get("chromosome"));
        setGenotype(magicTrick.get("genotype"));
        setVariantClass(magicTrick.get("variantClass"));
        setPathomechanism(magicTrick.get("pathomechanism"));
        setConsequence(magicTrick.get("consequence"));
        setCrypticSpliceSiteType(magicTrick.get("crypticSpliceSiteType"));
        setReporter(magicTrick.get("reporter"));
        setEmsa(magicTrick.get("emsa"));
        setOtherChoices(magicTrick.get("otherChoices"));
        setOtherEffect(magicTrick.get("otherEffect"));
        setDiseaseDatabases(magicTrick.get("diseaseDatabases"));
        setCosegregation(magicTrick.get("cosegregation"));
        setComparability(magicTrick.get("comparability"));
        setSex(magicTrick.get("sex"));
    }


    public ChoiceBasket(File where) throws FileNotFoundException {
        this(new FileInputStream(where));
    }


    public ChoiceBasket(URL url) throws IOException {
        this(url.openStream());

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
