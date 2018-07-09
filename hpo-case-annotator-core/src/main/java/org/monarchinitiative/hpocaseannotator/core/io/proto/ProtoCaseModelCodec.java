package org.monarchinitiative.hpocaseannotator.core.io.proto;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.JsonFormat;
import org.monarchinitiative.hpocaseannotator.core.io.CaseModelCodec;
import org.monarchinitiative.hpocaseannotator.core.model.proto.CaseModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * This codec serializes/deserializes {@link CaseModel} in JSON format.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public final class ProtoCaseModelCodec implements CaseModelCodec {

    /**
     * Charset used to encode and decode data into bytes.
     */
    private final Charset charset;


    /**
     * @param charset {@link Charset} used to encode and decode data
     */
    public ProtoCaseModelCodec(Charset charset) {
        this.charset = charset;
    }


    /**
     * Create codec that uses default <code>UTF-8</code> charset to encode and decode data.
     */
    public ProtoCaseModelCodec() {
        this(Charset.forName("UTF-8"));
    }


    @Override
    public CaseModel decode(InputStream inputStream) throws IOException {
        // decode content of the InputStream into CaseModel instance
        CaseModel.Builder builder = CaseModel.newBuilder();
        JsonFormat.parser().merge(new InputStreamReader(inputStream, charset), builder);
        return builder.build();
    }


    @Override
    public void encode(CaseModel caseModel, OutputStream outputStream, boolean updateTimestamp) throws IOException {
        if (updateTimestamp) {
            long timestamp = System.currentTimeMillis();
            CaseModel stamped = CaseModel.newBuilder(caseModel)
                    .setUpdated(Timestamp.newBuilder()
                            .setSeconds(timestamp / 1000)
                            .setNanos(Math.toIntExact(timestamp % 1000))
                            .build())
                    .build();
            outputStream.write(JsonFormat.printer().print(stamped).getBytes(charset));
        } else
            outputStream.write(JsonFormat.printer().print(caseModel).getBytes(charset));

    }
}
