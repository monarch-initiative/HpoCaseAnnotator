package org.monarchinitiative.hpo_case_annotator.io.v1;

import com.google.protobuf.util.JsonFormat;
import org.monarchinitiative.hpo_case_annotator.io.ModelParser;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantPosition;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class ProtoJSONModelParser implements ModelParser<DiseaseCase> {

    private static final JsonFormat.Printer JSON_PRINTER = JsonFormat.printer();

    private static final JsonFormat.Parser JSON_PARSER = JsonFormat.parser();

    private static final ProtoJSONModelParser INSTANCE = new ProtoJSONModelParser();

    private ProtoJSONModelParser() {
    }

    public static ProtoJSONModelParser getInstance() {
        return INSTANCE;
    }

    public static void saveDiseaseCase(OutputStream os, DiseaseCase model, Charset charset) throws IOException {
        // ----------- clear deprecated fields when saving data ---------------
        // genome build from model
        DiseaseCase.Builder builder = model.toBuilder().clearGenomeBuild();

        // contig, pos, refAllele, and altAllele from variants
        final List<Variant> updatedVariants = builder.getVariantBuilderList().stream()
                .map(vb -> vb.clearContig().clearPos().clearRefAllele().clearAltAllele().build())
                .collect(Collectors.toList());

        final DiseaseCase updatedModel = builder.clearVariant().addAllVariant(updatedVariants).build();

        // ----------- save the updated model ---------------------------------
        os.write(JSON_PRINTER.print(updatedModel).getBytes(charset));
    }


    public static DiseaseCase readDiseaseCase(InputStream is) throws IOException {
        try (Reader reader = new InputStreamReader(is)) {
            return readDiseaseCase(reader);
        }
    }

    public static DiseaseCase readDiseaseCase(Reader reader) throws IOException {
        DiseaseCase.Builder builder = DiseaseCase.newBuilder();
        JSON_PARSER.merge(reader, builder);

        // To maintain compatibility with the older data the VariantPosition is populated if it does not exist.
        for (Variant.Builder varBuilder : builder.getVariantBuilderList()) {
            if (varBuilder.getVariantPosition().equals(VariantPosition.getDefaultInstance())) {
                varBuilder.setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(convertGenomeAssemblyString(builder.getGenomeBuild()))
                        .setContig(varBuilder.getContig())
                        .setPos(varBuilder.getPos())
                        .setRefAllele(varBuilder.getRefAllele())
                        .setAltAllele(varBuilder.getAltAllele())
                        .build());
            }
        }
        return builder.build();
    }

    private static GenomeAssembly convertGenomeAssemblyString(String genomeBuild) {
        return switch (genomeBuild.toUpperCase()) {
            case "HG18", "NCBI36" -> GenomeAssembly.NCBI_36;
            case "GRCH37", "HG19" -> GenomeAssembly.GRCH_37;
            case "GRCH38", "HG38" -> GenomeAssembly.GRCH_38;
            default -> throw new IllegalArgumentException("Unknown genome build: " + genomeBuild);
        };
    }

    @Override
    public void write(DiseaseCase model, OutputStream outputStream) throws IOException {
        saveDiseaseCase(outputStream, model, StandardCharsets.UTF_8);
    }


    @Override
    public DiseaseCase read(InputStream inputStream) throws IOException {
        return readDiseaseCase(inputStream);
    }
}
