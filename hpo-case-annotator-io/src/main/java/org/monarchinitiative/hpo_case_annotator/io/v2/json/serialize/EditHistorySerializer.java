package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.EditHistory;

import java.io.IOException;

public class EditHistorySerializer extends StdSerializer<EditHistory> {

    public EditHistorySerializer() {
        this(EditHistory.class);
    }

    public EditHistorySerializer(Class<EditHistory> t) {
        super(t);
    }

    @Override
    public void serialize(EditHistory editHistory, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("curatorId", editHistory.curatorId());
        gen.writeStringField("softwareVersion", editHistory.softwareVersion());
        gen.writeStringField("timestamp", editHistory.timestamp().toString());
        gen.writeEndObject();
    }
}
