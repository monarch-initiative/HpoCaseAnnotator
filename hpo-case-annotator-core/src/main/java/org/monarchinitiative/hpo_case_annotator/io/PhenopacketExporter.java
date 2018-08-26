package org.monarchinitiative.hpo_case_annotator.io;


import com.google.common.collect.ImmutableList;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.model.HPO;
import org.monarchinitiative.hpo_case_annotator.model.Variant;
import org.phenopackets.api.PhenoPacket;
import org.phenopackets.api.io.JsonGenerator;
import org.phenopackets.api.model.association.PhenotypeAssociation;
import org.phenopackets.api.model.condition.DiseaseOccurrence;
import org.phenopackets.api.model.condition.DiseaseStage;
import org.phenopackets.api.model.condition.Phenotype;
import org.phenopackets.api.model.entity.Person;
import org.phenopackets.api.model.ontology.OntologyClass;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhenopacketExporter {

    DiseaseCaseModel model = null;

    private File pathToFileToSave = null;


    public PhenopacketExporter(File wheretosave, DiseaseCaseModel mod) {
        this.pathToFileToSave = wheretosave;
        this.model = mod;
    }


    /** To do -- choose appropriate HPO Terms for this! */
    private DiseaseOccurrence getDiseaseStage() {
        DiseaseStage stage = new DiseaseStage();
        String age = this.model.getFamilyInfo().getAge();
        stage.setDescription(age);
        stage.setTypes(ImmutableList.of(
                OntologyClass.of("HP:0040279", "Onset")
        ));
        DiseaseOccurrence occurence = new DiseaseOccurrence();
        occurence.setStage(stage);
        return occurence;
    }


    private org.phenopackets.api.model.entity.Disease getDisease() {
        org.phenopackets.api.model.entity.Disease disease = new org.phenopackets.api.model.entity.Disease();
        disease.setId(this.model.getDisease().getDiseaseId());
        disease.setLabel(this.model.getDisease().getDiseaseName());
        // does not compile  disease.setTypes(ImmutableList.of(OntologyClass.of("EFO:0000508", "genetic disorder")));
        return disease;
    }


    private Person getPerson() {
        Person person = new Person();
        person.setId(model.getFamilyInfo().getFamilyOrPatientID());
        person.setLabel(model.getFamilyInfo().getFamilyOrPatientID());
        person.setSex(model.getFamilyInfo().getSex());
        return person;
    }


    private Phenotype getDiseasePhenotype() {
        Phenotype diseasePhenotype = new Phenotype();
        List<HPO> hpolist = model.getHpoList();
        List<OntologyClass> oclist = new ArrayList<>();

        for (HPO hpo : hpolist) {
            OntologyClass oc = new OntologyClass.Builder(hpo.getHpoId()).setLabel(hpo.getHpoName()).build();
            oclist.add(oc);
        }
        diseasePhenotype.setTypes(ImmutableList.copyOf(oclist));
        return diseasePhenotype;
    }


    private List<org.phenopackets.api.model.entity.Variant> getVariants() {

        List<Variant> varlist = this.model.getVariants();
        List<org.phenopackets.api.model.entity.Variant> ppvarlist = new ArrayList<>();

        for (Variant var : varlist) {
            org.phenopackets.api.model.entity.Variant v = new org.phenopackets.api.model.entity.Variant();
            v.setChromosome(var.getChromosome());
            v.setRefBases(var.getReferenceAllele());
            v.setAltBases(var.getAlternateAllele());
            String posstring = var.getPosition();
            try {
                Integer pos = Integer.parseInt(posstring);
                v.setStartPosition(pos);
                String ra = var.getReferenceAllele();
                int endpos = pos + ra.length() - 1; /* todo really, are we sure? */
                v.setEndPosition(endpos);
            } catch (NumberFormatException e) {
                e.printStackTrace(); /* todo feedback to GUI */
            }

            v.setAssembly(model.getGenomeBuild());
            // TODO use Jannovar to get HGVS
            //v.getDescriptionHGVS(var.)
            ppvarlist.add(v);
        }


        return ppvarlist;
    }


    public PhenoPacket createPhenopacketFromDiseaseCaseModel() {
        String title = this.model.getPublication().getFirstAuthorSurname() +
                "-" + model.getPublication().getYear() +
                "-" + model.getTargetGene().getGeneName();
        String age = model.getFamilyInfo().getAge();
        /* Add disease occurence (onset) information */
        DiseaseOccurrence occurrence = getDiseaseStage();
        org.phenopackets.api.model.entity.Disease disease = getDisease();
        Person person = getPerson();
        //DiseaseOccurrenceAssociation association = new DiseaseOccurrenceAssociation.Builder(occurrence).
        Phenotype phenotype = getDiseasePhenotype();
        PhenotypeAssociation association = new PhenotypeAssociation.Builder(phenotype).setEntity(person).build();
        List<org.phenopackets.api.model.entity.Variant> varlist = getVariants();
        PhenoPacket packet = new PhenoPacket.Builder().title(title).addDisease(disease).addPerson(person).addPhenotypeAssociation(association).setVariants(varlist).build();


        return packet;
    }


    public void writeToPhenopacket() {
        PhenoPacket packet = createPhenopacketFromDiseaseCaseModel();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.pathToFileToSave))) {
//            System.out.println(JsonGenerator.render(packet));
            bw.write(JsonGenerator.render(packet));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
