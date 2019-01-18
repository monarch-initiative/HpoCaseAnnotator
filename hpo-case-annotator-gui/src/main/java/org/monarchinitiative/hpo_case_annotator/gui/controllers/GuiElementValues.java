package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Structure of this POJO matches structure of the YAML file <code>gui-element-values.yml</code> (formerly
 * <code>choice-basket.yml</code>) located at classpath.
 * <p>
 * The file contains options that are loaded into GUI elements (mainly {@link javafx.scene.control.ComboBox}es) that need
 * to offer to the user a set of pre-defined values.
 */
public class GuiElementValues {

    /**
     * This is an ugly way of List initialization, but it works for now.
     */
    private List<GenomeAssembly> genomeBuild;

    private List<String> chromosome;

    private List<String> variantClass;

    private List<String> pathomechanism;

    private List<String> consequence;

    private List<String> reporter;

    private List<String> emsa;

    private List<String> otherChoices;

    private List<String> otherEffect;

    private List<String> diseaseDatabases;

    private List<String> cosegregation;

    private List<String> comparability;

    private GuiElementValues() {
        genomeBuild = Arrays.stream(GenomeAssembly.values()).filter(f -> !f.equals(GenomeAssembly.UNRECOGNIZED)).collect(Collectors.toList());

    }

    public static GuiElementValues guiElementValuesFrom(InputStream is) throws IOException {
        return new ObjectMapper(new YAMLFactory()).readValue(is, GuiElementValues.class);
    }

    public List<GenomeAssembly> getGenomeBuild() {
        return genomeBuild;
    }

    public List<String> getChromosome() {
        return chromosome;
    }

    public void setChromosome(List<String> chromosome) {
        this.chromosome = chromosome;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuiElementValues that = (GuiElementValues) o;
        return Objects.equals(genomeBuild, that.genomeBuild) &&
                Objects.equals(chromosome, that.chromosome) &&
                Objects.equals(variantClass, that.variantClass) &&
                Objects.equals(pathomechanism, that.pathomechanism) &&
                Objects.equals(consequence, that.consequence) &&
                Objects.equals(reporter, that.reporter) &&
                Objects.equals(emsa, that.emsa) &&
                Objects.equals(otherChoices, that.otherChoices) &&
                Objects.equals(otherEffect, that.otherEffect) &&
                Objects.equals(diseaseDatabases, that.diseaseDatabases) &&
                Objects.equals(cosegregation, that.cosegregation) &&
                Objects.equals(comparability, that.comparability);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genomeBuild, chromosome, variantClass, pathomechanism, consequence, reporter, emsa, otherChoices, otherEffect, diseaseDatabases, cosegregation, comparability);
    }

    @Override
    public String toString() {
        return "GuiElementValues{" +
                "genomeBuild=" + genomeBuild +
                ", chromosome=" + chromosome +
                ", variantClass=" + variantClass +
                ", pathomechanism=" + pathomechanism +
                ", consequence=" + consequence +
                ", reporter=" + reporter +
                ", emsa=" + emsa +
                ", otherChoices=" + otherChoices +
                ", otherEffect=" + otherEffect +
                ", diseaseDatabases=" + diseaseDatabases +
                ", cosegregation=" + cosegregation +
                ", comparability=" + comparability +
                '}';
    }
}
