package org.monarchinitiative.hpo_case_annotator.io.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.monarchinitiative.hpo_case_annotator.io.json.deserialize.*;
import org.monarchinitiative.hpo_case_annotator.io.json.deserialize.variant.CuratedVariantDeserializer;
import org.monarchinitiative.hpo_case_annotator.io.json.deserialize.variant.MendelianVariantMetadataDeserializer;
import org.monarchinitiative.hpo_case_annotator.io.json.deserialize.variant.SplicingVariantMetadataDeserializer;
import org.monarchinitiative.hpo_case_annotator.io.json.deserialize.variant.StructuralVariantMetadataDeserializer;
import org.monarchinitiative.hpo_case_annotator.io.json.serialize.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.MendelianVariantMetadata;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.SplicingVariantMetadata;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.StructuralVariantMetadata;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.svart.GenomicAssembly;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonStudyParser {

    private static final Version VERSION = new Version(2, 0, 0, null, null, null);
    private final ObjectMapper objectMapper;

    public JsonStudyParser(GenomicAssembly hg19, GenomicAssembly hg38) {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        SimpleModule module = new SimpleModule("StudyParser", VERSION);

        for (JsonSerializer<?> serializer : serializers())
            module.addSerializer(serializer);

        module.addDeserializer(AgeRange.class, new AgeRangeDeserializer());
        module.addDeserializer(Disease.class, new DiseaseDeserializer());
        module.addDeserializer(EditHistory.class, new EditHistoryDeserializer());
        module.addDeserializer(Individual.class, new IndividualDeserializer());
        module.addDeserializer(PhenotypicFeature.class, new PhenotypicFeatureDeserializer());
        module.addDeserializer(PhenotypicObservation.class, new PhenotypicObservationDeserializer());
        module.addDeserializer(Pedigree.class, new PedigreeDeserializer());
        module.addDeserializer(PedigreeMember.class, new PedigreeMemberDeserializer());
        module.addDeserializer(Publication.class, new PublicationDeserializer());
        module.addDeserializer(Study.class, new StudyDeserializer());
        module.addDeserializer(StudyMetadata.class, new StudyMetadataDeserializer());

        module.addDeserializer(CuratedVariant.class, new CuratedVariantDeserializer(hg19, hg38));
        module.addDeserializer(VariantMetadata.class, new VariantMetadataDeserializer());
        module.addDeserializer(MendelianVariantMetadata.class, new MendelianVariantMetadataDeserializer());
        module.addDeserializer(SplicingVariantMetadata.class, new SplicingVariantMetadataDeserializer());
        module.addDeserializer(StructuralVariantMetadata.class, new StructuralVariantMetadataDeserializer());
        // TODO - add the rest of deserializers

        objectMapper.registerModule(module);
    }

    private static List<JsonSerializer<?>> serializers() {
        return List.of(
                new AgeRangeSerializer(),
                new CuratedVariantSerializer(),
                new DiseaseSerializer(),
                new EditHistorySerializer(),
                new IndividualSerializer(),
                new PedigreeMemberSerializer(),
                new PedigreeSerializer(),
                new PhenotypicFeatureSerializer(),
                new PhenotypicObservationSerializer(),
                new PublicationSerializer(),
                new StudyMetadataSerializer(),
                new StudySerializer());
    }

    public String serialize(Study study) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            serialize(study, os);
            return os.toString();
        }
    }

    public void serialize(Study study, OutputStream outputStream) throws IOException {
        objectMapper.writeValue(outputStream, study);
    }

    public Study deserialize(InputStream inputStream) throws IOException {
        return objectMapper.readValue(inputStream, Study.class);
    }

    public Study deserialize(String payload) throws IOException {
        try (ByteArrayInputStream is = new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8))) {
            return deserialize(is);
        }
    }

}
