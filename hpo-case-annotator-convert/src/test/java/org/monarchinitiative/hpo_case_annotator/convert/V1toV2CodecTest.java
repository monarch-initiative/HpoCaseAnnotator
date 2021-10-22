package org.monarchinitiative.hpo_case_annotator.convert;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.time.Period;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class V1toV2CodecTest {

    @Test
    public void encodeDiseaseCase() throws ModelTransformationException {
        DiseaseCase comprehensiveCase = TestData.V1.comprehensiveCase();
        V1toV2Codec instance = V1toV2Codec.getInstance();

        Study study = instance.encode(comprehensiveCase);

        // publication
        assertThat(study.publication(), equalTo(Publication.of(
                List.of("Beygó J", "Buiting K", "Seland S", "Lüdecke HJ", "Hehr U", "Lich C", "Prager B", "Lohmann DR", "Wieczorek D"),
                "First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome",
                "Mol Syndromol",
                2012,
                "2(2)",
                "53-59",
                "22712005")));

        // metadata
        StudyMetadata metadata = study.studyMetadata();
        String freeText = """
                Authors are describing a mutations in CFTR exon 13 that appears to contain two 3'CSS utilization of which is increased when there is a mutation in ESE element present in exon 13 (Figure 2.).
                                
                The 3'CSS whose coordinates are recorded in variants is the dominant one (Figure 2. D, D248). However, there exists also another (D195) which has coordinates: 117232182, 3 splice site, CAATTTAG|TGCAGAAA .""";
        assertThat(metadata.freeText(), equalTo(freeText));
        assertThat(metadata.createdBy().curatorId(), equalTo("HPO:walterwhite"));
        assertThat(metadata.modifiedBy(), is(empty()));

        // variants
        List<CuratedVariant> variants = study.variants();
        assertThat(variants, hasSize(5));

        // TODO - test variants

        // members
        assertThat(study, instanceOf(FamilyStudy.class));
        FamilyStudy familyStudy = (FamilyStudy) study;
        List<? extends PedigreeMember> members = familyStudy.members().toList();
        assertThat(members, hasSize(1));
        assertThat(members.get(0), equalTo(PedigreeMember.of("FAM:001", "", "",
                Period.parse("P10Y5M4D"),
                List.of(DiseaseStatus.of(TermId.of("OMIM:219700"), "CYSTIC FIBROSIS; CF", false)),
                Map.of(
                        "4ff0f76632e6a0aca94996361bc29c58", Genotype.HOMOZYGOUS_ALTERNATE,
                        "b8394a48b4e1028f04930c3b739fac88", Genotype.HETEROZYGOUS,
                        "e8314b7b3d063966458a6bca6c5c3be8", Genotype.HOMOZYGOUS_ALTERNATE,
                        "4ccbca63d4212e7dc79259bc64255990", Genotype.HETEROZYGOUS,
                        "813960c422f5cc972a7a0e4099439751", Genotype.HETEROZYGOUS),
                List.of(PhenotypicObservation.of(
                        AgeRange.sinceBirthUntilAge(Period.parse("P10Y5M4D")),
                        List.of(
                                PhenotypicFeature.of(TermId.of("HP:1234567"), false),
                                PhenotypicFeature.of(TermId.of("HP:9876543"), true)
                        )
                )),
                true,
                Sex.MALE)));
    }
}