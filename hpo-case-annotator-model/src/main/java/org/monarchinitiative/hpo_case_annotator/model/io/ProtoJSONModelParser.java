package org.monarchinitiative.hpo_case_annotator.model.io;

import com.google.protobuf.util.JsonFormat;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class ProtoJSONModelParser implements ModelParser {

    public static final String MODEL_SUFFIX = ".json";

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtoJSONModelParser.class);

    private static final JsonFormat.Printer JSON_PRINTER = JsonFormat.printer();

    private static final JsonFormat.Parser JSON_PARSER = JsonFormat.parser();

    private final Path modelDir;

    private final Charset charset;


    /**
     * Create parser with <code>UTF-8</code> charset
     *
     * @param modelDir
     */
    public ProtoJSONModelParser(Path modelDir) {
        this(modelDir, Charset.forName("UTF-8"));
    }


    /**
     * @param modelDir {@link Path} to directory with JSON data files
     * @param charset
     */
    public ProtoJSONModelParser(Path modelDir, Charset charset) {
        this.modelDir = modelDir;
        this.charset = charset;
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


    public static Optional<DiseaseCase> readDiseaseCase(InputStream is) {
        try (Reader reader = new InputStreamReader(is)) {
            return readDiseaseCase(reader);
        } catch (IOException e) {
            LOGGER.warn("Unable to read the data", e);
            return Optional.empty();
        }
    }

    public static Optional<DiseaseCase> readDiseaseCase(Reader reader) {
        try {
            DiseaseCase.Builder builder = DiseaseCase.newBuilder();
            JSON_PARSER.merge(reader, builder);

            // To maintain compatibility with the older data the VariantPosition is populated if it does not exist.
            for (Variant.Builder varBuilder : builder.getVariantBuilderList()) {
                if (varBuilder.getVariantPosition().equals(VariantPosition.getDefaultInstance())) {
                    varBuilder.setVariantPosition(VariantPosition.newBuilder()
//                            .setGenomeAssembly(DiseaseCaseToDiseaseCaseModelCodec.convertGenomeAssemblyString(builder.getGenomeBuild()))
                            .setContig(varBuilder.getContig())
                            .setPos(varBuilder.getPos())
                            .setRefAllele(varBuilder.getRefAllele())
                            .setAltAllele(varBuilder.getAltAllele())
                            .build());
                }
            }
            return Optional.of(builder.build());
        } catch (IOException e) {
            LOGGER.warn("Unable to decode the data", e);
            return Optional.empty();
        }
    }


    @Override
    public void saveModel(OutputStream outputStream, DiseaseCase model) throws IOException {
        saveDiseaseCase(outputStream, model, charset);
    }


    @Override
    public Optional<DiseaseCase> readModel(InputStream inputStream) {
        return readDiseaseCase(inputStream);
    }


    public Collection<File> getModelNames() {
        if (modelDir == null) {
            LOGGER.warn("Unset model directory. Returning empty set of model names");
            return Collections.emptySet();
        }
        File[] files = modelDir.toFile().listFiles(f -> f.getName().endsWith(MODEL_SUFFIX));
        if (files == null) {
            return new HashSet<>();
        }
        return Arrays.asList(files);
    }
}
