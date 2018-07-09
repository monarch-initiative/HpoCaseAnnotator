package org.monarchinitiative.hpocaseannotator.core.io;

import org.monarchinitiative.hpocaseannotator.core.model.proto.CaseModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public interface CaseModelCodec {

    CaseModel decode(InputStream reader) throws IOException;

    void encode(CaseModel caseModel, OutputStream writer, boolean updateTimestamp) throws IOException;

}
