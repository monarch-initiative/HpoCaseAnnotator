package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.VitalStatus;

import java.io.IOException;

public class VitalStatusSerializer extends StdSerializer<VitalStatus> {


    public VitalStatusSerializer() {
        this(VitalStatus.class);
    }

    public VitalStatusSerializer(Class<VitalStatus> t) {
        super(t);
    }

    @Override
    public void serialize(VitalStatus value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField("status", value.getStatus().name());
        gen.writeObjectField("timeOfDeath", value.getTimeOfDeath());
    }
}
