package org.monarchinitiative.hpo_case_annotator.io;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class ProtoJSONModelParser implements ModelParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtoJSONModelParser.class);

    private static final JsonFormat.Printer JSON_PRINTER = JsonFormat.printer();

    private static final JsonFormat.Parser JSON_PARSER = JsonFormat.parser();

    private final Path modelDir;

    private final Charset charset;


    public static void saveDiseaseCase(OutputStream os, DiseaseCase model, Charset charset) throws IOException {
        os.write(JSON_PRINTER.print(model).getBytes(charset));
    }


    public static DiseaseCase readDiseaseCase(InputStream is) throws IOException {
        DiseaseCase.Builder builder = DiseaseCase.newBuilder();
        JSON_PARSER.merge(new InputStreamReader(is), builder);
        return builder.build();
    }

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


    @Override
    public void saveModel(OutputStream outputStream, DiseaseCase model) throws IOException {
        saveDiseaseCase(outputStream, model, charset);
    }


    @Override
    public DiseaseCase readModel(InputStream inputStream) throws IOException {
        return readDiseaseCase(inputStream);
    }


    public Collection<File> getModelNames() {
        LOGGER.warn("Getting model names is not supported at the moment");
        return Collections.emptySet();
    }
}
