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
import org.monarchinitiative.svart.ConfidenceInterval;
import org.monarchinitiative.svart.GenomicAssemblies;
import org.monarchinitiative.svart.GenomicAssembly;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class JsonStudyParser implements ModelParser<Study> {

    private static final Version VERSION = new Version(2, 0, 0, null, null, null);

    private static final JsonStudyParser INSTANCE = of(
            // hg18
            GenomicAssembly.readAssembly(Objects.requireNonNull(JsonStudyParser.class.getResourceAsStream("GCF_000001405.12_NCBI36_assembly_report.txt"), "Missing genome hg18 assembly report file. Contact developers")),
            // hg19
            GenomicAssemblies.GRCh37p13(),
            // hg38
            GenomicAssemblies.GRCh38p13());
    
    private final ObjectMapper objectMapper;

    private JsonStudyParser(GenomicAssembly... assemblies) {
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

        module.addDeserializer(CuratedVariant.class, new CuratedVariantDeserializer(assemblies));
        module.addDeserializer(ConfidenceInterval.class, new ConfidenceIntervalDeserializer());
        module.addDeserializer(VariantMetadata.class, new VariantMetadataDeserializer());
        module.addDeserializer(MendelianVariantMetadata.class, new MendelianVariantMetadataDeserializer());
        module.addDeserializer(SomaticVariantMetadata.class, new SomaticVariantMetadataDeserializer());
        module.addDeserializer(SplicingVariantMetadata.class, new SplicingVariantMetadataDeserializer());
        module.addDeserializer(StructuralVariantMetadata.class, new StructuralVariantMetadataDeserializer());

        objectMapper.registerModule(module);
    }

    public static JsonStudyParser getInstance() {
        return INSTANCE;
    }

    public static JsonStudyParser of(GenomicAssembly... assemblies) {
        return new JsonStudyParser(assemblies);
    }

    private static List<JsonSerializer<?>> serializers() {
        return List.of(
                new AgeRangeSerializer(),
                new ConfidenceIntervalSerializer(),
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
}
