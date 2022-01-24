package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Publication;

import java.io.IOException;

public class PublicationSerializer extends StdSerializer<Publication> {

    public PublicationSerializer() {
        this(Publication.class);
    }

    public PublicationSerializer(Class<Publication> t) {
        super(t);
    }

    @Override
    public void serialize(Publication publication, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        // authors
        gen.writeArrayFieldStart("authors");
        for (String author : publication.authors())
            gen.writeString(author);
        gen.writeEndArray();

        // title, journal, year, volume, pages, pmid
        gen.writeStringField("title", publication.title());
        gen.writeStringField("journal", publication.journal());
        gen.writeNumberField("year", publication.year());
        gen.writeStringField("volume", publication.volume());
        gen.writeStringField("pages", publication.pages());
        gen.writeStringField("pmid", publication.pmid());

        gen.writeEndObject();
    }
}
