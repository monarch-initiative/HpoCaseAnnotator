package org.monarchinitiative.hpo_case_annotator.io.v2.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.monarchinitiative.hpo_case_annotator.io.ModelParser;
import org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize.*;
import org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize.variant.*;
import org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.*;
import org.monarchinitiative.svart.GenomicAssembly;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

public class JsonStudyParser implements ModelParser<Study> {

    // TODO - this should be a singleton

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
        module.addDeserializer(SomaticVariantMetadata.class, new SomaticVariantMetadataDeserializer());
        module.addDeserializer(SplicingVariantMetadata.class, new SplicingVariantMetadataDeserializer());
        module.addDeserializer(StructuralVariantMetadata.class, new StructuralVariantMetadataDeserializer());

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

    @Override
    public void write(Study model, OutputStream outputStream) throws IOException {
        objectMapper.writeValue(outputStream, model);
    }

    @Override
    public Study read(InputStream inputStream) throws IOException {
        return objectMapper.readValue(inputStream, Study.class);
    }

    @Override
    public Collection<File> getModelNames() {
        throw new RuntimeException("Getting model names is not supported anymore");
    }
}
