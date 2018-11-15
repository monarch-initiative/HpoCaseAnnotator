package org.monarchinitiative.hpo_case_annotator.core.io;


import com.google.common.collect.ImmutableList;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Utils;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
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
import java.util.stream.Collectors;

public class PhenopacketExporter {

    DiseaseCase model = null;

    private File pathToFileToSave = null;


    public PhenopacketExporter(File wheretosave, DiseaseCase mod) {
        this.pathToFileToSave = wheretosave;
        this.model = mod;
    }


    /** To do -- choose appropriate HPO Terms for this! */
    private DiseaseOccurrence getDiseaseStage() {
        DiseaseStage stage = new DiseaseStage();
        String age = String.valueOf(this.model.getFamilyInfo().getAge());
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
        person.setId(model.getFamilyInfo().getFamilyOrProbandId());
        person.setLabel(model.getFamilyInfo().getFamilyOrProbandId());
        person.setSex(model.getFamilyInfo().getSex().toString());
        return person;
    }


    private Phenotype getDiseasePhenotype() {
        Phenotype diseasePhenotype = new Phenotype();
        List<OntologyClass> oclist = model.getPhenotypeList().stream()
                .map(oc -> new OntologyClass.Builder(oc.getId()).setLabel(oc.getLabel()).build())
                .collect(Collectors.toList());

        diseasePhenotype.setTypes(ImmutableList.copyOf(oclist));
        return diseasePhenotype;
    }


    private List<org.phenopackets.api.model.entity.Variant> getVariants() {

        List<Variant> varlist = this.model.getVariantList();
        List<org.phenopackets.api.model.entity.Variant> ppvarlist = new ArrayList<>();

        for (Variant var : varlist) {
            org.phenopackets.api.model.entity.Variant v = new org.phenopackets.api.model.entity.Variant();
            v.setChromosome(var.getContig());
            v.setRefBases(var.getRefAllele());
            v.setAltBases(var.getAltAllele());
            try {
                int pos = var.getPos();
                v.setStartPosition(pos);
                String ra = var.getRefAllele();
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
        String title = Utils.getFirstAuthorsSurname(this.model.getPublication()) +
                "-" + model.getPublication().getYear() +
                "-" + model.getGene().getSymbol();
        String age = String.valueOf(model.getFamilyInfo().getAge());
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
